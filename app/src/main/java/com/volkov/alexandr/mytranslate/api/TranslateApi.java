package com.volkov.alexandr.mytranslate.api;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import static com.volkov.alexandr.mytranslate.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class TranslateApi {
    private static final String LOG_TAG = makeLogTag(TranslateApi.class);

    private static final String API_KEY =
            "trnsl.1.1.20170707T093646Z.ae8af46d0d3ae91f.fc80db8c48018769cb3736f0c75a6797f7dc818a";
    private static final String LANGS_URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=%s&ui=ru";
    private static final String TRANSLATE_URL =
            "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&lang=%s&text=%s";

    private static RequestQueue queue;

    public TranslateApi(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
    }

    public void getLangs(final ResponseListener<List<Language>> listener) {
        String url = String.format(LANGS_URL, API_KEY);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Language> langs = new ArrayList<>();
                        try {
                            JSONObject lang = response.getJSONObject("langs");
                            Iterator<String> it = lang.keys();
                            while (it.hasNext()) {
                                String code = it.next();
                                String label = lang.getString(code);
                                langs.add(new Language(code, label));
                            }
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Failed parse json " + e.getMessage());
                            e.printStackTrace();
                        }
                        Log.i(LOG_TAG, "Downloaded list of languages (size = " + langs.size() + ")");
                        listener.onResponse(langs);
                    }
                }, listener);
        queue.add(request);
    }

    public void translate(final Word from, final Language toLanguage, final ResponseListener<Pair<ApiCode, Translate>> listener) {
        Language fromLan = from.getLanguage();
        String dir = fromLan.getCode() + "-" + toLanguage.getCode();
        String encodeText = from.getText();
        try {
            encodeText = URLEncoder.encode(from.getText(),"UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(TRANSLATE_URL, API_KEY, dir, encodeText);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getString("code");
                            JSONArray text = response.getJSONArray("text");
                            String textString = "";
                            if (text.length() != 0) {
                                textString = text.getString(0);
                            }
                            Word to = new Word(textString, toLanguage);
                            Translate field = new Translate(from, to, false);
                            Log.i(LOG_TAG, "Translated text from = " + from + " to " + to);
                            listener.onResponse(new Pair<>(ApiCode.parse(code), field));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, listener);
        queue.add(request);
    }
}