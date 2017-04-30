package com.qg.route.moments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qg.route.R;

/**
 * Created by Mr_Do on 2017/4/26.
 */

public class DataFragment extends Fragment {

    private static final String ID = "com.qg.route.moments.DataFragment.ID";
    private static final String DATA_URL = "";
    private String mId;

    private TextView mSex;
    private TextView mSite;
    private TextView mAge;
    private TextView mIntroduction;

    public static DataFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ID , id);
        DataFragment fragment = new DataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data , container , false);

        mSex = (TextView) view.findViewById(R.id.sex_data);
        mSite = (TextView) view.findViewById(R.id.site_data);
        mAge = (TextView) view.findViewById(R.id.age_data);
        mIntroduction = (TextView) view.findViewById(R.id.introduce_data);

        initData();
        initView();
        return view;
    }

    private void initView() {

    }

    private void initData() {
        mId = getArguments().getString(ID);
        // TODO: 2017/4/29 加入网络操作
    }


}
