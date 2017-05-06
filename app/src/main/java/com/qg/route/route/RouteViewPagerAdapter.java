package com.qg.route.route;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qg.route.R;
import com.qg.route.bean.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricco on 2017/5/2.
 */

public class RouteViewPagerAdapter extends PagerAdapter {

    private List<Route> mRoute;
    private Context mContext;
    private ArrayList<View> mView;

    public RouteViewPagerAdapter(List<Route> mRoute, Context context) {
        this.mRoute = mRoute;
        mContext = context;
        mView = new ArrayList<>();
        notifyViewChanged(mRoute);
    }

    public void notifyViewChanged(List<Route> mRoute) {
        mView.clear();
        for (int i=0; i<mRoute.size(); i++
                ) {
            View newView = LayoutInflater.from(mContext).inflate(R.layout.page_route, null);
            mView.add(newView);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(initView(mView.get(position)));
        return mView.get(position);
    }

    // 在此处初始化View
    private View initView(View newView) {

        return newView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mView.get(position));
    }

    @Override
    public int getCount() {
        return mView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
