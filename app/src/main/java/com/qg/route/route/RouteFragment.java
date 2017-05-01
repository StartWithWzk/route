package com.qg.route.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qg.route.R;
import com.qg.route.animition.ZoomOutPageTransformer;
import com.qg.route.bean.Trace;
import com.qg.route.bean.XYPoint;
import com.qg.route.routemap.RouteActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricco on 2017/4/18.
 */

public class RouteFragment extends Fragment{

    public final static int RESULT_ROUTE_SUCCESS = 1;
    public final static int RESULT_ROUTE_FAILURE = 0;
    public final static int REQUEST_ROUTE = 0;
    public final static String TRACE = "TRACE";

    private ArrayList<String> mUserName = new ArrayList<>();

    private ViewPager mViewPage;
    private RecyclerView mUserHeadRV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        setHasOptionsMenu(true);
        setUpViewPage(view);
        initView(view);

        return view;
    }

    private void initView(View view) {
        for (int i = 0; i < 100; i++) {
            mUserName.add(i + "");
        }
        // 这两行代码需要后期改进，考虑Recycleview的多种Viewholder情况
        NestedScrollView nestview = (NestedScrollView) view.findViewById(R.id.nsv_route);
        nestview.smoothScrollTo(0, 0);

        mUserHeadRV = (RecyclerView) view.findViewById(R.id.rv_user_head);
        // 设置为横向
        mUserHeadRV.setLayoutManager(
                new GridLayoutManager(getActivity(), 5));
        mUserHeadRV.setAdapter(new UserHeadAdapter(getActivity(), R.layout.item_user_headview, mUserName));
    }

    private ArrayList<View> mImag = new ArrayList<>();

    private void setUpViewPage(View view) {

        for (int i=0; i<5; i++) {
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.page_route, null);
            mImag.add(view1);
        }

        mViewPage = (ViewPager) view.findViewById(R.id.vp_route);
        mViewPage.setPageMargin(48);
        mViewPage.setOffscreenPageLimit(3);
        mViewPage.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPage.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mImag.get(position));
                mImag.get(position).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RouteActivity.actionStartForResult(getActivity(), RouteFragment.this, REQUEST_ROUTE);
                    }
                });
                return mImag.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImag.get(position));
            }

            @Override
            public int getCount() {
                return mImag.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_route, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_ROUTE_SUCCESS) {
            return;
        }
        if (requestCode == REQUEST_ROUTE) {
            Trace trace = data.getParcelableExtra(TRACE);
            if (trace != null) {
                // TODO: 2017/4/30
                List<XYPoint> list = trace.getPoint();
            }
        }
    }
}
