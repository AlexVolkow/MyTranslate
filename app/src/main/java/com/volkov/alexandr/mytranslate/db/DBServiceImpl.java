package com.volkov.alexandr.mytranslate.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.volkov.alexandr.mytranslate.model.Language;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;
import com.volkov.alexandr.mytranslate.db.contract.LanguagesContract.LanguagesEntry;
import com.volkov.alexandr.mytranslate.db.contract.TranslateContract.TranslateEntry;
import com.volkov.alexandr.mytranslate.db.contract.WordContract.WordEntry;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class DBServiceImpl implements DBService {
    public static final String TRANSLATE = "translate";
    public static final String LANGS_ID = "lang_id";

    private DBHelper dbHelper;
    //private static List<Translate> cache = new ArrayList<>();

    public DBServiceImpl(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public List<Language> getLangs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sortOrder =
                LanguagesEntry.COLUMN_LABEL + " ASC";

        Cursor c = db.query(LanguagesEntry.TABLE_NAME, null, null,
                null, null, null, sortOrder);

        List<Language> langs = new ArrayList<>();

        if (c.moveToFirst()) {
            int idColIndex =c.getColumnIndex(LanguagesEntry._ID);
            int codeColIndex = c.getColumnIndex(LanguagesEntry.COLUMN_CODE);
            int labelColIndex = c.getColumnIndex(LanguagesEntry.COLUMN_LABEL);

            do {
                langs.add(new Language(c.getLong(idColIndex), c.getString(codeColIndex), c.getString(labelColIndex)));
            } while (c.moveToNext());
        }
        c.close();
        return langs;
    }

    @Override
    public long addLang(Language language) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LanguagesEntry.COLUMN_CODE, language.getCode());
        values.put(LanguagesEntry.COLUMN_LABEL, language.getLabel());

        return db.insert(LanguagesEntry.TABLE_NAME, null, values);
    }

    @Override
    public Language getLanguageByCode(String code) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = LanguagesEntry.COLUMN_CODE + " = ?";
        String[] selectionArgs = { code };

        Cursor c = db.query(LanguagesEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null , null);

        Language res = null;
        if (c.moveToFirst()) {
            int idColIndex =c.getColumnIndex(LanguagesEntry._ID);
            int labelColIndex = c.getColumnIndex(LanguagesEntry.COLUMN_LABEL);

            res = new Language(c.getLong(idColIndex), code, c.getString(labelColIndex));
        }
        c.close();
        return res;
    }

    @Override
    public void addLangs(List<Language> languages) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Language language : languages) {
            ContentValues values = new ContentValues();
            values.put(LanguagesEntry.COLUMN_CODE, language.getCode());
            values.put(LanguagesEntry.COLUMN_LABEL, language.getLabel());

            long id = db.insert(LanguagesEntry.TABLE_NAME, null, values);
            language.setId(id);
        }
    }

    @Override
    public long addWord(Word word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WordEntry.LANGUAGE_ID_COLUMN, word.getLanguage().getId());
        values.put(WordEntry.TEXT_COLUMN, word.getText());

        return db.insert(WordEntry.TABLE_NAME, null, values);
    }

    @Override
    public long addTranslate(Translate field) {
       /* if (!cache.isEmpty()) {
            cache.add(field);
        }*/

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Word from = field.getFrom();
        Word to = field.getTo();

        ContentValues values = new ContentValues();
        values.put(TranslateEntry.WORD_FROM_ID_COLUMN, from.getId());
        values.put(TranslateEntry.WORD_TO_ID_COLUMN, to.getId());
        values.put(TranslateEntry.IS_FAVORITE_COLUMN, field.isFavorite() ? 1 : 0);

        return db.insert(TranslateEntry.TABLE_NAME, null, values);
    }

    @Override
    public Language getLanguageById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = LanguagesEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor c = db.query(LanguagesEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null , null);

        Language res = null;
        if (c.moveToFirst()) {
            int codeColIndex = c.getColumnIndex(LanguagesEntry.COLUMN_CODE);
            int labelColIndex = c.getColumnIndex(LanguagesEntry.COLUMN_LABEL);

            res = new Language(id, c.getString(codeColIndex), c.getString(labelColIndex));
        }
        c.close();
        return res;
    }

    @Override
    public List<Translate> getTranslates(int limit) {
       /* if (!cache.isEmpty() && limit > ) {
            return new ArrayList<>(cache.subList(0, Math.min(cache.size(), limit)));
        }*/

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String table = TranslateEntry.TABLE_NAME + " INNER JOIN " + WordEntry.TABLE_NAME + " ON "
                + TranslateEntry.TABLE_NAME + "." + TranslateEntry.WORD_FROM_ID_COLUMN +" = " +
                WordEntry.TABLE_NAME + "." + WordEntry._ID + " OR " +
                TranslateEntry.TABLE_NAME + "." + TranslateEntry.WORD_TO_ID_COLUMN +" = " +
                WordEntry.TABLE_NAME + "." + WordEntry._ID;

        String[] columns = new String[]{TranslateEntry.TABLE_NAME + ".*",
                "group_concat(" + WordEntry.TABLE_NAME + "." + WordEntry.TEXT_COLUMN + ", '-') AS " + TRANSLATE,
                "group_concat(" + WordEntry.TABLE_NAME + "." + WordEntry.LANGUAGE_ID_COLUMN+ ", '-') AS "  + LANGS_ID};

        String groupBy = TranslateEntry.TABLE_NAME + "." + TranslateEntry._ID;

        String orderBy = TranslateEntry._ID + " DESC";

        Cursor c = db.query(table,columns, null, null, groupBy,
                null, orderBy, String.valueOf(limit));

        List<Translate> fields = new ArrayList<>();

        if (c.moveToFirst()) {
            int idColIndex =c.getColumnIndex(TranslateEntry._ID);
            int fromIdColIndex = c.getColumnIndex(TranslateEntry.WORD_FROM_ID_COLUMN);
            int toIdColIndex = c.getColumnIndex(TranslateEntry.WORD_TO_ID_COLUMN);
            int translateColIndex = c.getColumnIndex(TRANSLATE);
            int langIdColIndex = c.getColumnIndex(LANGS_ID);
            int favoriteColIndex = c.getColumnIndex(TranslateEntry.IS_FAVORITE_COLUMN);

            do {
                long id = c.getLong(idColIndex);

                String[] translate = c.getString(translateColIndex).split("-");
                String[] langsId = c.getString(langIdColIndex).split("-");

                Language fromLan = getLanguageById(Long.valueOf(langsId[0]));
                long fromId = c.getLong(fromIdColIndex);
                Word from = new Word(fromId, translate[0], fromLan);

                Language toLan = getLanguageById(Long.valueOf(langsId[1]));
                long toId = c.getLong(toIdColIndex);
                Word to = new Word(toId, translate[1], toLan);

                boolean isFavorite = c.getLong(favoriteColIndex) == 1;

                Translate tr = new Translate(id, from, to, isFavorite);
                fields.add(tr);
                //cache.add(tr);
            } while (c.moveToNext());
        }
        c.close();
        return fields;
    }

    @Override
    public Translate getLastTranslate() {
        List<Translate> last = getTranslates(1);
        if (last.isEmpty()) {
            return null;
        } else {
            return last.get(0);
        }
    }

    @Override
    public long findWord(Word word) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = WordEntry.TEXT_COLUMN + " = ? and " + WordEntry.LANGUAGE_ID_COLUMN + " = ?";
        String[] selectionArgs = { word.getText(), String.valueOf(word.getLanguage().getId()) };

        Cursor c = db.query(WordEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null , null);

        long res = -1;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(WordEntry._ID);
            res = c.getLong(idColIndex);
        }
        c.close();
        return res;
    }

    @Override
    public long findTranslate(Translate field) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = TranslateEntry.WORD_FROM_ID_COLUMN + " = ? and " + TranslateEntry.WORD_TO_ID_COLUMN + " = ?";
        String[] selectionArgs = { String.valueOf(field.getFrom().getId()), String.valueOf(field.getTo().getId()) };

        Cursor c = db.query(TranslateEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null , null);

        long res = -1;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(TranslateEntry._ID);
            res = c.getLong(idColIndex);
        }
        c.close();
        return res;
    }

    @Override
    public void deleteTranslateField(Translate field) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TranslateEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(field.getId()) };

        //cache.remove(field);
        db.delete(TranslateEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void makeFavorite(Translate field) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TranslateEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(field.getId()) };

        ContentValues values = new ContentValues();
        values.put(TranslateEntry.IS_FAVORITE_COLUMN, 1);

        db.update(TranslateEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void makeUnFavorite(Translate field) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TranslateEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(field.getId()) };

        ContentValues values = new ContentValues();
        values.put(TranslateEntry.IS_FAVORITE_COLUMN, 0);

        db.update(TranslateEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public boolean isFavoriteTranslate(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = TranslateEntry._ID + " = ? ";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor c = db.query(TranslateEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null , null);

        boolean res = false;
        if (c.moveToFirst()) {
            int isFavoriteColIndex = c.getColumnIndex(TranslateEntry.IS_FAVORITE_COLUMN);
            res = c.getInt(isFavoriteColIndex) == 1;
        }
        c.close();
        return res;
    }
}
