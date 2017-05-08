package com.qg.route.route;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qg.route.R;
import com.qg.route.bean.Route;
import com.qg.route.custom.DrawRouteView;
import com.qg.route.utils.Constant;

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
        container.addView(initView(mView.get(position), position));
        return mView.get(position);
    }

    // 在此处初始化View
    private View initView(View newView, int position) {
        LinearLayout linearLayout = (LinearLayout) newView.findViewById(R.id.ll_route);
        linearLayout.addView(new DrawRouteView(mContext, mRoute.get(position)));
        ImageView routeTypeImage = (ImageView) newView.findViewById(R.id.iv_route_type);
        TextView routeTypeText = (TextView) newView.findViewById(R.id.tv_route_type);
        switch (mRoute.get(position).getSelectedRouteType()) {
            case Constant.ROUTE_TYPE_WALK:
                Glide.with(mContext).load(R.mipmap.run).into(routeTypeImage);
                routeTypeText.setText("健身");
                break;
            case Constant.ROUTE_TYPE_RIDE:
                Glide.with(mContext).load(R.mipmap.school).into(routeTypeImage);
                routeTypeText.setText("上学");
                break;
            case Constant.ROUTE_TYPE_BUS:
                Glide.with(mContext).load(R.mipmap.school).into(routeTypeImage);
                routeTypeText.setText("上学");
                break;
            case Constant.ROUTE_TYPE_DRIVE:
                Glide.with(mContext).load(R.mipmap.office).into(routeTypeImage);
                routeTypeText.setText("上班");
                break;
            default:
                break;
        }
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
