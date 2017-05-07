package com.qg.route.moments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Mr_Do on 2017/4/26.
 */

public class DataFragment extends Fragment {

    private static final String ID = "com.qg.route.moments.DataFragment.ID";
    private String mGetPersonDataUrl = Constant.MomentsUrl.PERSON_DATA_GET;
    private String mId;
    private User mData;
    private Handler mHandler = new Handler();
    private TextView mSex;
    private TextView mName;
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
        mName = (TextView) view.findViewById(R.id.name_data);
        mIntroduction = (TextView) view.findViewById(R.id.introduce_data);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void updateView() {
        mName.setText(mData.getName());
        mIntroduction.setText(mData.getIntroduction());
        if(mData.getSex() == 1) {
            mSex.setText("男");
        }else{
            mSex.setText("女");
        }
    }

    private void initData() {
        mId = getArguments().getString(ID);
        getPersonData();
    }

    private void getPersonData() {
        HttpUtil.DoGet(mGetPersonDataUrl + mId,
                new HttpUtil.HttpConnectCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        Gson gson = new Gson();
                        RequestResult<User> result = null;
                        try {
                            result = gson.fromJson(response.body().charStream(), new TypeToken<RequestResult<User>>(){}.getType());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(result != null && result.getState() == 145){
                            mData = result.getData();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateView();
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(IOException e) {

                    }
                    @Override
                    public void onFailure() {
                    }
                },false);
    }
}
