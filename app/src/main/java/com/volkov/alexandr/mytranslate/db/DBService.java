package com.volkov.alexandr.mytranslate.db;

import com.volkov.alexandr.mytranslate.api.Language;

import java.util.List;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public interface DBService {
    List<Language> getLangs();

    long addLang(Language language);

    void addLangs(List<Language> languages);
}
