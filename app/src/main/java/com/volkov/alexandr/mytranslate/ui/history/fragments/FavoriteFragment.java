package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import com.volkov.alexandr.mytranslate.model.Translate;

import java.util.ArrayList;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class FavoriteFragment extends BaseFragment {
    public FavoriteFragment() {
    }

    @Override
    protected RecyclerView.Adapter<Holder> getAdapter() {
        return new FavoriteAdapter(translates);
    }

    public static Fragment newInstance(ArrayList<Translate> translates) {
        return setArguments(new FavoriteFragment(), translates);
    }
}
