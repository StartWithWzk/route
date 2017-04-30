package com.qg.route.chat;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.ChatLog;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatListFragment extends Fragment {



    private final static String MY_ID = "11111";
    private final static String LAND_URL = "http://118.89.54.17:8080/onway/user/login";
    private Handler mHandler = new Handler();
    private List<User> mFriends;
    private ResponseBody mBody;
    private RecyclerView mChatList = null;
    private String mChatListUrl = "http://118.89.54.17:8080/onway/relation/list";
    private String mFriendImageUrl = "http://118.89.54.17:8080/onway/picture/11111.jpg";
    private ChatAdapter mChatAdapter = null;

    private Runnable getNewsCountRunnable(final ChatHolder chatHolder){
        return new Runnable() {
            @Override
            public void run() {
                int count = 0;
                List<ChatLog> list = ChatDataBaseUtil.query(getActivity() , new String[]{ChatDataBaseHelper.FROM , ChatDataBaseHelper.IS_NEW} , new String[]{MY_ID, "1"} , null);
                count += list.size();
                list = ChatDataBaseUtil.query(getActivity() , new String[]{ChatDataBaseHelper.TO , ChatDataBaseHelper.IS_NEW} , new String[]{MY_ID, "1"} , null);
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
        mChatList.setAdapter(mChatAdapter);

        // TODO: 2017/4/27
        land();


        return view;
    }

    private void land() {
        Map<String , String> map = new HashMap<>();
        map.put("userid",MY_ID);
        map.put("password","123456");
        HttpUtil.PostMap(LAND_URL, map, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                Gson gson = new Gson();
                testJson();
                RequestResult requestResult = gson.fromJson(response.body().charStream() , RequestResult.class);
                Log.e("INFO",requestResult.getStateInfo());

                Headers headers = response.headers();
                Log.d("info_headers", "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                if(cookies != null && cookies.size()>0) {
                    String session = cookies.get(0);
                    HttpUtil.setSession(session);
                }
                getFriendList();
            }


            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        } , false);
    }

    private void testJson(){
        List<User> users = new ArrayList<User>();
        User user = new User();
        user.setIntroduction("ACB");
        user.setName("AAD");
        users.add(user);
        users.add(user);
        users.add(user);

        RequestResult<List<User>> requestResult = new RequestResult<List<User>>(1,"2",users);
        String json = new Gson().toJson(requestResult , new TypeToken<RequestResult<List<User>>>(){}.getType());
        Log.e("JSON",json);
        requestResult = new Gson().fromJson(json , new TypeToken<RequestResult<List<User>>>(){}.getType());
        Log.e("USER",requestResult.getData().get(0).getIntroduction());
    }

    private void getFriendList(){

        mFriends = new ArrayList<User>();
        HttpUtil.DoGet(mChatListUrl , new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                Gson gson = new Gson();
                mBody = response.body();
                if(mBody != null){
                    RequestResult requestResult = gson.fromJson(mBody.charStream(), RequestResult.class);

                    // TODO: 2017/4/27



//                    String user = requestResult.getData();
//                    mFriends = gson.fromJson(user , new TypeToken<List<User>>(){}.getType());
//
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mChatAdapter.notifyDataSetChanged();
//                        }
//                    });
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
            Glide.with(ChatListFragment.this).load(mFriendImageUrl)
            .into(mChatImage);
        }
        public void bindNewsCount(int count){
            if(count > 0)
                mNewsCount.setText(count + "");
            else mNewsCount.setVisibility(View.INVISIBLE);
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
            if(mFriends == null) return 0;
            else return mFriends.size();
        }
    }
}
