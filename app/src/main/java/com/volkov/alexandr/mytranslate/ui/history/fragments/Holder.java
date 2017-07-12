package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.volkov.alexandr.mytranslate.R;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class Holder extends RecyclerView.ViewHolder {
    TextView fromText;
    TextView toText;
    TextView langTr;
    ToggleButton fav;

    public Holder(View itemView) {
        super(itemView);
        fromText = (TextView) itemView.findViewById(R.id.tv_from_text);
        toText = (TextView) itemView.findViewById(R.id.tv_to_text);
        langTr = (TextView) itemView.findViewById(R.id.tv_lang_tr);
        fav = (ToggleButton) itemView.findViewById(R.id.tb_fav);
    }
}