package com.qg.route.routemap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.qg.route.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricco on 2017/4/22.
 * adapter
 */

public class RouteSearchAdapter extends RecyclerView.Adapter<RouteSearchAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Tip> mTips;
    private int mResource;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public RouteSearchAdapter(Context context, int resource, List<Tip> tips) {
        mLayoutInflater = LayoutInflater.from(context);
        mResource = resource;
        mTips = tips;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(
                mLayoutInflater.inflate(mResource, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Tip tip = mTips.get(position);
        holder.tv_route_name.setText(tip.getName());
        holder.tv_route_address.setText(tip.getDistrict() + " " + tip.getAddress());
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTips.size();
    }

    public void notifyTipsChange(List<Tip> tip) {
        mTips = tip;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_item;
        TextView tv_route_name;
        TextView tv_route_address;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item_route_result);
            tv_route_name = (TextView) itemView.findViewById(R.id.tv_route_name);
            tv_route_address = (TextView) itemView.findViewById(R.id.tv_route_address);
        }
    }
}
