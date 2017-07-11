package com.volkov.alexandr.mytranslate.db.contract;

import android.provider.BaseColumns;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class LanguagesContract {
    private LanguagesContract() {}

    public static class LanguagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "languages";

        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_CODE = "code";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + LanguagesEntry.TABLE_NAME + " (" +
                    LanguagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LanguagesEntry.COLUMN_CODE + " TEXT, " +
                    LanguagesEntry.COLUMN_LABEL + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LanguagesEntry.TABLE_NAME;
}
