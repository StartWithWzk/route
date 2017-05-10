package com.qg.route.information;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.Information;
import com.qg.route.bean.RequestResult;
import com.qg.route.utils.Constant;
import com.qg.route.utils.FriendDataBaseHelper;
import com.qg.route.utils.FriendDataBaseUtil;
import com.qg.route.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by Mr_Do on 2017/5/8.
 */

public class InformationFragment extends Fragment {

    private RelativeLayout mLayout;
    private static final String HANDLE_INFORMATION = Constant.InformationUrl.HANDLE_INFORMATION;
    private static final String INFORMATION_GET = Constant.InformationUrl.INFORMATION_GET;
    private List<Information> mInformationList;
    private RecyclerView mInformationRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private InformationAdapter mAdapter;
    private Handler mHandler;
    private int mPage = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information , container , false);
        mLayout = (RelativeLayout) view.findViewById(R.id.empty_information_layout);
        mInformationRecyclerView = (RecyclerView) view.findViewById(R.id.information_list);
        mInformationList = new ArrayList<>();
        mAdapter = new InformationAdapter();
        mInformationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mInformationRecyclerView.setAdapter(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0 , ItemTouchHelper.START);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                handleInformation(mInformationList.get(viewHolder.getAdapterPosition()) , 4);
            }
        });
        mItemTouchHelper.attachToRecyclerView(mInformationRecyclerView);
        mHandler = new Handler();
        getInformationList();
        return view;
    }

    // TODO: 2017/5/8 获取更多消息功能
    public Boolean isSlideToBottom(){
        if(mInformationRecyclerView.computeVerticalScrollExtent() +
                mInformationRecyclerView.computeVerticalScrollOffset() >=
                mInformationRecyclerView.computeVerticalScrollRange())
            return true;
        else return false;
    }

    public void getInformationList() {
        HttpUtil.DoGet(INFORMATION_GET + mPage,
                new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                if(response != null){
                    RequestResult<List<Information>> requestResult = null;
                    Gson gson = new Gson();
                    try {
                        requestResult = gson.fromJson(response.body().charStream() , RequestResult.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(requestResult != null && requestResult.getState() == 151){
                        String json = gson.toJson(requestResult.getData() , new TypeToken<List<Information>>(){}.getType());
                        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
                        for(JsonElement e : jsonArray){
                            mInformationList.add(gson.fromJson(e , Information.class));
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO: 2017/5/10
                                for(Information information : mInformationList){
                                    if((information.getSendId()+"").equals(Constant.USER_ID)){
                                        mInformationList.remove(information);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                                if(mInformationList.size() == 0){
                                    mLayout.setVisibility(View.VISIBLE);
                                }else mLayout.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        } ,false);
    }

    private class InformationHolder extends RecyclerView.ViewHolder{
        private Button mAcceptButton;
        private Button mRejectButton;
        private TextView mInformationText;
        public InformationHolder(View itemView) {
            super(itemView);
            mAcceptButton = (Button) itemView.findViewById(R.id.accept_button);
            mRejectButton = (Button) itemView.findViewById(R.id.reject_button);
            mInformationText = (TextView) itemView.findViewById(R.id.information_text);
        }
        public void bindView(final Information information){
            mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleInformation(information , 1);
                    mInformationList.remove(information);
                    mAdapter.notifyItemRemoved(mInformationList.indexOf(information));
                }

            });
            mRejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleInformation(information , 3);
                    mInformationList.remove(information);
                    mAdapter.notifyItemRemoved(mInformationList.indexOf(information));
                }
            });
            mInformationText.setText(information.getContent());
        }
    }

    private class InformationAdapter extends RecyclerView.Adapter<InformationHolder>{

        @Override
        public InformationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =  LayoutInflater.from(getActivity()).inflate(R.layout.item_information , parent ,false);
            return new InformationHolder(view);
        }

        @Override
        public void onBindViewHolder(InformationHolder holder, int position) {
            holder.bindView(mInformationList.get(position));
        }

        @Override
        public int getItemCount() {
            return mInformationList.size();
        }
    }

    private void handleInformation(final Information information , final int flag) {
        HttpUtil.DoGet(HANDLE_INFORMATION + information.getId() + "/" + flag, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                RequestResult requestResult = null;
                Gson gson = new Gson();
                if(response != null) {
                    requestResult = gson.fromJson(response.body().charStream() , RequestResult.class);
                    if(requestResult != null && requestResult.getState() == 154) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemRemoved(mInformationList.indexOf(information));
                                mInformationList.remove(information);
                                mAdapter.notifyDataSetChanged();
                                if(flag == 1){
                                    Map<String , String> map = new HashMap<String, String>();
                                    map.put(FriendDataBaseHelper.USER_ID , information.getSendId()+"");
                                    FriendDataBaseUtil.insert(getActivity() , map);
                                }
                            }
                        });

                    }
                }

            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        } , false);
    }
}
