package com.qg.route.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qg.route.R;
import com.qg.route.bean.ChatLog;
import com.qg.route.moments.ChatGlideUtil;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.Constant;
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

    private ChatListReceiver mChatListReceiver = new ChatListReceiver();
    private List<Integer> mCountList;
    private Boolean isResume = false;
    private String mFragmentState;
    private final static String MY_ID = Constant.USER_ID;
    private Handler mHandler = new Handler();
    private List<ChatBean> mFriends;
    private RecyclerView mChatList = null;
    private String mFriendImageUrl = Constant.ChatUrl.CHAT_HEAD_IMAGE_GET;
    private ChatAdapter mChatAdapter = null;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private RelativeLayout mLayout;
    private static final String ON_PAUSE = "onPause";
    private static final String ON_RESUME = "onResume";

    private Runnable getNewsCountRunnable(final ChatBean user){
        return new Runnable() {
            @Override
            public void run() {
                int count = 0;
                List<ChatLog> list  = new ArrayList<>();
                if(user.getIs_circle().equals("0")) {
                    list = ChatDataBaseUtil.query(getActivity(), new String[]{ChatDataBaseHelper.FROM,
                            ChatDataBaseHelper.TO, ChatDataBaseHelper.IS_NEW}, new String[]{user.getUser_id(), MY_ID, "1"}, null);
                }else{
                    list = ChatDataBaseUtil.query(getActivity(), new String[]{
                            // TODO: 2017/5/10
                            ChatDataBaseHelper.TO, ChatDataBaseHelper.IS_NEW}, new String[]{user.getUser_id(),"1"}, null);
                }
                count += list.size();
                mCountList.add(count);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCountList.size() > 0)
                            mChatAdapter.notifyItemChanged(mCountList.size()-1);
                        Log.e("UPDATe",mCountList.size()+"  "+mCountList.get(mCountList.size()-1));
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
        mLayout = (RelativeLayout) view.findViewById(R.id.empty_chat_layout);
        mChatList = (RecyclerView) view.findViewById(R.id.chat_list);
        mChatList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriends = new ArrayList<ChatBean>();
        mCountList = new ArrayList<>();
        mChatAdapter = new ChatAdapter();
        mChatList.setAdapter(mChatAdapter);
        executorService.execute(getFriendListRunnable());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentState = ON_RESUME;
        if(isResume) {
            executorService.execute(getFriendListRunnable());
        }
        isResume = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFragmentState = ON_PAUSE;
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
                mCountList.clear();
                if(mFriends!=null && mFriends.size()>0) {
                    for (int i = 0 ; i < mFriends.size() ; i++) {
                        if (mFriends.get(i).getUser_id().equals("0")) {
                            mFriends.remove(mFriends.get(i));
                        }else if(mFriends.get(i).getLast_content() == null){
                            mFriends.remove(mFriends.get(i));
                        } else executorService.execute(getNewsCountRunnable(mFriends.get(i)));
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mFriends.size() == 0){
                            mLayout.setVisibility(View.VISIBLE);
                        }else mLayout.setVisibility(View.GONE);
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
            if(count > 0) {
                mNewsCount.setVisibility(View.VISIBLE);
                mNewsCount.setText(count + "");
            }else mNewsCount.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            if(mFriend.getIs_circle().equals("1")){
                intent = ChatActivity.newIntent(getActivity() , mFriend.getName() , mFriend.getUser_id() , true);
            }else{
                intent = ChatActivity.newIntent(getActivity() , mFriend.getName() , mFriend.getUser_id() , false);
            }
            HashMap<String , String> map = new HashMap<>();
            map.put(ChatDataBaseHelper.IS_NEW , "0");
            ChatDataBaseUtil.updata(getActivity() , map , new String[]{ChatDataBaseHelper.FROM , ChatDataBaseHelper.IS_NEW} , new String[]{mFriend.getUser_id() , "1"});
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
            Integer i = mCountList.get(position);
            Log.e("I",i+" "+position);
            holder.bindChatList(user);
            holder.bindNewsCount(i);
        }


        @Override
        public int getItemCount() {
            if(mFriends == null) return 0;
            else return mFriends.size();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mChatListReceiver , new IntentFilter(ChatService.CHANGE_LIST));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mChatListReceiver);
    }

    private class ChatListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mFragmentState != null) {
                executorService.execute(getFriendListRunnable());
            }
        }
    }
}
