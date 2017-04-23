package com.qg.route.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qg.route.R;
import com.qg.route.animition.ZoomOutPageTransformer;

import java.util.ArrayList;

/**
 * Created by Ricco on 2017/4/18.
 */

public class RouteFragment extends Fragment{

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
        for (int i = 0; i < 10; i++) {
            mUserName.add(i + "");
        }
        mUserHeadRV = (RecyclerView) view.findViewById(R.id.rv_user_head);
        // 设置为横向
        mUserHeadRV.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
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
}
