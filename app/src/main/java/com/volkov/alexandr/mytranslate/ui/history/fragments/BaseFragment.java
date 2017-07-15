package com.volkov.alexandr.mytranslate.ui.history.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.db.DBService;
import com.volkov.alexandr.mytranslate.db.DBServiceImpl;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.ui.history.SimpleDividerItemDecoration;

import java.util.ArrayList;

import static com.volkov.alexandr.mytranslate.ui.history.HistoryManagerFragment.HISTORY;
import static com.volkov.alexandr.mytranslate.utils.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 12.07.2017.
 */
public abstract class BaseFragment extends Fragment {
    private static final String LOG_TAG = makeLogTag(BaseFragment.class);

    private Unbinder unbinder;

    @BindView(R.id.tv_empty_history)
    TextView tvEmpty;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.history_view)
    RecyclerView history;

    protected ArrayList<TranslateObserver> translates;
    protected DBService dbService;

    private ArrayList<TranslateObserver> found = new ArrayList<>();

    private HistoryAdapter adapter;

    public BaseFragment() {
    }

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

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (translates.isEmpty()) {
            tvEmpty.setText(getEmptyPlaceholder());
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        etSearch.setHint(getHintPlaceHolder());
        initRecyclerView();
        return view;
    }

    protected static Fragment setArguments(Fragment fragment, ArrayList<Translate> translates) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(HISTORY, translates);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.ib_clear_search)
    public void clearSearch() {
        etSearch.getText().clear();
        adapter.setDataSet(translates);
    }

    @OnClick(R.id.ib_search)
    public void search() {
        String searchStr = etSearch.getText().toString();
        found.clear();

        for (TranslateObserver translate : translates) {
            if (translate.getFrom().getText().contains(searchStr) ||
                    translate.getTo().getText().contains(searchStr)) {
                found.add(translate);
            }
        }

        adapter.setDataSet(found);
        Log.i(LOG_TAG, "For search '" + searchStr + "' found " + found.size() + " translates");
    }

    private void initRecyclerView() {
        history.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        history.setLayoutManager(layoutManager);
        history.addItemDecoration(new SimpleDividerItemDecoration(getContext(), false));
        adapter = getAdapter();
        history.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract HistoryAdapter getAdapter();

    protected abstract String getEmptyPlaceholder();

    protected abstract String getHintPlaceHolder();
}

