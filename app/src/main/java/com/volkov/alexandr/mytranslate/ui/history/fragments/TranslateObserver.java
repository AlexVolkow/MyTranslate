package com.volkov.alexandr.mytranslate.ui.history.fragments;

import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by AlexandrVolkov on 13.07.2017.
 */
public class TranslateObserver extends Translate{
    private static Set<FavoriteCallback> subscribers = new HashSet<>();

    public TranslateObserver(long id, Word from, Word to, boolean isFavorite) {
        super(id, from, to, isFavorite);
    }

    public static void subscribe(FavoriteCallback callback) {
        subscribers.add(callback);
    }

    public void setFavorite(boolean favorite) {
        super.setFavorite(favorite);
        notifySubscribers();
    }

    private void notifySubscribers() {
        for (FavoriteCallback callback : subscribers) {
            callback.onFavoriteStatusChanged(this);
        }
    }

    public interface FavoriteCallback {
        void onFavoriteStatusChanged(TranslateObserver translate);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
