package com.qg.route.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Ricco on 2017/4/19.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragment;
    private ArrayList<String> mNames;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragment = new ArrayList<>();
        mNames = new ArrayList<>();
    }

    public void addFragment(String name, Fragment fragment) {
        mNames.add(name);
        mFragment.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNames.get(position);
    }
}
