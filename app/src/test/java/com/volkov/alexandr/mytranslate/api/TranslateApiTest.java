package com.volkov.alexandr.mytranslate.api;


import android.support.v4.util.Pair;

import com.volkov.alexandr.mytranslate.model.Language;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by alexa on 23.12.2017.
 */
public class TranslateApiTest {
    @Test
    public void downloadLangs() throws JSONException{
        String rawJson = "{\"langs\":{\"af\":\"Африкаанс\", \"az\":\"Азербайджанский\"," +
                "\"bn\":\"Бенгальский\",\"bs\":\"Боснийский\",\"ca\":\"Каталанский\"}}";

        Language af = new Language("af", "Африкаанс");
        Language az = new Language("az", "Азербайджанский");
        Language bn = new Language("bn", "Бенгальский");
        Language bs = new Language("bs", "Боснийский");
        Language ca = new Language("ca", "Каталанский");

        JSONObject json = new JSONObject(rawJson);
        List<Language> langs = TranslateApi.parseLanguageList(json);
        assertThat(langs.size()).isEqualTo(5);
        assertThat(langs).containsOnly(af, az, bn, bs, ca);
    }

    @Test
    public void translateRuEn() throws JSONException {
        String rawJson = "{\"code\":\"200\",\"lang\":\"ru-en\",\"text\":[\"Refrigerator\"]}";

        Language ru = new Language("ru","Русский");
        Language en = new Language("en", "Английский");
        Word from = new Word("Холодильник", ru);
        Word to = new Word("Refrigerator", en);

        JSONObject json = new JSONObject(rawJson);
        Pair<ApiCode, Translate> translate = TranslateApi.parseTranslate(json, from, en);
        assertThat(translate.first).isEqualTo(ApiCode.OK);
        assertThat(translate.second.getTo()).isEqualTo(to);
    }
}