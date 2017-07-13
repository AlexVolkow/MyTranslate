package com.volkov.alexandr.mytranslate.ui;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.android.volley.VolleyError;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.api.ResponseListener;
import com.volkov.alexandr.mytranslate.api.TranslateApi;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;
import com.volkov.alexandr.mytranslate.db.DBService;
import com.volkov.alexandr.mytranslate.db.DBServiceImpl;
import com.volkov.alexandr.mytranslate.model.Language;
import com.volkov.alexandr.mytranslate.ui.history.HistoryManagerFragment;

import java.util.ArrayList;
import java.util.List;

import static com.volkov.alexandr.mytranslate.LogHelper.makeLogTag;

public class MainActivity extends AppCompatActivity implements ResponseListener<List<Language>> {
    private static final String LOG_TAG = makeLogTag(MainActivity.class);
    public static final int LIMIT = 100;

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ProgressDialog pd;

    private DBService dbService;
    private List<Language> langs;
    private TranslateApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(this);
        dbService = new DBServiceImpl(this);
        langs = dbService.getLangs();

        api = new TranslateApi(this);

        if (langs.isEmpty()) {
            pd.setMessage("List of languages downloading...");
            pd.setCancelable(false);
            pd.show();
            api.getLangs(this);
        } else {
            initBottomNavigation();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (pd.isShowing()) {
            pd.hide();
        }
        Log.e(LOG_TAG, "Error with downloading list of languages. " + error);
        showAlert("Error with downloading list of languages. " +
                "Please check you network connection and restart application");
        finish();
    }

    @Override
    public void onResponse(List<Language> response) {
        langs = response;
        new LangsToDB().execute();
    }

    public void showAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Failed downloading")
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Fragment makeTranslateFragment() {
        Translate last = dbService.getLastTranslate();
        if (last == null) {
            Language ru = dbService.getLanguageByCode("ru");
            Language en = dbService.getLanguageByCode("en");
            last = new Translate(new Word(en), new Word(ru), false);
        }
        return TranslateFragment.newInstance(last, (ArrayList<Language>) langs);
    }

    private Fragment makeHistoryFragment() {
        ArrayList<Translate> translates = (ArrayList<Translate>) dbService.getTranslates(LIMIT);
        return HistoryManagerFragment.newInstance(translates);
    }

    private void initBottomNavigation() {
        fragmentManager = getSupportFragmentManager();
        fragment = makeTranslateFragment();
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
                                fragment = makeTranslateFragment();
                                currPage = 1;
                                break;
                            case R.id.action_history:
                                fragment = makeHistoryFragment();
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

    private class LangsToDB extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void[] params) {
            dbService.addLangs(langs);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pd.isShowing()) {
                pd.hide();
            }
            initBottomNavigation();
        }
    }
}
