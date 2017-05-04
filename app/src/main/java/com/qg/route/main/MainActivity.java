package com.qg.route.main;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.qg.route.BaseActivity;
import com.qg.route.R;
import com.qg.route.bean.Information;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.chat.ChatListFragment;
import com.qg.route.chat.ChatService;
import com.qg.route.contacts.ContactsActivity;
import com.qg.route.information.InformationActivity;
import com.qg.route.moments.MomentsActivity;
import com.qg.route.moments.MomentsFragment;
import com.qg.route.recommend.RecommendActivity;
import com.qg.route.route.RouteFragment;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;
import com.qg.route.utils.JsonUtil;
import com.qg.route.utils.SPUtil;
import com.qg.route.utils.URLHelper;

import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static java.lang.System.exit;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
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
    private ImageView userHead;
    private TextView userName;

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
    }

    // 暂时的登录措施
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
                    User temp = JsonUtil.toObject(JsonUtil.toJson(requestResult.getData()), User.class);
                    if (temp != null) {
                        SPUtil.put(MainActivity.this, Constant.KEY_USER_ID, temp.getUserid());
                        SPUtil.put(MainActivity.this, Constant.KEY_USER_NAME, temp.getName());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setUpViewPage();
                            setUpNavigation();
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
        mPagerAdapter.addFragment("聊聊", new ChatListFragment());
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
        mNView.setNavigationItemSelectedListener(this);
        View headView = mNView.getHeaderView(0);
        userHead = (ImageView) headView.findViewById(R.id.iv_user_head);
        userHead.setOnClickListener(this);
        userName = (TextView) headView.findViewById(R.id.tv_user_name);
        // 设置名字以及头像
        Glide.with(this).load(URLHelper.getPic((int) SPUtil.get(this, Constant.KEY_USER_ID, -1)))
                .error(R.mipmap.head_default)
                .into(userHead);
        userName.setText((CharSequence) SPUtil.get(this, Constant.KEY_USER_NAME, "zhikang_wen"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission(REQUEST_PERMISSION, PERMISSION_LIST);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawers();
        switch (item.getItemId()) {
            case R.id.menu_address:
                Intent intent = new Intent(this , ContactsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_friend_recommend:
                RecommendActivity.actionStart(this);
                return true;
            case R.id.menu_notification:
                intent = new Intent(MainActivity.this , InformationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_order:
                return true;
            case R.id.menu_setting:
                return true;
            case R.id.menu_exit:
                exit(0);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = MomentsActivity.newIntent(Constant.USER_ID , MainActivity.this);
        startActivity(intent);
    }
}
