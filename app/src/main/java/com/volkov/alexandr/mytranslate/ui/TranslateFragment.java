package com.volkov.alexandr.mytranslate.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.VolleyError;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.api.ApiCode;
import com.volkov.alexandr.mytranslate.api.ResponseListener;
import com.volkov.alexandr.mytranslate.api.TranslateApi;
import com.volkov.alexandr.mytranslate.db.DBService;
import com.volkov.alexandr.mytranslate.db.DBServiceImpl;
import com.volkov.alexandr.mytranslate.model.Dictionary;
import com.volkov.alexandr.mytranslate.model.Language;
import com.volkov.alexandr.mytranslate.model.Translate;
import com.volkov.alexandr.mytranslate.model.Word;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.volkov.alexandr.mytranslate.ui.SwitchLanguageActivity.LANG_KEY;
import static com.volkov.alexandr.mytranslate.utils.AndroidHelper.hasConnection;
import static com.volkov.alexandr.mytranslate.utils.AndroidHelper.showAlert;
import static com.volkov.alexandr.mytranslate.utils.LogHelper.makeLogTag;


public class TranslateFragment extends Fragment implements View.OnClickListener,
        ResponseListener<Pair<ApiCode, Translate>> {
    private static final String LOG_TAG = makeLogTag(TranslateFragment.class);

    private static final int SWITCH_LANGUAGE = 1;
    private static final String FROM_KEY = "FROM";
    private static final String TO_KEY = "TO";
    public static final String CURR_LANG_KEY = "CURR_LANG";
    public static final String LANGS_KEY = "LANGS";
    private static final String TRANSLATE = "TRANSLATE";

    private Language from;
    private Language to;
    private Translate last;

    private ArrayList<Language> langs;
    private DBService dbService;
    private TranslateApi api;
    private ResponseListener<Dictionary> dictionaryListener;

    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvTranslate;
    private TextView tvError;
    private EditText etTrText;
    private Button btnRepeat;
    private ToggleButton ibAddFavorite;
    private TextView article;

    public TranslateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new TranslateApi(getContext());
        dbService = new DBServiceImpl(getContext());

        Bundle args = getArguments();
        if (args != null) {
            langs = args.getParcelableArrayList(LANGS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        article = (TextView) view.findViewById(R.id.article);
        article.setMovementMethod(new ScrollingMovementMethod());
        tvFrom = (TextView) view.findViewById(R.id.tv_from);
        tvTo = (TextView) view.findViewById(R.id.tv_to);
        tvTranslate = (TextView) view.findViewById(R.id.tv_translate);
        btnRepeat = (Button) view.findViewById(R.id.btn_repeat);
        tvError = (TextView) view.findViewById(R.id.tv_error);
        etTrText = (EditText) view.findViewById(R.id.et_trtext);
        ImageButton ibSwap = (ImageButton) view.findViewById(R.id.ib_swap);
        ibAddFavorite = (ToggleButton) view.findViewById(R.id.tb_add_fav);
        ImageButton trl = (ImageButton) view.findViewById(R.id.ib_translate);
        final ImageButton ibDeleteText = (ImageButton) view.findViewById(R.id.ib_delete_text);
        ibDeleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTrText.getText().clear();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            last = args.getParcelable(TRANSLATE);

            Word wordFrom = last.getFrom();
            from = wordFrom.getLanguage();
            tvFrom.setText(from.getLabel());
            etTrText.setText(wordFrom.getText());

            Word wordTo = last.getTo();
            to = wordTo.getLanguage();
            tvTo.setText(to.getLabel());
            tvTranslate.setText(wordTo.getText());

            ibAddFavorite.setChecked(last.isFavorite());

            dictionaryListener = new ResponseListener<Dictionary>() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Невозможно скачать статью из Яндекс.Словарь, попрбуйте позже",
                            Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Can't download the article from the dictionary " + error);
                }

                @Override
                public void onResponse(Dictionary response) {
                    article.setText(DictionaryDecorator.decorate(response));
                }
            };
            api.dictionary(last.getFrom(), to, dictionaryListener);
        }

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });

        ibAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (last.getFrom().getText().isEmpty() ||
                        last.getId() == 0) {
                    Toast.makeText(getContext(), "Задан неккоректный перевод, пожалуста нажмите на кнопку для перевода" +
                                    "или ожидайте ответа от сервера"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!last.isFavorite()) {
                    dbService.makeFavorite(last);
                    Toast.makeText(getContext(), "Перевод " + last + " добавлен в избранное", Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, "Translate " + last + " added to favorite");
                } else {
                    dbService.makeUnFavorite(last);
                    Toast.makeText(getContext(), "Перевод " + last + " убран из избранного", Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, "Translate " + last + " removed from favorite");
                }
                ibAddFavorite.setChecked(!last.isFavorite());
                last.setFavorite(!last.isFavorite());
            }
        });

        ibSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Language temp = from;
                from = to;
                to = temp;

                tvFrom.setText(from.getLabel());
                tvTo.setText(to.getLabel());
                Log.i(LOG_TAG, "Switch language state: from = " + from + " to = " + to);
            }
        });

        tvFrom.setOnClickListener(this);
        tvTo.setOnClickListener(this);

        trl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });
        return view;
    }

    private void translate() {
        if (hasConnection(getContext())) {
            String text = etTrText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(getContext(), "Введена пустая строка", Toast.LENGTH_SHORT).show();
            } else {
                Word word = new Word(text, from);
                api.translate(word, to, TranslateFragment.this);
                api.dictionary(word, to, dictionaryListener);
            }
        } else {
            failedTranslate();
            Log.e(LOG_TAG, "No internet connection for translate");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static Fragment newInstance(Translate field, ArrayList<Language> langs) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(TRANSLATE, field);
        arguments.putParcelableArrayList(LANGS_KEY, langs);

        TranslateFragment fragment = new TranslateFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        Language curr;
        if (view.getId() == R.id.tv_from) {
            intent.setAction(FROM_KEY);
            curr = from;
        } else {
            intent.setAction(TO_KEY);
            curr = to;
        }
        intent.setClass(getContext(), SwitchLanguageActivity.class);
        intent.putParcelableArrayListExtra(LANGS_KEY, langs);
        intent.putExtra(CURR_LANG_KEY, curr);
        startActivityForResult(intent, SWITCH_LANGUAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SWITCH_LANGUAGE) {
            if (resultCode == RESULT_OK) {
                String action = data.getAction();
                Language language = data.getParcelableExtra(LANG_KEY);
                if (action.equals(FROM_KEY)) {
                    from = language;
                    tvFrom.setText(from.getLabel());
                } else {
                    to = language;
                    tvTo.setText(to.getLabel());
                }
                Log.i(LOG_TAG, "Switch language state: from = " + from + " to = " + to);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showAlert(getContext(), "Произошла ошибка во время перевода текста, пожалуйста попробуйте позже");
    }

    @Override
    public void onResponse(Pair<ApiCode, Translate> response) {
        ApiCode code = response.first;
        Translate field = response.second;
        if (code == ApiCode.OK) {
            Word to = field.getTo();
            String text = to.getText();

            addTranslate(field);
            last = field;

            tvTranslate.setText(text);
            btnRepeat.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
            ibAddFavorite.setChecked(field.isFavorite());
        } else {
            failedTranslate();
            Log.e(LOG_TAG, "Failed translate text " + field.getFrom() + " with code " + code);
        }
    }

    private void addWord(Word word) {
        long id = dbService.findWord(word);
        if (id > 0) {
            word.setId(id);
        } else {
            id = dbService.addWord(word);
            word.setId(id);
            Log.i(LOG_TAG, "Add new word " + word);
        }
    }

    private void addTranslate(Translate field) {
        addWord(field.getFrom());
        addWord(field.getTo());

        long idField = dbService.findTranslate(field);
        field.setId(idField);
        if (idField > 0) {
            field.setFavorite(dbService.isFavoriteTranslate(idField));
            dbService.deleteTranslateField(field);
        }
        long id = dbService.addTranslate(field);
        field.setId(id);
        Log.i(LOG_TAG, "Add new translate " + field);
    }

    private void failedTranslate() {
        tvTranslate.setText("");
        tvError.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.VISIBLE);
    }
}
