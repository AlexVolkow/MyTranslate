package com.volkov.alexandr.mytranslate.ui.history.fragments;

import com.volkov.alexandr.mytranslate.model.Translate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class FavoriteAdapter extends HistoryAdapter {
    public FavoriteAdapter(List<Translate> dataSet) {
        super(dataSet);
        List<Translate> favorite = new ArrayList<>();
        for (Translate translate : dataSet) {
            if (translate.isFavorite()) {
                favorite.add(translate);
            }
        }
        super.dataSet = favorite;
    }

    public void addItem(Translate favorite) {
        dataSet.add(0, favorite);
        notifyItemChanged(0);
    }

    public void deleteItem(Translate favorite) {
        int idx = dataSet.indexOf(favorite);
        dataSet.remove(idx);
        notifyItemRemoved(idx);
    }
}
