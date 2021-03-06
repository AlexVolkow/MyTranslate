package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.support.v4.app.Fragment;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.model.Translate;

import java.util.ArrayList;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public class HistoryFragment extends BaseFragment {
    public HistoryFragment() {
    }

    @Override
    protected HistoryAdapter getAdapter() {
        return new HistoryAdapter(translates, dbService);
    }

    public static Fragment newInstance(ArrayList<Translate> translates) {
        return setArguments(new HistoryFragment(), translates);
    }

    @Override
    protected String getEmptyPlaceholder() {
        return getString(R.string.empty_history);
    }

    @Override
    protected String getHintPlaceHolder() {
        return getString(R.string.search_in_history);
    }
}
