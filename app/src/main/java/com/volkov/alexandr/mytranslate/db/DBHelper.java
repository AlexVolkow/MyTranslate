package com.volkov.alexandr.mytranslate.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.volkov.alexandr.mytranslate.db.contract.LanguagesContract;
import com.volkov.alexandr.mytranslate.db.contract.TranslateContract;
import com.volkov.alexandr.mytranslate.db.contract.WordContract;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "MyTranslate.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LanguagesContract.SQL_CREATE_ENTRIES);
        db.execSQL(WordContract.SQL_CREATE_ENTRIES);
        db.execSQL(TranslateContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LanguagesContract.SQL_DELETE_ENTRIES);
        db.execSQL(WordContract.SQL_DELETE_ENTRIES);
        db.execSQL(TranslateContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}