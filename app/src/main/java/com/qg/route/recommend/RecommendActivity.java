package com.qg.route.recommend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.qg.route.BaseActivity;
import com.qg.route.R;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;
import com.qg.route.utils.JsonUtil;
import com.qg.route.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Ricco on 2017/5/6.
 * 推荐好友页面
 */

public class RecommendActivity extends BaseActivity implements RecommendAdapter.OnItemSelectedListener {

    private static final String TAG = "RECOMMENDACTIVITY";
    private Toolbar mToolbar;
    private RecyclerView mRV;

    // 数据
    private ArrayList<User> mUserList;
    private RecommendAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        initToolBar();
        initRecyclerView();
        initData();
    }

    private void initData() {
        HttpUtil.DoGet(Constant.RECOMMEND_FRIEND, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                ResponseBody body1 = response.body();
                if (body1 != null) {
                    RequestResult requestResult1 = JsonUtil.toObject(body1.charStream(), RequestResult.class);
                    Log.d(TAG, "onSuccess: " + requestResult1.getStateInfo());
                    ArrayList<User> temp = JsonUtil.toObjectList(requestResult1.getData()
                            , new TypeToken<ArrayList<User>>(){}.getType());
                    mUserList.clear();
                    mUserList.addAll(temp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
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
        }, false);
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.tb_recommend);
        mToolbar.setTitle("推荐好友");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
        mRV = (RecyclerView) findViewById(R.id.rv_recommend);
        mRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mUserList = new ArrayList<User>();
        mRV.setAdapter(mAdapter = new RecommendAdapter(this, mUserList));
        mAdapter.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RecommendActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(View view, final int position) {
        new AlertDialog.Builder(this).setTitle("系统提醒")
                .setMessage("是否请求添加为好友？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpUtil.DoGet(Constant.InformationUrl.INFORMATION_ADD + mUserList.get(position).getUserid(),
                                new HttpUtil.HttpConnectCallback() {
                            @Override
                            public void onSuccess(Response response) {
                                ResponseBody body = response.body();
                                if (body != null) {
                                    final RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 显示添加好友信息反馈
                                            ToastUtil.show(RecommendActivity.this, requestResult.getStateInfo());
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
                        }, false);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(true)
                .show();
    }
}
