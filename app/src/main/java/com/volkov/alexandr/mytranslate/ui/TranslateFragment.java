package com.volkov.alexandr.mytranslate.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.volkov.alexandr.mytranslate.R;
import com.volkov.alexandr.mytranslate.api.Language;
import com.volkov.alexandr.mytranslate.api.ResponseListener;
import com.volkov.alexandr.mytranslate.api.ApiCode;
import com.volkov.alexandr.mytranslate.api.YandexApi;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.volkov.alexandr.mytranslate.LogHelper.makeLogTag;
import static com.volkov.alexandr.mytranslate.ui.SwitchLanguage.LANG_KEY;


public class TranslateFragment extends Fragment implements View.OnClickListener,
        ResponseListener<Pair<ApiCode, String>> {
    private static final String LOG_TAG = makeLogTag(TranslateFragment.class);

    public static final int SWITCH_LANGUAGE = 1;
    public static final String FROM_KEY = "from";
    public static final String TO_KEY = "to";
    public static final String TEXT_KEY = "text";
    public static final String CURR_LANG_KEY = "curr_lang";
    public static final String LANGS_KEY = "langs";

    private Language from;
    private Language to;
    private ArrayList<Language> langs;
    private YandexApi api;

    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvTranslate;
    private TextView tvError;
    private Button btnRepeat;

    public TranslateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new YandexApi(getContext());

        Bundle args = getArguments();
        if (args != null) {
            from = args.getParcelable(FROM_KEY);
            to = args.getParcelable(TO_KEY);
            langs = args.getParcelableArrayList(LANGS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        tvFrom = (TextView) view.findViewById(R.id.tv_from);
        tvTo = (TextView) view.findViewById(R.id.tv_to);
        tvTranslate = (TextView) view.findViewById(R.id.tv_translate);
        btnRepeat = (Button) view.findViewById(R.id.btn_repeat);
        tvError = (TextView) view.findViewById(R.id.tv_error);
        ImageButton ibSwap = (ImageButton) view.findViewById(R.id.ib_swap);

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

        final EditText etTrText = (EditText) view.findViewById(R.id.et_trtext);
        etTrText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                                (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                            if (event == null || !event.isShiftPressed()) {
                                translate(v.getText().toString());
                                return true;
                            }
                        }
                        return false;
                    }
                });

        ImageButton trl = (ImageButton) view.findViewById(R.id.ib_translate);
        trl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               translate(etTrText.getText().toString());
            }
        });
        return view;
    }

    private void translate(String text) {
        if (hasConnection(getContext())) {
            api.translate(from, to, text, TranslateFragment.this);
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

    public static Fragment newInstance(Parcelable from, Parcelable to, ArrayList<Language> langs) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(FROM_KEY, from);
        arguments.putParcelable(TO_KEY, to);
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
        intent.setClass(getContext(), SwitchLanguage.class);
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

    }

    @Override
    public void onResponse(Pair<ApiCode, String> response) {
        ApiCode code = response.first;
        if (code == ApiCode.OK) {
            String text = response.second;
            tvTranslate.setText(text);
            btnRepeat.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        } else {
            failedTranslate();
            Log.e(LOG_TAG, "Failed translate text with code " + code);
        }
    }

    private void failedTranslate() {
        tvTranslate.clearComposingText();
        tvError.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.VISIBLE);
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}
