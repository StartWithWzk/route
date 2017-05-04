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
import com.qg.route.R;
import com.qg.route.bean.ChatLog;
import com.qg.route.login.LoginActivity;
import com.qg.route.moments.ChatGlideUtil;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.Constant;
import com.qg.route.utils.FriendDataBaseHelper;
import com.qg.route.utils.FriendDataBaseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatListFragment extends Fragment {

    private final static String MY_ID = Constant.USER_ID;
    private Handler mHandler = new Handler();
    private List<ChatBean> mFriends;
    private RecyclerView mChatList = null;
    private String mFriendImageUrl = Constant.ChatUrl.CHAT_HEAD_IMAGE_GET;
    private ChatAdapter mChatAdapter = null;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Runnable getNewsCountRunnable(final ChatHolder chatHolder, final ChatBean user){
        return new Runnable() {
            @Override
            public void run() {
                int count = 0;
                List<ChatLog> list = ChatDataBaseUtil.query(getActivity() , new String[]{ChatDataBaseHelper.FROM ,
                        ChatDataBaseHelper.TO , ChatDataBaseHelper.IS_NEW} , new String[]{user.getUser_id() , MY_ID, "1"} , null);
                Map<String , String> map = new HashMap<>();
                map.put(ChatDataBaseHelper.IS_NEW , "0");
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

        mChatList = (RecyclerView) view.findViewById(R.id.chat_list);
        mChatList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriends = new ArrayList<ChatBean>();
        mChatAdapter = new ChatAdapter();
        mChatList.setAdapter(mChatAdapter);
        executorService.execute(getFriendListRunnable());
        return view;
    }




    private Runnable getFriendListRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                mFriends = FriendDataBaseUtil.query(getActivity() , null ,null ,null);
                Collections.sort(mFriends, new Comparator<ChatBean>() {
                    @Override
                    public int compare(ChatBean chatBean, ChatBean t1) {
                        int result = 0;
                        if(Long.parseLong(chatBean.getLast_time()) > Long.parseLong(t1.getLast_time())){
                            result =  -1;
                        }else if(Long.parseLong(chatBean.getLast_time()) == Long.parseLong(t1.getLast_time())){
                            result =  0;
                        }else{
                            result =  1;
                        }
                        return  result;
                    }
                });
                if(mFriends!=null && mFriends.size()>0) {
                    for (int i = 0 ; i < mFriends.size() ; i++) {
                        if (mFriends.get(i).getUser_id().equals("0"))
                            mFriends.remove(mFriends.get(i));
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mChatAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }


    private class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mChatTitle = null;
        private TextView mLastContent = null;
        private TextView mNewsCount = null;
        private ImageView mChatImage = null;
        private ChatBean mFriend = null;

        public ChatHolder(View itemView) {
            super(itemView);
            mChatTitle = (TextView) itemView.findViewById(R.id.chat_title);
            mLastContent = (TextView) itemView.findViewById(R.id.chat_last_content);
            mChatImage = (ImageView) itemView.findViewById(R.id.chat_image);
            mNewsCount = (TextView) itemView.findViewById(R.id.news_count);
            itemView.setOnClickListener(this);
        }
        public void bindChatList(ChatBean friend){
            mFriend = friend;
            mLastContent.setText(friend.getLast_content());
            mChatTitle.setText(friend.getName());
            ChatGlideUtil.loadImageByUrl(ChatListFragment.this , mFriendImageUrl+friend.getUser_id()+".jpg" , mChatImage , R.drawable.normal_person_image);
        }
        public void bindNewsCount(int count){
            if(count > 0)
                mNewsCount.setText(count + "");
            else mNewsCount.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity() , ChatActivity.class);
            intent.putExtra(ChatFragment.USER_NAME , mFriend.getName());
            intent.putExtra(ChatFragment.USER_ID , mFriend.getUser_id());
            intent.putExtra(ChatFragment.IS_CIRCLE , mFriend.getIs_circle());
            HashMap<String , String> map = new HashMap<>();
            map.put(ChatDataBaseHelper.IS_NEW , "0");
            ChatDataBaseUtil.updata(getActivity() , map , new String[]{ChatDataBaseHelper.FROM} , new String[]{mFriend.getUser_id()});
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
            ChatBean user = mFriends.get(position);
            holder.bindChatList(user);
            executorService.execute(getNewsCountRunnable(holder , user));
        }


        @Override
        public int getItemCount() {
            if(mFriends == null) return 0;
            else return mFriends.size();
        }
    }
}
