package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.model.Translate;

import java.util.List;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class HistoryAdapter extends RecyclerView.Adapter<Holder> {
    protected List<Translate> dataSet;

    public HistoryAdapter(List<Translate> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Translate translate = dataSet.get(holder.getAdapterPosition());
        holder.fromText.setText(translate.getFrom().getText());
        holder.toText.setText(translate.getTo().getText());

        String codeFrom = translate.getFrom().getLanguage().getCode();
        String codeTo = translate.getTo().getLanguage().getCode();
        holder.langTr.setText(codeFrom + "-" + codeTo);

        holder.fav.setChecked(translate.isFavorite());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
