package com.qg.route.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.chat.ChatActivity;
import com.qg.route.chat.ChatFragment;
import com.qg.route.moments.ChatGlideUtil;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Mr_Do on 2017/4/30.
 */

public class FriendListFragment extends Fragment {

    private Handler mHandler = new Handler();
    private List<User> mFriends;
    private ResponseBody mBody;
    private String mFriendsUrl = Constant.ChatUrl.FRIEND_LIST_GET;
    private String mFriendImageUrl = Constant.ChatUrl.CHAT_HEAD_IMAGE_GET;
    private RecyclerView mRecyclerView;
    private FriendAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_friend_list , container ,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.friend_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriends = new ArrayList<>();
        mAdapter = new FriendAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getFriendList();
        return view;
    }

    private void getFriendList(){

        mFriends = new ArrayList<User>();
        HttpUtil.DoGet(mFriendsUrl, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                mFriends.clear();
                Gson gson = new Gson();
                mBody = response.body();
                if(mBody != null){
                    RequestResult<List<User>> requestResult = null;
                    try {
                        requestResult = gson.fromJson(mBody.charStream(), RequestResult.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(requestResult != null) {
                        String json = gson.toJson(requestResult.getData(), new TypeToken<List<User>>() {
                        }.getType());
                        JsonArray jsonElements = new JsonParser().parse(json).getAsJsonArray();
                        for (JsonElement element : jsonElements) {
                            mFriends.add(gson.fromJson(element, User.class));
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onFailure() {

            }
        } ,false);

    }

    private class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private User mFriend;
        private TextView mFriendName;
        private TextView mFriendIntroduction;
        private ImageView mFriendImage;
        public FriendHolder(View itemView) {
            super(itemView);
            mFriendImage = (ImageView) itemView.findViewById(R.id.friend_image);
            mFriendIntroduction = (TextView) itemView.findViewById(R.id.friend_introduce);
            mFriendName = (TextView) itemView.findViewById(R.id.friend_title);
            itemView.setOnClickListener(this);
        }
        public void bindView(User friend){
            mFriend = friend;
            mFriendName.setText(friend.getName());
            mFriendIntroduction.setText(friend.getIntroduction());
            ChatGlideUtil.loadImageByUrl(FriendListFragment.this , mFriendImageUrl+friend.getUserid()+".jpg" , mFriendImage);
        }

        @Override
        public void onClick(View view) {
            Intent intent = ChatActivity.newIntent(getActivity() ,mFriend.getName() , mFriend.getUserid()+"");
            startActivity(intent);
        }
    }
    private class FriendAdapter extends RecyclerView.Adapter<FriendHolder>{
        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_friend , parent , false);
            return new FriendHolder(view);
        }

        @Override
        public void onBindViewHolder(FriendHolder holder, int position) {
            User user = mFriends.get(position);
            holder.bindView(user);
        }

        @Override
        public int getItemCount() {
            return mFriends.size();
        }
    }
}
