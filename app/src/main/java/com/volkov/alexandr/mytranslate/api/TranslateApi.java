package com.volkov.alexandr.mytranslate.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.volkov.alexandr.mytranslate.model.Dictionary;
import com.volkov.alexandr.mytranslate.model.Language;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.volkov.alexandr.mytranslate.utils.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class TranslateApi {
    private static final String LOG_TAG = makeLogTag(TranslateApi.class);

    private static final String DICTIONARY_API_KEY =
            "dict.1.1.20170714T093109Z.71f88c1da76db26f.77603d97175b9da271543cfcfed0a7e8ceeaa755";
    private static final String TRANSLATE_API_KEY =
            "trnsl.1.1.20170707T093646Z.ae8af46d0d3ae91f.fc80db8c48018769cb3736f0c75a6797f7dc818a";

    private static final String LANGS_URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=%s&ui=ru";
    private static final String TRANSLATE_URL =
            "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&lang=%s&text=%s";
    private static final String DICTIONARY_URL =
            "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=%s&lang=%s&text=%s&ui=ru";

    private static RequestQueue queue;

    public TranslateApi(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
    }

    public void getLangs(final ResponseListener<List<Language>> listener) {
        String url = String.format(LANGS_URL, TRANSLATE_API_KEY);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    List<Language> langs = new ArrayList<>();
                    try {
                        langs = parseLanguageList(response);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Failed parse json " + e.getMessage());
                        e.printStackTrace();
                    }
                    Log.i(LOG_TAG, "Downloaded list of languages (size = " + langs.size() + ")");
                    listener.onResponse(langs);
                }, listener);
        queue.add(request);
    }

    static List<Language> parseLanguageList(JSONObject json) throws JSONException {
        List<Language> langs = new ArrayList<>();
        JSONObject lang = json.getJSONObject("langs");
        Iterator<String> it = lang.keys();
        while (it.hasNext()) {
            String code = it.next();
            String label = lang.getString(code);
            langs.add(new Language(code, label));
        }
        return langs;
    }

    public void translate(final Word from, final Language toLanguage,
                          final ResponseListener<Pair<ApiCode, Translate>> listener) {
        String url = createUrl(from, toLanguage, TRANSLATE_URL, TRANSLATE_API_KEY);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Pair<ApiCode, Translate> translate = parseTranslate(response, from, toLanguage);
                        Log.i(LOG_TAG, "Translated text from = " + from + " to = " +
                                translate.second.getTo());
                        listener.onResponse(translate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, listener);
        queue.add(request);
    }

    static Pair<ApiCode, Translate> parseTranslate(JSONObject response, Word from, Language toLanguage) throws JSONException {
        String code = response.getString("code");
        JSONArray text = response.getJSONArray("text");
        String textString = "";
        if (text.length() != 0) {
            textString = text.getString(0);
        }
        Word to = new Word(textString, toLanguage);
        Translate field = new Translate(from, to, false);
        return Pair.create(ApiCode.parse(code), field);
    }

    public void dictionary(Word word, Language toLanguage, final ResponseListener<Dictionary> listener) {
        String url = createUrl(word, toLanguage, DICTIONARY_URL, DICTIONARY_API_KEY);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray defs = response.getJSONArray("def");

                        if (defs.length() == 0) {
                            listener.onResponse(Dictionary.EMPTY);
                            return;
                        }

                        Dictionary dictionary = parseDictionary(defs);

                        Log.i(LOG_TAG, "Downloaded article about " + dictionary);
                        listener.onResponse(dictionary);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, listener);
        queue.add(request);
    }

    static Dictionary parseDictionary(JSONArray defs) throws JSONException {
        String text = defs.getJSONObject(0).getString("text");
        Dictionary dictionary = new Dictionary(text);

        if (defs.getJSONObject(0).has("ts")) {
            String ts = defs.getJSONObject(0).getString("ts");
            dictionary.setTranscription(ts);
        }

        for (int i = 0; i < defs.length(); i++) {
            JSONObject def = defs.getJSONObject(i);
            String pos = null;
            if (def.has("pos")) {
                pos = def.getString("pos");
            }

            JSONArray trans = def.getJSONArray("tr");
            for (int j = 0; j < trans.length(); j++) {
                JSONObject tran = trans.getJSONObject(j);
                String textTran = tran.getString("text");
                Dictionary.Translate translate = new Dictionary.Translate(textTran);

                if (tran.has("gen")) {
                    String gen = tran.getString("gen");
                    translate.setGender(gen);
                }

                if (tran.has("mean")) {
                    JSONArray means = tran.getJSONArray("mean");
                    for (int k = 0; k < means.length(); k++) {
                        translate.addMean(means.getJSONObject(k).getString("text"));
                    }
                }
                dictionary.addTranslate(pos, translate);
            }
        }
        return dictionary;
    }

    private String createUrl(Word word, Language toLanguage, String url, String apiKey) {
        Language fromLan = word.getLanguage();
        String dir = fromLan.getCode() + "-" + toLanguage.getCode();
        String encodeText = word.getText();
        try {
            encodeText = URLEncoder.encode(word.getText(), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format(url, apiKey, dir, encodeText);
    }
}
