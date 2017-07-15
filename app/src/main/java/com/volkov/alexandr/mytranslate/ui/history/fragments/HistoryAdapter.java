package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.db.DBService;

import java.util.List;

import static com.volkov.alexandr.mytranslate.utils.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class HistoryAdapter extends RecyclerView.Adapter<Holder> implements TranslateObserver.FavoriteCallback {
    private static final String LOG_TAG = makeLogTag(HistoryAdapter.class);

    protected List<TranslateObserver> dataSet;
    protected DBService dbService;

    public HistoryAdapter(List<TranslateObserver> dataSet, DBService dbService) {
        this.dbService = dbService;
        this.dataSet = dataSet;
        TranslateObserver.subscribe(this);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final TranslateObserver translate = dataSet.get(holder.getAdapterPosition());
        holder.fromText.setText(translate.getFrom().getText());
        holder.toText.setText(translate.getTo().getText());

        String codeFrom = translate.getFrom().getLanguage().getCode();
        String codeTo = translate.getTo().getLanguage().getCode();
        holder.langTr.setText(codeFrom + "-" + codeTo);

        holder.fav.setChecked(translate.isFavorite());
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!translate.isFavorite()) {
                    dbService.makeFavorite(translate.getTranslate());
                    Log.i(LOG_TAG, "Translate " + translate.getTranslate() + " added to favorite");
                } else {
                    dbService.makeUnFavorite(translate.getTranslate());
                    Log.i(LOG_TAG, "Translate " + translate.getTranslate() + " removed from favorite");
                }
                translate.setFavorite(!translate.isFavorite());
            }
        });
    }

    public void addItem(TranslateObserver favorite) {
        dataSet.add(0, favorite);
        notifyDataSetChanged();
    }

    public void deleteItem(TranslateObserver favorite) {
        dataSet.remove(favorite);
        notifyDataSetChanged();
    }

    @Override
    public void onFavoriteStatusChanged(TranslateObserver translate) {
        notifyDataSetChanged();
    }

    public void setDataSet(List<TranslateObserver> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
