package com.volkov.alexandr.mytranslate.utils;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class LogHelper {
    private static final String LOG_PREFIX = "mtr_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }


    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }
}
