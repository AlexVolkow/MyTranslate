package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.db.DBService;
import com.volkov.alexandr.mytranslate.db.DBServiceImpl;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.ui.history.SimpleDividerItemDecoration;

import java.util.ArrayList;

import static com.volkov.alexandr.mytranslate.ui.history.HistoryManagerFragment.HISTORY;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public abstract class BaseFragment extends Fragment {
    protected ArrayList<TranslateObserver> translates;
    protected DBService dbService;
    protected TextView tvEmpty;

    public BaseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbService = new DBServiceImpl(getContext());

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<Translate> translates = args.getParcelableArrayList(HISTORY);
            this.translates = new ArrayList<>();
            for (Translate translate : translates) {
                TranslateObserver favorite = new TranslateObserver(translate);
                this.translates.add(favorite);
            }
        }

        View view =  inflater.inflate(R.layout.fragment_history, container, false);

        tvEmpty = (TextView) view.findViewById(R.id.tv_empty_history);
        if (translates.isEmpty()) {
            tvEmpty.setText(getEmptyPlaceholder());
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        RecyclerView history = (RecyclerView) view.findViewById(R.id.history_view);
        history.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        history.setLayoutManager(layoutManager);
        history.addItemDecoration(new SimpleDividerItemDecoration(getContext(), false));
        RecyclerView.Adapter<Holder> adapter = getAdapter();
        history.setAdapter(adapter);

        return view;
    }

    protected static Fragment setArguments(Fragment fragment, ArrayList<Translate> translates) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(HISTORY, translates);
        fragment.setArguments(args);
        return fragment;
    }

    protected abstract RecyclerView.Adapter<Holder> getAdapter();

    protected abstract String getEmptyPlaceholder();
}

