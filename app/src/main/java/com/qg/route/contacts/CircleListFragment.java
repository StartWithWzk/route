package com.qg.route.contacts;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.ChatRoom;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.chat.ChatActivity;
import com.qg.route.moments.ChatGlideUtil;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Response;

/**
 * Created by Mr_Do on 2017/4/30.
 */

public class CircleListFragment extends Fragment{

    private static final String GET_ROOM_LIST_URL = Constant.ChatUrl.ROOM_LIST_GET;
    private static final String HEAD_IMAGE_URL = Constant.ChatUrl.CHAT_HEAD_IMAGE_GET;
    private RecyclerView mCircleList;
    private List<ChatRoom> mChatRoomList;
    private Handler mHandler;
    private ChatRoomAdapter mAdapter;
    private RelativeLayout mLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_circle_list , container ,false);
        mLayout = (RelativeLayout) view.findViewById(R.id.empty_circle_layout);
        mCircleList = (RecyclerView) view.findViewById(R.id.circle_list);
        mChatRoomList = new ArrayList<>();
        mHandler = new Handler();
        mAdapter = new ChatRoomAdapter();
        mCircleList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCircleList.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getChatRoomList();
    }

    private class ChatRoomHolder extends RecyclerView.ViewHolder{
        private ImageView mHeadImage;
        private TextView mNameText;
        private TextView mNewContent;
        private TextView mNumber;
        private View mView;
        public ChatRoomHolder(View itemView) {
            super(itemView);
            mNameText = (TextView) itemView.findViewById(R.id.chat_title);
            mNumber = (TextView) itemView.findViewById(R.id.chat_last_content);
            mHeadImage = (ImageView) itemView.findViewById(R.id.chat_image);
            mNewContent = (TextView) itemView.findViewById(R.id.news_count);
            mView = itemView;
        }

        public void bindView(final ChatRoom chatRoom){
            mNewContent.setVisibility(View.INVISIBLE);
            mNameText.setText(chatRoom.getRoomName());
            mNumber.setText("人数 : " + chatRoom.getRoomCount()+"");
            ChatGlideUtil.loadImageByUrl(CircleListFragment.this , HEAD_IMAGE_URL ,
                    mHeadImage , R.drawable.normal_person_image );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = ChatActivity.newIntent(getActivity() , chatRoom.getRoomName() , chatRoom.getId()+"" , true);
                    HashMap<String , String> map = new HashMap<>();
                    map.put(ChatDataBaseHelper.IS_NEW , "0");
                    ChatDataBaseUtil.updata(getActivity() , map , new String[]{ChatDataBaseHelper.FROM} , new String[]{chatRoom.getId()+""});
                    startActivity(intent);
                }
            });
        }
    }

    private void getChatRoomList(){
        HttpUtil.DoGet(GET_ROOM_LIST_URL,
                new HttpUtil.HttpConnectCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        if(response != null){
                            RequestResult<List<ChatRoom>> requestResult = null;
                            Gson gson = new Gson();
                            try {
                                requestResult = gson.fromJson(response.body().charStream() , RequestResult.class);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if(requestResult != null && requestResult.getState() == 301){
                                String json = gson.toJson(requestResult.getData(), new TypeToken<List<ChatRoom>>() {
                                }.getType());
                                JsonArray jsonElements = new JsonParser().parse(json).getAsJsonArray();
                                mChatRoomList.clear();
                                for (JsonElement element : jsonElements) {
                                    mChatRoomList.add(gson.fromJson(element, ChatRoom.class));
                                }
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyDataSetChanged();
                                        if(mChatRoomList.size() == 0){
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
                } , false);
    }

    private class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomHolder>{

        @Override
        public ChatRoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_chat_list , parent , false);
            return new ChatRoomHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatRoomHolder holder, int position) {
            holder.bindView(mChatRoomList.get(position));
        }

        @Override
        public int getItemCount() {
            return mChatRoomList.size();
        }
    }
}
