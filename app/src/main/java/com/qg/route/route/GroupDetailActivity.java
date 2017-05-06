package com.qg.route.route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.qg.route.BaseActivity;
import com.qg.route.R;
import com.qg.route.bean.ChatRoom;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;
import com.qg.route.utils.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Ricco on 2017/5/2.
 */

public class GroupDetailActivity extends BaseActivity implements View.OnClickListener {
    public static final String GROUP_START_KEY = "ROUTE_START";
    private static final String TAG = "GROUPDETAILACTIVITY";

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView mGroupName;
    private TextView mGroupDetail;
    private Button mGroupAdd;

    // 头像适配器
    private UserHeadAdapter mUserHeadAdapter;

    private int groupId;
    private ChatRoom mChatRoom;
    private ArrayList<User> mUserList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        // 获取启动活动端传来的数据
        Intent intent = getIntent();
        if (intent != null) {
            groupId = intent.getIntExtra(GROUP_START_KEY, 0);
        } else {
            groupId = 0;
        }
        Log.d(TAG, "onCreate: " + groupId);
        initToolbar();
        initView();
        initDate();
    }

    /**
     * 初始化列表数据
     */
    private void initDate() {
        HttpUtil.DoGet(Constant.GroupUrl.GROUP_INFOMATION + groupId, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                final ResponseBody body = response.body();
                if (body != null) {
                    RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                    Log.d(TAG, "onSuccess: " + requestResult.getStateInfo());
                    if (requestResult.getState() == 177) {
                        if (requestResult.getData() != null) { // 数据不为空，说明圈子含有数据
                            mChatRoom = JsonUtil.toObject(requestResult.getData().toString(), ChatRoom.class);
                            HttpUtil.DoGet(Constant.GroupUrl.GROUP_GET_LIST + groupId,
                                    new HttpUtil.HttpConnectCallback() {
                                        @Override
                                        public void onSuccess(Response response) {
                                            ResponseBody body1 = response.body();
                                            if (body1 != null) {
                                                RequestResult requestResult1 = JsonUtil.toObject(body1.charStream(), RequestResult.class);
                                                ArrayList<User> temp = JsonUtil.toObjectList(requestResult1.getData()
                                                                    , new TypeToken<ArrayList<User>>(){}.getType());
                                                mUserList.clear();
                                                mUserList.addAll(temp);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mGroupName.setText(mChatRoom.getRoomName());
                                                        mGroupDetail.setText(mChatRoom.getDestination());
                                                        mUserHeadAdapter.notifyDataSetChanged();

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
                    }
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

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tb_group_detail);
        mToolbar.setTitle("圈子介绍");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_user_all);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5, LinearLayoutManager.VERTICAL, false));
        mUserList = new ArrayList<>();
        mRecyclerView.setAdapter(mUserHeadAdapter = new UserHeadAdapter(this, R.layout.item_user_headview, mUserList));
        mGroupAdd = (Button) findViewById(R.id.btn_group_add);
        mGroupAdd.setOnClickListener(this);
        mGroupName = (TextView) findViewById(R.id.tv_group_name);
        mGroupDetail = (TextView) findViewById(R.id.tv_group_detail);
    }

    public static void actionStart(Context context, int groupId) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(GROUP_START_KEY, groupId);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (mChatRoom != null) {
            // 应该加入圈子,这里还应该判断我以前是否加入过此圈子
            HttpUtil.DoGet(Constant.GroupUrl.GROUP_ALL, new HttpUtil.HttpConnectCallback() {
                @Override
                public void onSuccess(Response response) {
                    Log.d(TAG, "onSuccess: GROUP_ALL");
                    ResponseBody body = response.body();
                    if (body != null) {
                        final RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                        if (requestResult.getState() == 301) {
                            ArrayList<ChatRoom> mChatRoomList = JsonUtil.toObjectList(requestResult.getData(),
                                    new TypeToken<ArrayList<ChatRoom>>() {
                                    }.getType());
                            for (ChatRoom chatRoom : mChatRoomList
                                    ) {
                                if (chatRoom.getId() == mChatRoom.getId()) { // 说明我曾经加入过此圈子
                                    // TODO: 2017/5/6 此处直接圈子即可
                                    return;
                                }
                            }
                            // 到达此处说明我没有加入过此圈子，于是请求加入
                            HttpUtil.DoGet(Constant.GroupUrl.GROUP_ADD + mChatRoom.getId(), new HttpUtil.HttpConnectCallback() {
                                @Override
                                public void onSuccess(Response response) {
                                    ResponseBody body = response.body();
                                    if (body != null) {
                                        RequestResult requestresult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                                        if (requestresult.getState() == 174) {
                                            Log.d(TAG, "onSuccess: " + requestresult.getStateInfo());
                                            // TODO: 2017/5/6 此处跳转圈子
                                        }
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
    }
}
