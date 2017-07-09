package com.volkov.alexandr.mytranslate.api;

/**
 * Created by AlexandrVolkov on 09.07.2017.
 */
public enum ApiCode {
    OK(200), TO_LONG(413), NOT_TRANSLATE(422), NOT_SUPPORT_LANG(501), UNKNOWN_CODE(0);

    int code;
    ApiCode(int code) {
        this.code = code;
    }

    public static ApiCode parse(String v) {
        switch (v) {
            case "200":
                return OK;
            case "413":
                return TO_LONG;
            case "422":
                return NOT_TRANSLATE;
            case "501":
                return NOT_SUPPORT_LANG;
            default:
                return UNKNOWN_CODE;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "(" + code + ")";
    }
}
