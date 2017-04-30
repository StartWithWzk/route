package com.qg.route.moments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qg.route.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Mr_Do on 2017/4/25.
 */

public class MomentsFragment extends Fragment {

    private static String mId = null;
    private static final String ID = "com.qg.route.moments.MomentsFragment.ID";
    private static final String MOMENTS_URL = "";

    public static MomentsFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ID , id);
        MomentsFragment fragment = new MomentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class testData{
        public String text;
        public ArrayList<String> list;
        public testData(){
            text = new Random().nextInt() + "";
            list = new ArrayList<>();
            for(int i = 0 ; i < new Random().nextInt(5) ; i++){
                list.add(i + "");
            }
        }
    }

    private List<testData> list = new ArrayList<>();
    private RecyclerView mMomentList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moments , container , false);
        mMomentList = (RecyclerView) view.findViewById(R.id.moments_list);
        initData();
        initUI();
        return view;
    }

    private void initData(){
        for(int i = 0 ; i < 20 ; i++){
            testData testData = new testData();
            list.add(testData);
        }
        mId = getArguments().getString(ID);
        // TODO: 2017/4/29 加入网络操作
    }

    private class CommentHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;
        public CommentHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.comment_text);
        }
        public void bindView(String text){
            mTextView.setText(text);
        }

    }

    private class MomentsHolder extends RecyclerView.ViewHolder{
        private TextView mText;
        private RecyclerView mRecyclerView;
        public MomentsHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.item);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.comment_list);
        }

        public void bindView(final testData testData){
            mText.setText(testData.text);
            RecyclerView.Adapter<CommentHolder> adapter = new RecyclerView.Adapter<CommentHolder>() {
                @Override
                public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View view = inflater.inflate(R.layout.item_comment , parent ,false);
                    return new CommentHolder(view);
                }

                @Override
                public void onBindViewHolder(CommentHolder holder, int position) {
                    holder.bindView(testData.list.get(position));
                }

                @Override
                public int getItemCount() {
                    return testData.list.size();
                }

            };
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(adapter);
        }
    }

    private void initUI() {
        RecyclerView.Adapter<MomentsHolder> adapter = new RecyclerView.Adapter<MomentsHolder>() {
            @Override
            public MomentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.item_moments , parent , false);
                return new MomentsHolder(view);
            }

            @Override
            public void onBindViewHolder(MomentsHolder holder, int position) {
                holder.bindView(list.get(position));
            }

            @Override
            public int getItemCount() {
                return list.size();
            }

        };
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
        mMomentList.setAdapter(adapter);
    }

}
