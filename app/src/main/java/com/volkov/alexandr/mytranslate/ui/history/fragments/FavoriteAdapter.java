package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.volkov.alexandr.mytranslate.db.DBService;

import java.util.ArrayList;
import java.util.List;

import static com.volkov.alexandr.mytranslate.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class FavoriteAdapter extends HistoryAdapter {
    private static final String LOG_TAG = makeLogTag(FavoriteAdapter.class);

    private TextView tvEmpty;

    public FavoriteAdapter(List<TranslateObserver> dataSet, DBService dbService, TextView tvEmpty) {
        super(dataSet, dbService);
        this.tvEmpty = tvEmpty;
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateObserver translate = dataSet.get(holder.getAdapterPosition());
                dbService.makeUnFavorite(translate.getTranslate());
                translate.setFavorite(false);
                Log.i(LOG_TAG, "Translate " + translate + " removed from favorite");
            }
        });
    }

    @Override
    public void onFavoriteStatusChanged(TranslateObserver translate) {
        if (!translate.isFavorite()) {
            deleteItem(translate);
        } else {
            addItem(translate);
        }
        if (dataSet.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }
}
