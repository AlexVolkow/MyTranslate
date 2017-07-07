package com.volkov.alexandr.mytranslate.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.volkov.alexandr.mytranslate.api.Language;
import com.volkov.alexandr.mytranslate.db.LanguagesContract.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class DBServiceImpl implements DBService {
    private DBHelper dbHelper;

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
            int codeColIndex = c.getColumnIndex(LanguagesEntry.COLUMN_CODE);
            int labelColIndex = c.getColumnIndex(LanguagesEntry.COLUMN_LABEL);

            do {
                langs.add(new Language(c.getString(codeColIndex), c.getString(labelColIndex)));
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
    public void addLangs(List<Language> languages) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Language language : languages) {
            ContentValues values = new ContentValues();
            values.put(LanguagesEntry.COLUMN_CODE, language.getCode());
            values.put(LanguagesEntry.COLUMN_LABEL, language.getLabel());

            db.insert(LanguagesEntry.TABLE_NAME, null, values);
        }
    }
}
