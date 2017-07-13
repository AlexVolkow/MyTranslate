package com.volkov.alexandr.mytranslate.ui.history.fragments;

import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by AlexandrVolkov on 13.07.2017.
 */
public class TranslateObserver{
    private Translate translate;
    private static Set<FavoriteCallback> subscribers = new HashSet<>();

    public TranslateObserver(Translate translate) {
        this.translate = translate;
    }

    public static void subscribe(FavoriteCallback callback) {
        subscribers.add(callback);
    }

    public void setFavorite(boolean favorite) {
        translate.setFavorite(favorite);
        notifySubscribers();
    }

    private void notifySubscribers() {
        for (FavoriteCallback callback : subscribers) {
            callback.onFavoriteStatusChanged(this);
        }
    }

    public long getId() {
        return translate.getId();
    }

    public void setId(long id) {
        translate.setId(id);
    }

    public Word getFrom() {
        return translate.getFrom();
    }

    public Word getTo() {
        return translate.getTo();
    }

    public boolean isFavorite() {
        return translate.isFavorite();
    }

    protected Translate getTranslate() {
        return translate;
    }

    public void setTranslate(Translate translate) {
        this.translate = translate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslateObserver)) return false;

        TranslateObserver observer = (TranslateObserver) o;

        return translate != null ? translate.equals(observer.translate) : observer.translate == null;
    }

    @Override
    public int hashCode() {
        return translate != null ? translate.hashCode() : 0;
    }

    public interface FavoriteCallback {
        void onFavoriteStatusChanged(TranslateObserver translate);
    }
}
