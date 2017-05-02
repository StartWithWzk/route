package com.qg.route.routemap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qg.route.R;
import com.qg.route.utils.Constant;

import java.util.List;

/**
 * Created by Ricco on 2017/4/27.
 */

public class RouteSelectedAdapter extends RecyclerView.Adapter<RouteSelectedAdapter.ViewHolder> {

    private Context mContext;
    private int mResource;
    private int mData;
    private int clickItem;
    private OnClickListener mListener;

    interface OnClickListener {
        void onClick(View v, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mListener = onClickListener;
    }

    public RouteSelectedAdapter(Context context, int resource, int num) {
        mContext = context;
        mResource = resource;
        mData = num;
    }

    public void setClickItem(int position) {
        clickItem = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder =
                new ViewHolder(LayoutInflater.from(mContext).inflate(mResource, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText("路线" + (position + 1));
        // 特殊处理
        if (clickItem == position) {
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.font_white));
            holder.mLL.setBackgroundResource(R.drawable.rect_round_blue);
        } else {
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.font_gray));
            holder.mLL.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.mLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickItem(position);
                notifyDataSetChanged();
                mListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData;
    }


    public void notifyNumChanged(int num) {
        this.mData = num;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLL;
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mLL = (LinearLayout) itemView.findViewById(R.id.ll_item_select);
            mTextView = (TextView) itemView.findViewById(R.id.tv_route_select);
        }
    }
}
