package com.qg.route.chat;

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
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.ChatLog;
import com.qg.route.bean.User;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatListFragment extends Fragment {

    private final static String ID = "";
    private Handler mHandler = new Handler();
    private List<User> mFriends;
    private ResponseBody mBody;
    private RecyclerView mChatList = null;
    private String mChatListUrl = "";
    private String mFriendNameUrl = "";
    private String mFriendImageUrl = "";
    private ChatAdapter mChatAdapter = null;

    private Runnable getNewsCountRunnable(final ChatHolder chatHolder){
        return new Runnable() {
            @Override
            public void run() {
                int count = 0;
                List<ChatLog> list = ChatDataBaseUtil.query(getActivity() , new String[]{ChatDataBaseHelper.FROM , ChatDataBaseHelper.IS_NEW} , new String[]{ID , "1"} , null);
                count += list.size();
                list = ChatDataBaseUtil.query(getActivity() , new String[]{ChatDataBaseHelper.TO , ChatDataBaseHelper.IS_NEW} , new String[]{ID , "1"} , null);
                count += list.size();
                final int finalCount = count;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        chatHolder.bindNewsCount(finalCount);
                    }
                });
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat_list , container ,false);

        Intent intent = new Intent(getActivity() , ChatService.class);
        getActivity().startService(intent);

        mChatList = (RecyclerView) view.findViewById(R.id.chat_list);
        mChatList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatAdapter = new ChatAdapter();
        getFriendList();
        return view;
    }

    private void getFriendList(){

        mFriends = new ArrayList<User>();

        HttpUtil.DoGet(mChatListUrl , new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                Gson gson = new Gson();
                mBody = response.body();
                if(mBody != null){
                    mFriends= gson.fromJson(mBody.charStream(), new TypeToken<List<User>>() {
                    }.getType());
                    for(int i = 0 ; i < mFriends.size() ; i++){
                        final int finalI = i;
                        HttpUtil.DoGet(mFriendNameUrl + mFriends.get(i).getUserid(), new HttpUtil.HttpConnectCallback() {
                            @Override
                            public void onSuccess(Response response) {
                                Gson gson = new Gson();
                                User user = gson.fromJson(mBody.charStream() , User.class);
                                mFriends.get(finalI).setName(user.getName());
                                if(finalI == mFriends.size() - 1 && isAdded()){
                                    if(mChatList.getAdapter() != null)
                                        mChatList.getAdapter().notifyDataSetChanged();
                                    else mChatList.setAdapter(mChatAdapter);
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


    private class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mChatTitle = null;
        private TextView mChatIntroduce = null;
        private TextView mNewsCount = null;
        private ImageView mChatImage = null;
        private User mFriend = null;

        public ChatHolder(View itemView) {
            super(itemView);
            mChatTitle = (TextView) itemView.findViewById(R.id.chat_title);
            mChatIntroduce = (TextView) itemView.findViewById(R.id.chat_introduce);
            mChatImage = (ImageView) itemView.findViewById(R.id.chat_image);
            mNewsCount = (TextView) itemView.findViewById(R.id.news_count);
            itemView.setOnClickListener(this);
        }
        public void bindChatList(User friend){
            mFriend = friend;
            mChatIntroduce.setText(friend.getIntroduction());
            mChatTitle.setText(friend.getName());
            Glide.with(ChatListFragment.this).load(mFriendImageUrl+friend.getUserid())
            .into(mChatImage);
        }
        public void bindNewsCount(int count){
            mNewsCount.setText(count + "");
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity() , ChatActivity.class);
            intent.putExtra(ChatFragment.USER_NAME , mFriend.getName())
                    .putExtra(ChatFragment.USER_ID , mFriend.getUserid());
            startActivity(intent);
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatHolder>{

        @Override
        public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_chat_list , parent ,false);
            return new ChatHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatHolder holder, int position) {
            User user = mFriends.get(position);
            holder.bindChatList(user);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(getNewsCountRunnable(holder));
        }


        @Override
        public int getItemCount() {
            return mFriends.size();
        }
    }
}
