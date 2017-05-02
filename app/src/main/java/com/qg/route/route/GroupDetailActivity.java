package com.qg.route.route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.qg.route.BaseActivity;
import com.qg.route.R;

import java.util.ArrayList;

/**
 * Created by Ricco on 2017/5/2.
 */

public class GroupDetailActivity extends BaseActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView mGroupName;
    private TextView mGroupDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tb_group_detail);
        mToolbar.setTitle("圈子介绍");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        ArrayList<String> mUserName = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mUserName.add(i + "");
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_user_all);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new UserHeadAdapter(this, R.layout.item_user_headview, mUserName, false));
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        context.startActivity(intent);
    }
}
