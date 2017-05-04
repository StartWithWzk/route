package com.qg.route.recommend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qg.route.R;
import com.qg.route.bean.User;
import com.qg.route.custom.CircleImageVIew;
import com.qg.route.utils.URLHelper;

import java.util.ArrayList;

/**
 * Created by Ricco on 2017/5/6.
 */

class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<User> mUserList;
    private OnItemSelectedListener mListener;

    public interface OnItemSelectedListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    public RecommendAdapter(Context context, ArrayList<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recommend, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        User user = mUserList.get(position);
        Glide.with(mContext).load(URLHelper.getPic(user.getUserid())).error(R.mipmap.head_default).into(holder.mHeadView);
        if (user.getSex() == 0) { // 男
            Glide.with(mContext).load(R.mipmap.boy).into(holder.mSex);
        } else { // 女
            Glide.with(mContext).load(R.mipmap.girl).into(holder.mSex);
        }
        holder.mUserName.setText(user.getName());
        holder.mSimilarity.setText(user.getSuitability());
        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mUserList == null) {
            return 0;
        }
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        private CircleImageVIew mHeadView;
        private ImageView mHeadView;
        private TextView mUserName;
        private ImageView mSex;
        private TextView mSimilarity;
        private RelativeLayout mRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeadView = (ImageView) itemView.findViewById(R.id.civ_head);
            mUserName  = (TextView) itemView.findViewById(R.id.tv_recommend_name);
            mSex = (ImageView) itemView.findViewById(R.id.iv_recommend_sex);
            mSimilarity = (TextView) itemView.findViewById(R.id.tv_similarity);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_click_help);
        }
    }
}
