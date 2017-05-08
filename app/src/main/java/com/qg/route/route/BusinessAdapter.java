package com.qg.route.route;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qg.route.R;
import com.qg.route.bean.Business;
import com.qg.route.utils.Constant;
import com.qg.route.utils.URLHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricco on 2017/5/3.
 */

public class BusinessAdapter extends  RecyclerView.Adapter<BusinessAdapter.ViewHolder>{

    private List<Business> mBusiness;
    private Context context;

    public BusinessAdapter(List<Business> mBusiness, Context context) {
        this.mBusiness = mBusiness;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_business, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Business business = mBusiness.get(position);
        Glide.with(context)
                .load(URLHelper.getBusinessPic(business.getId()))
                .error(R.mipmap.temp_picture)
                .into(holder.businessPic);
        holder.businessName.setText(business.getBusiName());
        holder.businessRank.setText(String.valueOf(business.getBusiGrade()));
        holder.businessConsume.setText("人均消费" + String.valueOf(business.getConsumption()) + "元");
    }

    @Override
    public int getItemCount() {
        if (mBusiness != null) {
            return mBusiness.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView businessPic;
        private TextView businessName;
        private TextView businessRank;
        private TextView businessConsume;

        public ViewHolder(View itemView) {
            super(itemView);
            businessPic = (ImageView) itemView.findViewById(R.id.iv_business);
            businessName = (TextView) itemView.findViewById(R.id.tv_business_name);
            businessRank = (TextView) itemView.findViewById(R.id.tv_rank);
            businessConsume = (TextView) itemView.findViewById(R.id.tv_consume);
        }
    }
}
