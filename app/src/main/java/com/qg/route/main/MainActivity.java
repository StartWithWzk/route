package com.qg.route.main;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.qg.route.BaseActivity;
import com.qg.route.R;
import com.qg.route.route.RouteFragment;

public class MainActivity extends BaseActivity {
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

        setUpToolbar();
        setUpViewPage();
        setUpDrawer();
        setUpNavigation();
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
        mPagerAdapter.addFragment("动态", new RouteFragment());
        mPagerAdapter.addFragment("聊聊", new RouteFragment());
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
