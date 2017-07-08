package com.volkov.alexandr.mytranslate.ui;


import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.volkov.alexandr.mytranslate.api.YandexApi;
import com.volkov.alexandr.mytranslate.db.DBService;
import com.volkov.alexandr.mytranslate.db.DBServiceImpl;
import com.volkov.alexandr.mytranslate.api.Language;

import java.util.ArrayList;
import java.util.List;

import static com.volkov.alexandr.mytranslate.LogHelper.makeLogTag;

public class MainActivity extends AppCompatActivity implements ResponseListener<List<Language>> {
    private static final String LOG_TAG = makeLogTag(MainActivity.class);

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

        pd = new ProgressDialog(this);
        dbService = new DBServiceImpl(this);
        langs = dbService.getLangs();

        api = new YandexApi(this);

        if (langs.isEmpty()) {
            pd.setMessage("List of languages downloading...");
            pd.setCancelable(false);
            pd.show();
            api.getLangs(this);
        }

        fragmentManager = getSupportFragmentManager();
        fragment = TranslateFragment.newInstance(Language.RU, Language.EN,
                (ArrayList<Language>) langs);
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
                                fragment = TranslateFragment.newInstance(Language.RU, Language.EN,
                                        (ArrayList<Language>) langs);
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
        if (pd.isShowing()) {
            pd.hide();
        }
        Log.e(LOG_TAG, "Error with downloading list of languages. " +
                "Please check you network connection and restart application");
        showAlert("Error with downloading list of languages. " +
                "Please check you network connection and restart application");
        finish();
    }

    @Override
    public void onResponse(List<Language> response) {
        if (pd.isShowing()) {
            pd.hide();
        }
        langs = response;
        dbService.addLangs(response);
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
}
