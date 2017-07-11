package com.volkov.alexandr.mytranslate.db;

import com.volkov.alexandr.mytranslate.model.Language;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;

import java.util.List;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public interface DBService {
    List<Language> getLangs();

    long addLang(Language language);

    void addLangs(List<Language> languages);

    long addWord(Word word);

    long findWord(Word word);

    long addTranslate(Translate field);

    long findTranslate(Translate field);

    Language getLanguageById(long id);

    Language getLanguageByCode(String code);

    List<Translate> getTranslates(int limit);

    Translate getLastTranslate();

    boolean isFavoriteTranslate(long id);

    void deleteTranslateField(Translate field);

    void makeFavorite(Translate field);

    void makeUnFavorite(Translate field);
}
