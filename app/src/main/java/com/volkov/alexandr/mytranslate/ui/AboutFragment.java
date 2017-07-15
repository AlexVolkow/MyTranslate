package com.volkov.alexandr.mytranslate.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.volkov.alexandr.mytranslate.R;


public class AboutFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.tv_developer)
    TextView developer;

    @BindView(R.id.tv_yandex_translate_api)
    TextView api;

    public AboutFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        developer.setMovementMethod(LinkMovementMethod.getInstance());
        api.setMovementMethod(LinkMovementMethod.getInstance());

        developer.setText(Html.fromHtml(getString(R.string.inf_developer)));
        api.setText(Html.fromHtml(getString(R.string.inf_yandex_translate_api)));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
