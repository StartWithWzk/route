package com.qg.route.chat;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.ChatLog;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.main.MainActivity;
import com.qg.route.utils.ChatDataBaseHelper;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.Constant;
import com.qg.route.utils.FriendDataBaseHelper;
import com.qg.route.utils.FriendDataBaseUtil;
import com.qg.route.utils.HttpUtil;
import com.qg.route.utils.JsonUtil;
import com.qg.route.utils.URLHelper;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.ByteString;

/**
 * Created by Mr_Do on 2017/4/23.
 */

public class ChatService extends Service {

    private final static String OFFLINE_MESSAGE_URL = Constant.ChatUrl.OFFLINE_MESSAGE_URL;
    private final static String GET_DATA = Constant.MomentsUrl.PERSON_DATA_GET ;
    private final static String CHAT_URL = Constant.ChatUrl.WEB_SOCKET;
    public final static String CHANGE_CONTENT = "com.qg.route.chatservice.CHANGE_CONTENT";
    public final static String CHANGE_LIST = "com.qg.route.chatservice.CHANGE_LIST";
    private SoundPool mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

    public static  Intent newIntent(Context context){
        return new Intent(context , ChatService.class);
    }
    private void getOffLineMessage(){
        HttpUtil.DoGet(OFFLINE_MESSAGE_URL, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                if(response != null){
                    RequestResult<List<ChatLog>> requestResult = null;
                    Gson gson = new Gson();
                    try {
                        requestResult = gson.fromJson(response.body().charStream() , RequestResult.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(requestResult != null && requestResult.getState() != -1){
                        if(requestResult.getData() != null) {
                            String json = gson.toJson(requestResult.getData(), new TypeToken<List<ChatLog>>() {
                            }.getType());
                            JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
                            for (JsonElement element : jsonArray) {
                                ChatLog chatLog = gson.fromJson(element, ChatLog.class);
                                save(chatLog , null , false);
                            }
                        }
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

    private void save(final ChatLog chatLog , final String text , final Boolean mustSend){
        Map<String,String> map = newMessage(chatLog);
        ChatDataBaseUtil.insert(ChatService.this , map);

        HttpUtil.DoGet(GET_DATA + chatLog.getSendId(),
                new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                if(response != null){
                    RequestResult<User> requestResult = null;
                    Gson gson = new Gson();
                    try {
                        requestResult = gson.fromJson(response.body().charStream() , new TypeToken<RequestResult<User>>(){}.getType());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(requestResult != null){
                        if(requestResult.getData() != null) {
                            User user = requestResult.getData();
                            //存到数据库
                            Map<String, String> map1 = new HashMap<String, String>();
                            if (user != null) {
                                map1.put(FriendDataBaseHelper.NAME, user.getName());
                                map1.put(FriendDataBaseHelper.USER_ID, chatLog.getReceiveId() + "");//receive才是群
                                map1.put(FriendDataBaseHelper.LAST_CONTENT, chatLog.getContent());
                                map1.put(FriendDataBaseHelper.LAST_TIME, chatLog.getSendTime() +"");
                                if(chatLog.getFlag() == 3){
                                    map1.put(FriendDataBaseHelper.IS_CIRCLE , "1");
                                }else map1.put(FriendDataBaseHelper.IS_CIRCLE , "0");
                                FriendDataBaseUtil.replace(ChatService.this, map1);
                            }
                            if(mustSend) {
                                Intent intent = new Intent(CHANGE_CONTENT);
                                intent.putExtra(CHANGE_CONTENT, text);
                                sendBroadcast(intent);
                                intent = new Intent(CHANGE_LIST);
                                sendBroadcast(intent);
                            }
                        }
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(HttpUtil.getSession()!= null && isNetWorkUsable()) {
            getOffLineMessage();
            HttpUtil.connectWebSocket(CHAT_URL, new HttpUtil.SocketConnectCallback() {
                @Override
                public void onMessage(String text) {
                    Log.e("ONMESSAGE",text);
                    ChatLog chatLog = toChatLog(text);
                    save(chatLog , text , true);
                    AudioManager am = (AudioManager) ChatService.this
                            .getSystemService(Context.AUDIO_SERVICE);
                    float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    float volumnRatio = volumnCurrent / audioMaxVolumn;
                    mSoundPool.play(mSoundPool.load(ChatService.this , R.raw.message , 1) , volumnRatio ,volumnRatio , 1 , 0 , 1);
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
    }

    private Map<String,String> newMessage(ChatLog chatLog){
        Map<String , String> map = new HashMap<String, String>();
        map.put(ChatDataBaseHelper.USER_ID , chatLog.getId()+"");
        map.put(ChatDataBaseHelper.FROM , chatLog.getSendId() + "");
        map.put(ChatDataBaseHelper.TO , chatLog.getReceiveId() + "");
        map.put(ChatDataBaseHelper.CONTENT , chatLog.getContent());
        map.put(ChatDataBaseHelper.DATE , chatLog.getSendTime()+"");
        map.put(ChatDataBaseHelper.IS_NEW , "1");
        return map;
    }

    private Boolean isNetWorkUsable(){
        ConnectivityManager connectivityManagerCompat = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetWorkAvailable = connectivityManagerCompat.getActiveNetworkInfo() != null
                && connectivityManagerCompat.getActiveNetworkInfo().isConnected();
        return isNetWorkAvailable;
    }

    @Override
    public void onDestroy() {
        HttpUtil.closeWebSocket();
        Log.e("SERVICE","DESTROY");
        mSoundPool.release();
        super.onDestroy();
    }
}
