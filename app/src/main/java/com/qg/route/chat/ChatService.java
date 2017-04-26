package com.qg.route.chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.qg.route.bean.ChatLog;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import okio.ByteString;

/**
 * Created by Mr_Do on 2017/4/23.
 */

public class ChatService extends Service {

    private final static String CHAT_URL = "http://www.baidu.com";
    public final static String CHANGE_CONTENT = "com.qg.route.chatservice.CHANGE_CONTENT";
    public final static String CHAT_LOG = "com.qg.route.chatservice.CHAT_TEXT";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(isNetWorkUsable()) {
            HttpUtil.connectWebSocket(CHAT_URL, new HttpUtil.SocketConnectCallback() {
                @Override
                public void onMessage(String text) {
                    ChatLog chatLog = toChatLog(text);
                    Map<String,String> map = newMessage(chatLog);
                    ChatDataBaseUtil.insert(ChatService.this , map);
                    Intent intent = new Intent(CHANGE_CONTENT);
                    intent.putExtra(CHANGE_CONTENT , text);
                    sendBroadcast(intent);
                }

                private ChatLog toChatLog(String text) {
                    Gson gson = new Gson();
                    ChatLog chatLog = gson.fromJson(text, ChatLog.class);
                    return chatLog;
                }

                @Override
                public void onMessage(ByteString bytes) {

                }
            });
        }

        return START_REDELIVER_INTENT;
    }

    private Map<String,String> newMessage(ChatLog chatLog){
        Map<String , String> map = new HashMap<String, String>();
        map.put(ChatDataBaseHelper.USER_ID , chatLog.getId()+"");
        map.put(ChatDataBaseHelper.FROM , chatLog.getSendId() + "");
        map.put(ChatDataBaseHelper.TO , chatLog.getReceiveId() + "");
        map.put(ChatDataBaseHelper.CONTENT , chatLog.getContent());
        map.put(ChatDataBaseHelper.DATE , chatLog.getSendTime());
        map.put(ChatDataBaseHelper.IS_NEW , "1");
        return map;
    }

    private Boolean isNetWorkUsable(){
        ConnectivityManager connectivityManagerCompat = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetWorkAvailable = connectivityManagerCompat.getActiveNetworkInfo() != null
                && connectivityManagerCompat.getActiveNetworkInfo().isConnected();
        return isNetWorkAvailable;
    }
}
