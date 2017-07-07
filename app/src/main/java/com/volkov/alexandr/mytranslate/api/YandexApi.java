package com.volkov.alexandr.mytranslate.api;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.volkov.alexandr.mytranslate.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class YandexApi {
    private static final String LOG_TAG = makeLogTag(YandexApi.class);

    private static final String API_KEY =
            "trnsl.1.1.20170707T093646Z.ae8af46d0d3ae91f.fc80db8c48018769cb3736f0c75a6797f7dc818a";
    private static final String LANGS_URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=%s&ui=ru";

    private RequestQueue queue;
    private Context context;

    public YandexApi(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
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
                            e.printStackTrace();
                        }
                        Log.i(LOG_TAG, "Downloaded list of languages (size = " + langs.size() + ")");
                        listener.onResponse(langs);
                    }
                }, listener);
        queue.add(request);
    }
}
