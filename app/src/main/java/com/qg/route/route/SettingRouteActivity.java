package com.qg.route.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
    private RecyclerView resultList;
    private EditText searchText;
    RouteSearchAdapter mAdapter;
    private List<String> mData = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_route);
        resultList = (RecyclerView) findViewById(R.id.rl_result);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        resultList.setAdapter(mAdapter = new RouteSearchAdapter());

        searchText = (EditText) findViewById(R.id.et_search);
        searchText.setText(getIntent().getStringExtra(RouteActivity.ROUTE_NAME));
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

    @Override
    public void onGetInputtips(List<Tip> list, int i) {

        mData.clear();
        for (int j = 0; j < list.size(); j++) {
            mData.add(list.get(j).getName());
        }
        Log.d("tip", "onGetInputtips: " + mData.toString());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class RouteSearchAdapter extends RecyclerView.Adapter<RouteSearchAdapter.MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(SettingRouteActivity.this)
                    .inflate(R.layout.item_route_result, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.tv_route_name.setText(mData.get(position).toString());
            holder.tv_route_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(RouteActivity.ROUTE_NAME, holder.tv_route_name.getText().toString());
                    setResult(1, intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_route_name;
            public MyViewHolder(View itemView) {
                super(itemView);
                tv_route_name = (TextView) itemView.findViewById(R.id.tv_route_name);
            }
        }
    }
}
