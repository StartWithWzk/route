package com.qg.route.routemap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.qg.route.BaseActivity;
import com.qg.route.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricco on 2017/4/15.
 */

public class SettingRouteActivity extends BaseActivity implements Inputtips.InputtipsListener {

    // widget
    private RecyclerView resultList;
    private EditText searchText;
    private Toolbar mToolbar;

    // adapter
    RouteSearchAdapter mAdapter;

    // origin search data
    private String mOriginText;
    // tips
    private List<Tip> mTips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_route);
        // 初始化toolbar
        setUpToolBar();
        // 初始化View
        init();
        setUpEditText();

        mAdapter.setOnItemClickListener(new RouteSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(RouteActivity.ROUTE_NAME, mTips.get(position));
                SettingRouteActivity.this.setResult(1, intent);
                finish();
            }
        });
    }

    private void setUpEditText() {
        searchText = (EditText) findViewById(R.id.et_search);
        searchText.setText(mOriginText);
        searchText.setSelection(mOriginText.length());
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();
                InputtipsQuery inputquery = new InputtipsQuery(newText, "广州");
                inputquery.setCityLimit(true);
                Inputtips inputTips = new Inputtips(SettingRouteActivity.this, inputquery);
                inputTips.setInputtipsListener(SettingRouteActivity.this);
                inputTips.requestInputtipsAsyn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        if (getIntent() != null) {
            mOriginText = getIntent().getStringExtra(RouteActivity.ROUTE_NAME);
        } else {
            mOriginText = "";
        }
        resultList = (RecyclerView) findViewById(R.id.rl_result);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        resultList.setAdapter(
                mAdapter = new RouteSearchAdapter(this, R.layout.item_route_result, new ArrayList<Tip>()));
    }

    private void setUpToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.tb_setting);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {

        if (i == AMapException.CODE_AMAP_SUCCESS) {
            mAdapter.notifyTipsChange(mTips = list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(0);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 快速启动
    public static void actionStartForResult(Activity context, int requestCode, String originText) {
        Intent intent = new Intent(context, SettingRouteActivity.class);
        intent.putExtra(RouteActivity.ROUTE_NAME, originText);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(0);
    }
}
