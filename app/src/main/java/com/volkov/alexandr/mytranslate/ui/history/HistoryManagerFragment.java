package com.volkov.alexandr.mytranslate.ui.history;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.ui.history.fragments.FavoriteFragment;
import com.volkov.alexandr.mytranslate.ui.history.fragments.HistoryFragment;

import java.util.ArrayList;


public class HistoryManagerFragment extends Fragment {
    public static final String HISTORY = "HISTORY";

    private Unbinder unbinder;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private ArrayList<Translate> translates;

    public HistoryManagerFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            translates = args.getParcelableArrayList(HISTORY);
        }

        View view =  inflater.inflate(R.layout.fragment_history_manager, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(HistoryFragment.newInstance(translates), getString(R.string.history));
        adapter.addFragment(FavoriteFragment.newInstance(translates), getString(R.string.favorite));
        viewPager.setAdapter(adapter);
    }

    public static HistoryManagerFragment newInstance(ArrayList<Translate> translates) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(HISTORY, translates);

        HistoryManagerFragment fragment = new HistoryManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
