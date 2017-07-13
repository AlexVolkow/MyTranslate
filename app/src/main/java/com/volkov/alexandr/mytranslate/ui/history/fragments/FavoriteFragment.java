package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.model.Translate;

import java.util.ArrayList;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class FavoriteFragment extends BaseFragment {
    public FavoriteFragment() {}

    @Override
    protected RecyclerView.Adapter<Holder> getAdapter() {
        return new FavoriteAdapter(translates, dbService, tvEmpty);
    }

    public static Fragment newInstance(ArrayList<Translate> translates) {
        ArrayList<Translate> favorites = new ArrayList<>();
        for (Translate translate : translates) {
            if (translate.isFavorite()) {
                favorites.add(translate);
            }
        }
        return setArguments(new FavoriteFragment(), favorites);
    }

    @Override
    protected String getEmptyPlaceholder() {
        return getString(R.string.empty_favorite);
    }
}
