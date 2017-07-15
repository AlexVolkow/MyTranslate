package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.volkov.alexandr.mytranslate.R;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class Holder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_from_text)
    TextView fromText;
    @BindView(R.id.tv_to_text)
    TextView toText;
    @BindView(R.id.tv_lang_tr)
    TextView langTr;
    @BindView(R.id.tb_fav)
    ToggleButton fav;

    public Holder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}