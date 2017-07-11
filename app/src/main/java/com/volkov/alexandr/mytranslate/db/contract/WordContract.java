package com.volkov.alexandr.mytranslate.db.contract;

import android.provider.BaseColumns;

/**
 * Created by AlexandrVolkov on 10.07.2017.
 */
public class WordContract {
    private WordContract() {}

    public static class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";

        public static final String TEXT_COLUMN = "text";
        public static final String LANGUAGE_ID_COLUMN = "language_id";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + WordEntry.TABLE_NAME + " (" +
                    WordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WordEntry.TEXT_COLUMN + " TEXT, " +
                    WordEntry.LANGUAGE_ID_COLUMN + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME;
}
