package com.qg.route.route;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qg.route.R;

import java.util.ArrayList;

/**
 * Created by Ricco on 2017/4/22.
 */

class UserHeadAdapter extends RecyclerView.Adapter<UserHeadAdapter.MyViewHolder> {
    private ArrayList<String> mUserName;
    private LayoutInflater mLayoutInflater;
    private int mResource;

    public UserHeadAdapter(Context context, int item_user_headview, ArrayList<String> mUserName) {
        mLayoutInflater = LayoutInflater.from(context);
        mResource = item_user_headview;
        this.mUserName = mUserName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(mLayoutInflater
                .inflate(mResource, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 4) {
            holder.userHead.setImageResource(R.mipmap.points);
            holder.username.setText("");
        } else {
            holder.userHead.setImageResource(R.mipmap.ic_launcher_round);
            holder.username.setText(mUserName.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mUserName.size() > 5) {
            return 5;
        }
        return mUserName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userHead;
        TextView username;
        public MyViewHolder(View itemView) {
            super(itemView);
            userHead = (ImageView) itemView.findViewById(R.id.iv_user_head);
            username = (TextView) itemView.findViewById(R.id.tv_user_name);
        }
    }
}
