package com.volkov.alexandr.mytranslate.db.contract;

import android.provider.BaseColumns;

/**
 * Created by AlexandrVolkov on 10.07.2017.
 */
public class TranslateContract {
    private TranslateContract() {}

    public static class TranslateEntry implements BaseColumns {
        public static final String TABLE_NAME = "translate_field";

        public static final String WORD_FROM_ID_COLUMN = "word_from_id";
        public static final String WORD_TO_ID_COLUMN = "word_to_id";
        public static final String IS_FAVORITE_COLUMN = "is_favorite";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TranslateEntry.TABLE_NAME + " (" +
                    TranslateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TranslateEntry.WORD_FROM_ID_COLUMN + " INTEGER, " +
                    TranslateEntry.WORD_TO_ID_COLUMN + " INTEGER, " +
                    TranslateEntry.IS_FAVORITE_COLUMN + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TranslateEntry.TABLE_NAME;
}
