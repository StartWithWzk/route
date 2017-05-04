package com.qg.route.moments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qg.route.R;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by Mr_Do on 2017/5/6.
 */

public class UpdateDataActivity extends AppCompatActivity {
    private EditText mSexEdit;
    private EditText mNameEdit;

    private EditText mIntroductionEdit;
    private Button mFinishButton;
    private Toolbar mToolbar;

    private static final String NAME_KEY = "name";
    private static final String SEX_KEY = "sex";
    private static final String INTRODUCTION_KEY = "introduction";
    private String UpdateDataUrl = Constant.MomentsUrl.UPDATE_DATA;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);
        mSexEdit = (EditText) findViewById(R.id.sex_data_edit);
        mNameEdit = (EditText) findViewById(R.id.name_data_edit);
        mIntroductionEdit = (EditText) findViewById(R.id.introduce_data_edit);
        mFinishButton = (Button) findViewById(R.id.finish_update_button);
        mToolbar = (Toolbar) findViewById(R.id.tb_update_data);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("修改资料");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFinishButton.setEnabled(false);
                if (mSexEdit.getText() != null &&
                        (mSexEdit.getText().toString().equals("男") ||
                                mSexEdit.getText().toString().equals("女"))) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (mNameEdit.getText() != null) {
                        map.put(NAME_KEY, mNameEdit.getText().toString());
                    } else map.put(NAME_KEY, "");
                    if (mIntroductionEdit.getText() != null) {
                        map.put(INTRODUCTION_KEY, mIntroductionEdit.getText().toString());
                    } else map.put(INTRODUCTION_KEY, "");
                    if (mSexEdit.getText().toString().equals("男")) {
                        map.put(SEX_KEY, "1");
                    } else if (mSexEdit.getText().toString().equals("女")) {
                        map.put(SEX_KEY, "0");
                    }

                    HttpUtil.PostMap(UpdateDataUrl, map,
                            new HttpUtil.HttpConnectCallback() {
                                @Override
                                public void onSuccess(Response response) {
                                    finish();
                                }

                                @Override
                                public void onFailure(IOException e) {

                                }

                                @Override
                                public void onFailure() {

                                }
                            }, false);
                }else {
                    mFinishButton.setEnabled(true);
                }
            }
        });

    }
}
