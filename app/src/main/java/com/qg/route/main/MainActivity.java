package com.qg.route.main;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.qg.route.BaseActivity;
import com.qg.route.R;
import com.qg.route.bean.RequestResult;
import com.qg.route.chat.ChatListFragment;
import com.qg.route.chat.ChatService;
import com.qg.route.contacts.FriendListFragment;
import com.qg.route.login.LoginActivity;
import com.qg.route.moments.MomentsFragment;
import com.qg.route.route.RouteFragment;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;
import com.qg.route.utils.JsonUtil;
import com.qg.route.utils.URLHelper;

import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION = 0;

    // 待申请权限组
    private static final String[] PERMISSION_LIST = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    // widget
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mViewpager;
    private NavigationView mNView;

    // adapter
    private MainPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 要先登录
        login();

        setUpToolbar();
        setUpDrawer();
        setUpNavigation();
    }

    private void login() {
        HttpUtil.PostMap(Constant.UserUrl.LOGIN, URLHelper.sendLogin(Constant.USER_ID, Constant.PASSWORD)
                , new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                ResponseBody body = response.body();
                if (body != null && response.code() != 404) {
                    RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                    if (requestResult.getState() != 121) {
                        Log.d(TAG, "onSuccess: 登录失败！");
                        finish();
                    }
                    // 表单头
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    if(cookies != null && cookies.size()>0) {
                        // 取得Session
                        String session = cookies.get(0);
                        HttpUtil.setSession(session);
                        Log.d(TAG, "onSuccess: login success!\nsession: " + session);
                        Intent intent = ChatService.newIntent(MainActivity.this);
                        startService(intent);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setUpViewPage();
                        }
                    });
                } else {
                    Log.d(TAG, "onSuccess: 网络连接存在问题，请退出后重试");
                    finish();
                }
            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        } , false);
    }

    /**
     * 设置Toolbar
     */
    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mToolbar);
    }

    /**
     * 设置ViewPage
     */
    private void setUpViewPage() {
        // viewpager setting
        mViewpager = (ViewPager) findViewById(R.id.vp_main);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment("路线", new RouteFragment());
        mPagerAdapter.addFragment("动态", MomentsFragment.newInstance(Constant.USER_ID));
        mPagerAdapter.addFragment("聊聊", new FriendListFragment());
        mViewpager.setAdapter(mPagerAdapter);

        // viewpager tabs
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_main);
        mTabs.setTextColor(getResources().getColor(R.color.toolbarTextColor));
        mTabs.setTextSize(36);
        mTabs.setDividerColor(Color.TRANSPARENT);
        mTabs.setUnderlineHeight(0);
        mTabs.setIndicatorColor(getResources().getColor(R.color.toolbarTextColor));
        mTabs.setIndicatorHeight(12);
        mTabs.setTabPaddingLeftRight(48);
        mTabs.setViewPager(mViewpager);
    }

    /**
     * 设置DrawerLayout
     */
    private void setUpDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        }.syncState();
    }

    private void setUpNavigation() {
        mNView = (NavigationView) findViewById(R.id.navigation_view);
        mNView.setItemIconTintList(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestPermission(REQUEST_PERMISSION, PERMISSION_LIST);
    }
}
