package com.qg.route.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qg.route.R;

/**
 * Created by Ricco on 2017/4/18.
 */

public class RouteFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        TextView textView = new TextView(getActivity());
        textView.setText("hahahah");
        textView.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.addView(textView);

        return linearLayout;
    }
}
