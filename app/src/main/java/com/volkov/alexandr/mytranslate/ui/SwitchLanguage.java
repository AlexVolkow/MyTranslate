package com.volkov.alexandr.mytranslate.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.api.Language;

import java.util.ArrayList;

import static com.volkov.alexandr.mytranslate.ui.TranslateFragment.CURR_LANG_KEY;
import static com.volkov.alexandr.mytranslate.ui.TranslateFragment.LANGS_KEY;

public class SwitchLanguage extends AppCompatActivity {
    public static final String LANG_KEY = "lang";

    private ArrayList<Language> langs;
    private Language curr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_language);

        Intent intent = getIntent();
        langs = intent.getParcelableArrayListExtra(LANGS_KEY);
        curr = intent.getParcelableExtra(CURR_LANG_KEY);

        ListView langList = (ListView) findViewById(R.id.lang_list);

        Adapter adapter = new Adapter(langs, curr);
        langList.setAdapter(adapter);
        langList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Language lang = (Language) parent.getItemAtPosition(position);

                Intent intent = getIntent();
                intent.putExtra(LANG_KEY, lang);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.back_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Язык ввода");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class Adapter extends BaseAdapter {
        private ArrayList<Language> dataSet;
        private Language curr;

        public Adapter(ArrayList<Language> dataSet, Language curr) {
            this.dataSet = dataSet;
            this.curr = curr;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view =  LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lang_item, parent, false);
            }

            Language lang = (Language) getItem(position);

            TextView tvLang = (TextView) view.findViewById(R.id.tv_lang);
            ImageView ivCheck = (ImageView) view.findViewById(R.id.iv_check);
            tvLang.setText(lang.getLabel());

            if (curr.equals(lang)) {
                ivCheck.setVisibility(View.VISIBLE);
            } else {
                ivCheck.setVisibility(View.INVISIBLE);
            }
            return view;
        }

        @Override
        public int getCount() {
            return dataSet.size();
        }

        @Override
        public Object getItem(int position) {
            return dataSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
