package com.volkov.alexandr.mytranslate.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import com.volkov.alexandr.mytranslate.model.Dictionary;

import java.util.List;
import java.util.Map;

/**
 * Created by AlexandrVolkov on 14.07.2017.
 */
public class DictionaryDecorator {
    private static final int DARK_RED = Color.rgb(153, 51, 0);
    public static final int DARK_CYAN = Color.rgb(0, 153, 204);

    public static Spannable decorate(Dictionary dictionary) {
        SpannableStringBuilder str = new SpannableStringBuilder();
        str.append(dictionary.getText());
        Map<String, List<Dictionary.Translate>> trs = dictionary.getTranslates();
        if (dictionary.getTranscription() != null) {
            int currLen = str.length();
            str.append(" [").append(dictionary.getTranscription()).append("]");
            str.setSpan(new ForegroundColorSpan(Color.LTGRAY), currLen, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        str.append('\n');
        for (String pos : trs.keySet()) {
            int currLen = str.length();
            str.append(pos).append('\n');
            str.setSpan(new StyleSpan(Typeface.ITALIC), currLen, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(DARK_RED), currLen, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new RelativeSizeSpan(0.8f), currLen, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            List<Dictionary.Translate> translates = trs.get(pos);
            for (int i = 0; i < translates.size(); i++) {
                currLen = str.length();
                str.append(String.valueOf(i + 1));
                str.setSpan(new RelativeSizeSpan(0.9f), currLen, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new ForegroundColorSpan(Color.LTGRAY), currLen, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.append(decorateTranslate(translates.get(i), "\t"));
            }
            str.append("\n");
        }
        return str;
    }

    private static Spannable decorateTranslate(Dictionary.Translate translate, String pref) {
        SpannableStringBuilder spanstr = new SpannableStringBuilder();
        spanstr.append(pref).append(translate.getText());
        spanstr.setSpan(new ForegroundColorSpan(DARK_CYAN), 0, spanstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (translate.getGender() != null) {
            int currLen = spanstr.length();
            spanstr.append(" " + translate.getGender());
            spanstr.setSpan(new StyleSpan(Typeface.ITALIC), currLen, spanstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanstr.setSpan(new ForegroundColorSpan(Color.LTGRAY), currLen, spanstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        spanstr.append('\n');

        List<String> means = translate.getMeans();
        int currLen = spanstr.length();
        if (!means.isEmpty()) {
            spanstr.append(pref).append("(");
        }
        for (int i = 0; i < means.size(); i++) {
            spanstr.append(means.get(i));
            if (i != means.size() - 1) {
                spanstr.append(", ");
            }
        }
        if (!means.isEmpty()) {
            spanstr.append(")\n");
            spanstr.setSpan(new ForegroundColorSpan(DARK_RED), currLen, spanstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanstr;
    }
}
