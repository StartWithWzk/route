package com.qg.route.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.qg.route.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr_Do on 2017/4/29.
 */

public class ContactsActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private final static String TAB_FRIEND = "好友";
    private final static String TAB_FRIENDS = "圈子";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mTabLayout = (TabLayout) findViewById(R.id.contacts_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.contacts_view_pager);

        intiUI();
    }

    private void intiUI() {
        ContactsPagerAdapter adapter = new ContactsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendListFragment(), TAB_FRIEND);
        adapter.addFragment(new CircleListFragment() , TAB_FRIENDS);
        mViewPager.setAdapter(adapter);
        mTabLayout.addTab(mTabLayout.newTab().setText("ONE"));
        mTabLayout.addTab(mTabLayout.newTab().setText("TWO"));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class ContactsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public ContactsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment ,String name){
            mFragments.add(fragment);
            mFragmentTitles.add(name);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
