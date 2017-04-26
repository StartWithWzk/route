package com.qg.route.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qg.route.R;
import com.qg.route.bean.ChatLog;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.HttpUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatFragment extends Fragment {

    private final static String MY_ID = "";
    private Handler mHandler = new Handler();
    private List<ChatLog> mContents;
    private ChatAdapter mChatAdapter;
    private RecyclerView mChatContent;
    private EditText mSendContent;
    private Button mSendButton;
    private String mName;
    private String mId;
    private String mFragmentState;
    private ChatReceiver mChatReceiver = new ChatReceiver();
    private ExecutorService mExecutorService= Executors.newSingleThreadExecutor();;

    public static final String USER_NAME = "com.qg.route.chatfragment.USER_NAME";
    public static final String USER_ID = "com.qg.route.chatfragment.USER_ID";
    private static final String ON_PAUSE = "onPause";
    private static final String ON_RESUME = "onResume";

    public static ChatFragment newInstance(String name , String id){
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID , id);
        bundle.putString(USER_NAME , name);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private class ChatReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ChatLog chatLog = toChatLog(intent.getStringExtra(ChatService.CHAT_LOG));
            if(mFragmentState != null || mFragmentState == "onResume"){
                mContents.add(chatLog);
                mChatAdapter.notifyDataSetChanged();
            }
        }

        private ChatLog toChatLog(String text){
            Gson gson = new Gson();
            ChatLog chatLog = gson.fromJson(text , ChatLog.class);
            return chatLog;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat , container , false);
        mChatContent = (RecyclerView) view.findViewById(R.id.chat_content_list);
        mSendContent = (EditText) view.findViewById(R.id.send_edit_text);
        mSendButton = (Button) view.findViewById(R.id.send_button);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpUtil.sendMessage(mSendContent.getText().toString());
                mSendContent.setText("");
            }
        });

        mId = getArguments().getString(USER_ID);
        mName = getArguments().getString(USER_NAME);
        mContents = new ArrayList<>();
        mChatAdapter = new ChatAdapter();
        if(isAdded())
            mChatContent.setAdapter(mChatAdapter);
        mExecutorService.execute(getContentRunnable());
        return view;
    }

    private Runnable getContentRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                List<ChatLog> list = ChatDataBaseUtil.query(getActivity() , new String[]{
                    ChatDataBaseHelper.FROM} ,new String[]{MY_ID},null );
                list.addAll(ChatDataBaseUtil.query(getActivity() , new String[]{ChatDataBaseHelper.TO },new String[]{MY_ID},null));
                Collections.sort(list , new Comparator<ChatLog>() {
                    @Override
                    public int compare(ChatLog chatLog, ChatLog t1) {
                        if(Long.parseLong(chatLog.getSendTime()) - Long.parseLong(t1.getSendTime()) > 0){
                            return 1;
                        }else if(Long.parseLong(chatLog.getSendTime()) == Long.parseLong(t1.getSendTime())){
                            return 0;
                        }else{
                            return -1;
                        }
                    }
                });
                if(mChatContent != null && mChatContent.getAdapter() != null)
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mChatContent != null && mChatContent.getAdapter() != null)
                                mChatContent.getAdapter().notifyDataSetChanged();
                        }
                    });
            }
        };

    }

    private class ChatHolder extends RecyclerView.ViewHolder{

        private TextView mMyText;
        private TextView mFriendText;
        public ChatHolder(View itemView) {
            super(itemView);
            mMyText = (TextView) itemView.findViewById(R.id.my_text);
            mFriendText = (TextView) itemView.findViewById(R.id.friend_text);
        }
        public void bindChatContent(ChatLog chatLog){
            if((chatLog.getSendId()+"").equals(MY_ID)) {
                mMyText.setText(chatLog.getContent());
            }else mFriendText.setText(chatLog.getContent());
        }

    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatHolder>{

        @Override
        public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_chat , parent , false);
            return new ChatHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatHolder holder, int position) {
            ChatLog chatLog = mContents.get(position);
            holder.bindChatContent(chatLog);
        }

        @Override
        public int getItemCount() {
            return mContents.size();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ChatService.CHANGE_CONTENT);
        getActivity().registerReceiver(mChatReceiver , intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFragmentState = ON_PAUSE;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentState = ON_RESUME;
        if(mChatContent != null && mChatContent.getAdapter() != null){
            mExecutorService.execute(getContentRunnable());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mChatReceiver);
    }
}
