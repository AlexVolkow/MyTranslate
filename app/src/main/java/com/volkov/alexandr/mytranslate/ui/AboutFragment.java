package com.volkov.alexandr.mytranslate.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.volkov.alexandr.mytranslate.R;
import org.w3c.dom.Text;


public class AboutFragment extends Fragment {
    public AboutFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        TextView developer = (TextView) view.findViewById(R.id.tv_developer);
        TextView api = (TextView) view.findViewById(R.id.tv_yandex_translate_api);
        developer.setMovementMethod(LinkMovementMethod.getInstance());
        api.setMovementMethod(LinkMovementMethod.getInstance());
        developer.setText(Html.fromHtml(getString(R.string.inf_developer)));
        api.setText(Html.fromHtml(getString(R.string.inf_yandex_translate_api)));
        return view;
    }

}
