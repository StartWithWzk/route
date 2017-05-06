package com.qg.route.route;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qg.route.R;
import com.qg.route.bean.User;
import com.qg.route.utils.URLHelper;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ricco on 2017/4/22.
 */

class UserHeadAdapter extends RecyclerView.Adapter<UserHeadAdapter.MyViewHolder> {
    private Context mContext;
    // 用于限制个数
    private final boolean isLimit;
    private ArrayList<User> mUserList;
    private LayoutInflater mLayoutInflater;
    private int mResource;
    private int limitNum;

    public UserHeadAdapter(Context context, int resource, ArrayList<User> userList, int limitNum) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext = context);
        mResource = resource;
        this.mUserList = userList;
        this.limitNum = limitNum;
        isLimit = true;
    }

    public UserHeadAdapter(Context context, int resource, ArrayList<User> userList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mResource = resource;
        this.mUserList = userList;
        isLimit = false;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(mLayoutInflater
                .inflate(mResource, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = mUserList.get(position);
        if (isLimit && (position == limitNum - 1)) { // 最后一项
            Glide.with(mContext).load(R.mipmap.points).into(holder.userHead);
            holder.username.setText("");
        } else {
            Glide.with(mContext).load(URLHelper.getPic(user.getUserid()))
                    .error(R.mipmap.head_default).into(holder.userHead);
            holder.username.setText(user.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (isLimit && mUserList.size() > limitNum) {
            return limitNum;
        }
        return mUserList.size();
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
