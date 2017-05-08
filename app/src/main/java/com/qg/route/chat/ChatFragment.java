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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qg.route.R;
import com.qg.route.bean.ChatLog;
import com.qg.route.login.LoginActivity;
import com.qg.route.moments.ChatGlideUtil;
import com.qg.route.moments.MomentsActivity;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.Constant;
import com.qg.route.utils.FriendDataBaseHelper;
import com.qg.route.utils.FriendDataBaseUtil;
import com.qg.route.utils.HttpUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatFragment extends Fragment {

    private final static int VIEW_TYPE_FRIEND = 0;
    private final static int VIEW_TYPE_ME = 1;
    private final static String MY_ID = Constant.USER_ID;
    private final static String MY_NAME = LoginActivity.sMyName;
    private String mFriendImageUrl = Constant.ChatUrl.CHAT_HEAD_IMAGE_GET;
    private Handler mHandler = new Handler();
    private List<ChatLog> mContents;
    private ChatAdapter mChatAdapter;
    private RecyclerView mChatContent;
    private EditText mSendContent;
    private Button mSendButton;
    private String mFriendName;
    private String mFriendId;
    private Boolean isCircle;
    private String mFragmentState;
    private ChatReceiver mChatReceiver = new ChatReceiver();
    private ExecutorService mExecutorService= Executors.newSingleThreadExecutor();;

    public static final String USER_NAME = "com.qg.route.chatfragment.USER_NAME";
    public static final String USER_ID = "com.qg.route.chatfragment.USER_ID";
    public static final String IS_CIRCLE = "com.qg.route.chatfragment.CIRCLE";
    private static final String ON_PAUSE = "onPause";
    private static final String ON_RESUME = "onResume";

    public static ChatFragment newInstance(String name , String id , Boolean isCircle){
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID , id);
        bundle.putString(USER_NAME , name);
        bundle.putBoolean(IS_CIRCLE , isCircle);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private class ChatReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ChatLog chatLog = toChatLog(intent.getStringExtra(ChatService.CHANGE_CONTENT));
            Log.e("ChatLog",chatLog.toString());
            if(mFragmentState != null || mFragmentState == "onResume"){
                mContents.add(chatLog);
                mChatAdapter.notifyDataSetChanged();
                mChatContent.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
            }
        }

        private ChatLog toChatLog(String text){
            Gson gson = new Gson();
            ChatLog chatLog = gson.fromJson(text , ChatLog.class);
            return chatLog;
        }
    }

    private Map<String,String> newMessage(ChatLog chatLog){
        Map<String , String> map = new HashMap<String, String>();
        map.put(ChatDataBaseHelper.USER_ID , chatLog.getId()+"");
        map.put(ChatDataBaseHelper.FROM , chatLog.getSendId() + "");
        map.put(ChatDataBaseHelper.TO , chatLog.getReceiveId() + "");
        map.put(ChatDataBaseHelper.CONTENT , chatLog.getContent());
        map.put(ChatDataBaseHelper.DATE , chatLog.getSendTime()+"");
        map.put(ChatDataBaseHelper.IS_NEW , "0");
        return map;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat , container , false);
        mChatContent = (RecyclerView) view.findViewById(R.id.chat_content_list);
        mSendContent = (EditText) view.findViewById(R.id.send_edit_text);
        mSendButton = (Button) view.findViewById(R.id.send_button);
        mContents = new ArrayList<>();
        mFriendId = getArguments().getString(USER_ID);
        mFriendName = getArguments().getString(USER_NAME);
        isCircle = getArguments().getBoolean(IS_CIRCLE);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatLog chatLog = new ChatLog();
                chatLog.setSendId(Integer.parseInt(MY_ID));
                chatLog.setReceiveId(Integer.parseInt(mFriendId));
                chatLog.setContent(mSendContent.getText().toString());
                if(!isCircle)chatLog.setFlag(0);
                else chatLog.setFlag(3);
                String json = new Gson().toJson(chatLog , ChatLog.class);
                Log.e("JSON",json);
                //如果不是圈子信息
                //存到数据库

                Map<String, String> map = newMessage(chatLog);
                ChatDataBaseUtil.insert(getActivity(), map);
                HttpUtil.sendMessage(json);


                Map<String, String> map1 = new HashMap<String, String>();
                map1.put(FriendDataBaseHelper.NAME , mFriendName);
                map1.put(FriendDataBaseHelper.USER_ID, mFriendId);
                map1.put(FriendDataBaseHelper.LAST_CONTENT, chatLog.getContent());
                map1.put(FriendDataBaseHelper.LAST_TIME, chatLog.getSendTime() + "");
                FriendDataBaseUtil.replace(getActivity(), map1);

                mContents.add(chatLog);
                mChatAdapter.notifyDataSetChanged();
                mSendContent.setText("");
                mChatContent.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
            }
        });


        mChatAdapter = new ChatAdapter();
        if(isAdded()) {
            mChatContent.setLayoutManager(new LinearLayoutManager(getActivity()));
            mChatContent.setAdapter(mChatAdapter);
        }
        mExecutorService.execute(getContentRunnable());
        return view;
    }


    private Runnable getContentRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                List<ChatLog> list = ChatDataBaseUtil.query(getActivity() , new String[]{
                    ChatDataBaseHelper.FROM , ChatDataBaseHelper.TO} ,new String[]{MY_ID , mFriendId},null );
                if(list != null && list.size()>0) {
                    list.addAll(ChatDataBaseUtil.query(getActivity(),
                            new String[]{ChatDataBaseHelper.TO , ChatDataBaseHelper.FROM}, new String[]{MY_ID , mFriendId}, null));

                    Collections.sort(list, new Comparator<ChatLog>() {
                        @Override
                        public int compare(ChatLog chatLog, ChatLog t1) {
                            int result = 0;
                            if (chatLog.getSendTime() > t1.getSendTime()) {
                                result = 1;
                            } else if (chatLog.getSendTime() == (t1.getSendTime())) {
                                result = 0;
                            } else {
                                result = -1;
                            }
                            return result;
                        }
                    });
                }
                mContents.clear();
                mContents.addAll(list);
                Log.e("mContents" , mContents.toString());
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

    private class MyChatHolder extends RecyclerView.ViewHolder{
        private ImageView mMyImage;
        private TextView mMyText;
        public MyChatHolder(View itemView) {
            super(itemView);
            mMyText = (TextView) itemView.findViewById(R.id.my_text);
            mMyImage = (ImageView) itemView.findViewById(R.id.chat_content_my_image);
            mMyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = MomentsActivity.newIntent(MY_ID , getActivity());
                    startActivity(intent);
                }
            });
        }
        public void bindChatContent(ChatLog chatLog){
            mMyText.setText(chatLog.getContent());
            loadImage(MY_ID ,mMyImage);
        }

    }

    private class FriendChatHolder extends RecyclerView.ViewHolder{
        private TextView mFriendText;
        private ImageView mFriendImage;
        public FriendChatHolder(View itemView) {
            super(itemView);
            mFriendText = (TextView) itemView.findViewById(R.id.friend_text);
            mFriendImage = (ImageView) itemView.findViewById(R.id.chat_content_friend_image);
            mFriendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = MomentsActivity.newIntent(mFriendId , getActivity());
                    startActivity(intent);
                }
            });
        }
        public void bindChatContent(ChatLog chatLog){
            mFriendText.setText(chatLog.getContent());
            loadImage(chatLog.getSendId()+"" , mFriendImage);
        }
    }

    private void loadImage(String id , ImageView imageView){
        ChatGlideUtil.loadImageByUrl(ChatFragment.this , mFriendImageUrl+id+".jpg" , imageView , R.drawable.normal_person_image);
    }

    private class ChatAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_FRIEND) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.item_chat_friend, parent , false);
                return new FriendChatHolder(view);
            } else if(viewType == VIEW_TYPE_ME) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.item_chat_me, parent , false);
                return new MyChatHolder(view);
            } else return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChatLog chatLog = mContents.get(position);
            if(holder instanceof MyChatHolder){
                ((MyChatHolder) holder).bindChatContent(chatLog);
            }else if(holder instanceof FriendChatHolder){
                ((FriendChatHolder) holder).bindChatContent(chatLog);
            }
        }

        @Override
        public int getItemCount() {
            return mContents.size();
        }

        @Override
        public int getItemViewType(int position) {
            ChatLog log = null;
            if(mContents != null) {
                log = mContents.get(position);
            }
            if((log.getSendId()+"").equals(MY_ID))
                return VIEW_TYPE_ME;
            else return VIEW_TYPE_FRIEND;
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
//        if(mChatContent != null && mChatContent.getAdapter() != null){
//            mExecutorService.execute(getContentRunnable());
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mChatReceiver);
    }
}
