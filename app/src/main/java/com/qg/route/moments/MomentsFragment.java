package com.qg.route.moments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.Trends;
import com.qg.route.bean.User;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Response;


/**
 * Created by Mr_Do on 2017/4/25.
 */

public class MomentsFragment extends Fragment {

    private static String mId = null;
    private static String mName = null;
    private User mData;
    private static final String ID = "com.qg.route.moments.MomentsFragment.ID";
    private String mFriendImageUrl = Constant.MomentsUrl.LOAD_MOMENT_IMAGE;
    private String mHeadImageUrl = Constant.MomentsUrl.PERSON_HEAD_IMAGE;
    private String mDeleteMomentsUrl = Constant.MomentsUrl.DELETE_MOMENT;
    private String mGetPersonDataUrl = Constant.MomentsUrl.PERSON_DATA_GET;
    private static final String MOMENTS_URL = Constant.MomentsUrl.MOMENT_GET;

    private Handler mHandle;
    private List<Trends> mTrendList = new ArrayList<>();
    private MomentsAdapter mAdapter;
    private Handler mHandler = new Handler();

    public static MomentsFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ID , id);
        MomentsFragment fragment = new MomentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isRefreshing = false;
    private boolean firstInit = true;
    private RecyclerView mMomentList;
    private RelativeLayout mMomentsFragmentLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moments , container , false);
        mMomentList = (RecyclerView) view.findViewById(R.id.moments_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.moments_swipe);
        mMomentsFragmentLayout = (RelativeLayout) view.findViewById(R.id.empty_layout);
        mHandle = new Handler();

        initUI();
        return view;
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    private void initData(){
        mId = getArguments().getString(ID);
        HttpUtil.DoGet(MOMENTS_URL + Constant.USER_ID + "/" + new Date().getTime(), new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {

                stopReFresh();
                mTrendList.clear();
                if(response != null) {
                    RequestResult<List<Trends>> requestResult = null;
                    try {
                        Gson gson = new Gson();
                        requestResult = gson.fromJson(response.body().charStream(), RequestResult.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (requestResult != null && requestResult.getState() == 192) {
                        firstInit = false;
                        Gson gson = new Gson();
                        String json = gson.toJson(requestResult.getData(), new TypeToken<List<Trends>>() {
                        }.getType());
                        JsonArray jsonElements = new JsonParser().parse(json).getAsJsonArray();
                        for (JsonElement element : jsonElements) {
                            mTrendList.add(gson.fromJson(element, Trends.class));
                        }
                        Collections.sort(mTrendList, new Comparator<Trends>() {
                            @Override
                            public int compare(Trends trends, Trends t1) {
                                int result = 0;
                                if (trends.getSendTime() > t1.getSendTime()) {
                                    result = -1;
                                } else if (trends.getSendTime() == (t1.getSendTime())) {
                                    result = 0;
                                } else {
                                    result = 1;
                                }
                                return result;
                            }
                        });
                        mHandle.post(new Runnable() {
                            @Override
                            public void run() {
                                if(isRefreshing){
                                    isRefreshing = false;
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                mAdapter.notifyDataSetChanged();
                                setEmptyView();
                            }
                        });
                    }
                }
            }
            @Override
            public void onFailure(IOException e) {
                stopReFresh();
            }

            @Override
            public void onFailure() {

            }
        },false);
    }

    private void setEmptyView(){
        if(mTrendList.size() == 0){
            mMomentsFragmentLayout.setVisibility(View.VISIBLE);
        }else {
            mMomentsFragmentLayout.setVisibility(View.GONE);
        }
    }

    private void stopReFresh(){
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                if(isRefreshing){
                    isRefreshing = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void getPersonData(final MomentsHolder holder) {
        HttpUtil.DoGet(mGetPersonDataUrl + mId,
                new HttpUtil.HttpConnectCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        Gson gson = new Gson();
                        RequestResult<User> result = null;
                        try {
                            result = gson.fromJson(response.body().charStream(), new TypeToken<RequestResult<User>>(){}.getType());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(result != null && result.getState() == 145){
                            mData = result.getData();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.mNameText.setText(mData.getName());
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
                },false);
    }

    private class MomentsAdapter extends RecyclerView.Adapter<MomentsHolder>{

        @Override
        public MomentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_moments , parent , false);
            return new MomentsHolder(view);
        }

        @Override
        public void onBindViewHolder(MomentsHolder holder, int position) {
            holder.bindView(mTrendList.get(position) , position);
            getPersonData(holder);
        }

        @Override
        public int getItemCount() {
            return mTrendList.size();
        }
    }

    private class MomentsHolder extends RecyclerView.ViewHolder{
        private TextView mText;
        private ImageView mImageView;
        private ImageView mPersonImage;
        private ImageView mDeleteButton;
        private TextView mNameText;
        private TextView mDate;
        private ImageButton mPraiseButton;
        private ImageButton mCommentButton;
        private RecyclerView mRecyclerView;
        public MomentsHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.item);
            mImageView = (ImageView) itemView.findViewById(R.id.moments_image);
            mPraiseButton = (ImageButton) itemView.findViewById(R.id.button_praise);
            mCommentButton = (ImageButton) itemView.findViewById(R.id.button_comment);
            mPersonImage = (ImageView) itemView.findViewById(R.id.moments_person_image);
            mNameText = (TextView) itemView.findViewById(R.id.moments_name);
            mDate = (TextView) itemView.findViewById(R.id.moments_time);
            mDeleteButton = (ImageView) itemView.findViewById(R.id.moments_delete_button);
        }

        private void deleteMoments(String id , final int position){
            HttpUtil.DoGet(mDeleteMomentsUrl + id,
                    new HttpUtil.HttpConnectCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            mHandle.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTrendList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                    mAdapter.notifyDataSetChanged();
                                    setEmptyView();
                                }
                            });
                        }

                        @Override
                        public void onFailure(IOException e) {

                        }

                        @Override
                        public void onFailure() {

                        }
                    } , false);
        }


        public void bindView(final Trends trends , final int position){
            mText.setText(trends.getMessage());
            Date date = new Date(trends.getSendTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-mm-dd hh:mm");
            mDate.setText(dateFormat.format(date));
            mNameText.setText("某个吃瓜群众");
            ChatGlideUtil.loadImageByUrl(MomentsFragment.this , mFriendImageUrl+ trends.getId()+"_1.jpg" , mImageView , R.drawable.normal_person_image);
            ChatGlideUtil.loadImageByUrl(MomentsFragment.this , mHeadImageUrl + mId + ".jpg" , mPersonImage , R.drawable.normal_person_image );
            mDeleteButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    deleteMoments(trends.getId()+"" , position);
                    return true;
                }
            });
        }
    }

    private void initUI() {
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!firstInit) {
                    isRefreshing = true;
                    initData();
                }
            }
        });
        mAdapter = new MomentsAdapter();
        mMomentList.setLayoutManager(new LinearLayoutManager(getActivity()){
            int[] mMeasuredDimension = new int[2];
            @Override
            public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
                final int widthMode = View.MeasureSpec.getMode(widthSpec);
                final int heightMode = View.MeasureSpec.getMode(heightSpec);
                final int widthSize = View.MeasureSpec.getSize(widthSpec);
                final int heightSize = View.MeasureSpec.getSize(heightSpec);

                int width = 0;
                int height = 0;
                for (int i = 0; i < getItemCount(); i++) {
                    measureScrapChild(recycler, i,
                            View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                            mMeasuredDimension);
                    if (getOrientation() == HORIZONTAL) {
                        width = width + mMeasuredDimension[0];
                        if (i == 0) {
                            height = mMeasuredDimension[1];
                        }
                    } else {
                        height = height + mMeasuredDimension[1];
                        if (i == 0) {
                            width = mMeasuredDimension[0];
                        }
                    }
                }
                switch (widthMode) {
                    case View.MeasureSpec.EXACTLY:
                        width = widthSize;
                    case View.MeasureSpec.AT_MOST:
                    case View.MeasureSpec.UNSPECIFIED:
                }
                switch (heightMode) {
                    case View.MeasureSpec.EXACTLY:
                        height = heightSize;
                    case View.MeasureSpec.AT_MOST:
                    case View.MeasureSpec.UNSPECIFIED:
                }
                setMeasuredDimension(width, height);
            }

            private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
                if(recycler != null && recycler.getScrapList() != null && recycler.getScrapList().size() > position) {
                    View view = recycler.getViewForPosition(position);
                    if (view != null) {
                        RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                                getPaddingLeft() + getPaddingRight(), p.width);
                        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                                getPaddingTop() + getPaddingBottom(), p.height);
                        view.measure(childWidthSpec, childHeightSpec);
                        measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                        measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                        recycler.recycleView(view);
                    }
                }
            }

        });
        mMomentList.setAdapter(mAdapter);
    }

}
