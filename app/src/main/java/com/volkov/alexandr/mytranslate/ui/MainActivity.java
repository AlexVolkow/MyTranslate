package com.volkov.alexandr.mytranslate.ui;


import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.android.volley.VolleyError;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.api.ResponseListener;
import com.volkov.alexandr.mytranslate.api.YandexApi;
import com.volkov.alexandr.mytranslate.db.DBService;
import com.volkov.alexandr.mytranslate.db.DBServiceImpl;
import com.volkov.alexandr.mytranslate.api.Language;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ResponseListener<List<Language>> {
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ProgressDialog pd;

    private DBService dbService;
    private List<Language> langs;
    private YandexApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.pd = new ProgressDialog(this);
        this.dbService = new DBServiceImpl(this);
        this.langs = dbService.getLangs();

        this.api = new YandexApi(this);

        if (langs.isEmpty()) {
            pd.setMessage("List of languages downloading...");
            pd.setCancelable(false);
            pd.show();
            api.getLangs(this);
        }

        fragmentManager = getSupportFragmentManager();
        fragment = new TranslateFragment();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment).commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    int prevPage = 0;

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int currPage = 0;
                        switch (item.getItemId()) {
                            case R.id.action_translate:
                                fragment = new TranslateFragment();
                                currPage = 1;
                                break;
                            case R.id.action_history:
                                fragment = new HistoryFragment();
                                currPage = 2;
                                break;
                            case R.id.action_about:
                                fragment = new AboutFragment();
                                currPage = 3;
                                break;
                        }
                        if (currPage == prevPage) {
                            return true;
                        }
                        final FragmentTransaction transaction = fragmentManager.beginTransaction();
                        if (currPage > prevPage) {
                            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        prevPage = currPage;
                        transaction.replace(R.id.fragment_container, fragment).commit();
                        return true;
                    }
                });
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(List<Language> response) {
        if (pd.isShowing()) {
            pd.hide();
        }
        this.langs = response;
        dbService.addLangs(response);
    }
}
