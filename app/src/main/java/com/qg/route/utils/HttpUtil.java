package com.qg.route.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Mr_Do on 2017/4/6.
 */

public class HttpUtil {

    private static File sCacheDirectory = null;
    private static String sSession = null;

    /**
     * 设置session
     * @param session
     */
    public static void setSession(String session){
        sSession = session;
    }


    /**
     * 设置缓存目录
     * @param dir
     * @return
     */
    public static void setCacheDirary(File dir){
        if(sCacheDirectory == null)
            sCacheDirectory = dir;
    }

    public interface HttpConnectCallback{
        /**
         * 网络Http请求成功时回调
         */
        void onSuccess(Response response);

        /**
         * 网络请求出现异常时回调
         */
        void onFailure(IOException e);

        /**
         * 有返回但不成功时调用
         */
        void onFailure();
    }
    public interface SocketConnectCallback {

        /**
         * 接受到文字信息时回调
         * @param text
         */
        void onMessage(String text);

        /**
         * 接受到二进制数组时回调
         * @param bytes
         */
        void onMessage(ByteString bytes);
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    //修改CONNECT_TIME_OUT可以修改连接等待时间
    private static final int CONNECT_TIME_OUT = 30;
    //修改CACHE_TIME可以修改缓存时间
    private static final int CACHE_TIME = 30;
    //修改CACHE_LENGTH可以修改规定的缓存大小
    private static final long CACHE_LENGTH = 10 * 1024 * 1024;


    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    private static WebSocket sWebSocket;

    static {
        if(sCacheDirectory != null) {
            Cache cache = new Cache(sCacheDirectory, CACHE_LENGTH);
            OK_HTTP_CLIENT.newBuilder().connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS).cache(cache);
        }else{
            OK_HTTP_CLIENT.newBuilder().connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        }
    }

    /**
     * 同步请求
     * @param request
     * @return response
     * @throws IOException
     */
    private static Response synchronization(Request request) throws IOException {
        return OK_HTTP_CLIENT.newCall(request).execute();
    }

    /**
     * 异步请求
     * @param request
     * @param responseCallback
     * @return
     */
    private static void asynchronism(Request request, Callback responseCallback){
        OK_HTTP_CLIENT.newCall(request).enqueue(responseCallback);
    }

    /**
     * 异步WebSocket请求
     * @param request
     * @param webSocketListener
     */
    private static void asynchronizationWS(Request request , WebSocketListener webSocketListener){
        OK_HTTP_CLIENT.newWebSocket(request , webSocketListener);
    }

    private static void addSession(Request.Builder builder){
        if(sSession != null)
            builder.addHeader("cookie",sSession);
    }

    /**
     * 创建Get的request
     * @param url
     * @return request
     */
    private static Request buildRequest(String url){
        Request.Builder builder = new Request.Builder();
        addSession(builder);
        return builder.
                cacheControl(new CacheControl.Builder().maxStale(CACHE_TIME , TimeUnit.SECONDS).build())
                .url(url)
                .get()
                .build();
    }

    /**
     * 创建Post的request
     * @param url
     * @param requestBody
     * @return request
     */
    private static Request buildRequest(String url, RequestBody requestBody){
        Request.Builder builder = new Request.Builder();
        addSession(builder);
        return builder.url(url).post(requestBody).build();
    }

    /**
     * 创建WebSocket的request
     * @param url
     */
    private static Request buildWebSocketRequest(String url){
        return new Request.Builder().url(url).build();
    }


    /**
     * 创建提交Json的RequestBody
     * @param json
     * @return requestBody
     */
    private static RequestBody buildRequestBody(String json){
        return RequestBody.create(JSON , json);
    }

    /**
     * 创建提交键值对的RequestBody
     * map里面的键值对便是要传输的键值对数据
     * @param map
     * @return
     */
    private static RequestBody buildRequestBody(@NonNull Map<String, String> map){
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for(Map.Entry<String , String>entry : map.entrySet()){
            formBodyBuilder.add(entry.getKey() , entry.getValue());
        }
        return formBodyBuilder.build();
    }

    /**
     * 创建提交图片的RequestBody
     * @param list
     * @param picKey
     */
    private static RequestBody buildRequestBody(@NonNull List<File> list, String picKey){
        MultipartBody.Builder multipartBodyBuilder = null;

        for(File file : list){
            multipartBodyBuilder.addFormDataPart(picKey , file.getName() , RequestBody.create(MEDIA_TYPE_PNG , file));
        }

        return multipartBodyBuilder.build();
    }

    /**
     * 处理同步请求返回的response
     * @param request
     * @param connectCallback
     */
    private static void analyseSynResponse(Request request, HttpConnectCallback connectCallback){
        try {
            Response response = synchronization(request);
            if(response.isSuccessful()){
                connectCallback.onSuccess(response);
            }else connectCallback.onFailure();
        } catch (IOException e) {
            connectCallback.onFailure(e);
        }
    }

    /**
     * 处理异步请求返回的response
     * @param request
     * @param connectCallback
     */
    private static void analyseAsynResponse(Request request, final HttpConnectCallback connectCallback){
        asynchronism(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                connectCallback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                connectCallback.onSuccess(response);
            }
        });
    }

    /**
     * 处理请求
     * @param  request
     * @param  connectCallback
     * @param  isSyn
     */
    private static void analyseResponse(Request request, final HttpConnectCallback connectCallback, Boolean isSyn){
        if(isSyn)
            analyseSynResponse(request , connectCallback);
        else
            analyseAsynResponse(request , connectCallback);
    }


    /**
     * 使用Get方法获取数据
     * @param url 要求是String类型
     * @param connectCallback
     * @param isSyn 是否同步发送Get请求
     * @return
     */
    public static void DoGet(String url , HttpConnectCallback connectCallback , Boolean isSyn){
        Request request = buildRequest(url);
        analyseResponse(request , connectCallback ,isSyn);
    }


    /**
     * 使用Post方法上传Json
     * @param  url
     * @param  json
     * @param  connectCallback
     * @param  isSyn
     * @return
     */
    public  static void PostJson(String url , String json , HttpConnectCallback connectCallback , Boolean isSyn){
        RequestBody requestBody = buildRequestBody(json);
        Request request = buildRequest(url , requestBody);
        analyseResponse(request , connectCallback , isSyn);
    }

    /**
     * 使用Post方法上传键值对
     * @param url
     * @param map
     * @param connectCallback
     * @param isSyn
     * @return
     */
    public static void PostMap(String url , Map<String , String> map , HttpConnectCallback connectCallback , Boolean isSyn){
        RequestBody requestBody = buildRequestBody(map);
        Request request = buildRequest(url , requestBody);
        analyseResponse(request , connectCallback , isSyn);
    }

    /**
     * 使用同Post方法上传png图片（可单张也可以多张）
     * @param url
     * @param list
     * @param pic_key
     * @param connectCallback
     * @param isSyn
     * @return
     */
    public static void PostPic(String url , List<File> list , String pic_key , HttpConnectCallback connectCallback , Boolean isSyn){
        RequestBody requestBody = buildRequestBody(list , pic_key);
        Request request = buildRequest(url , requestBody);
        analyseResponse(request , connectCallback , isSyn);
    }

    /**
     * 连接socket
     * @param url
     */
    public static void connectWebSocket(String url , final SocketConnectCallback connectCallback){
        Request request = buildWebSocketRequest(url);
        OK_HTTP_CLIENT.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                sWebSocket = webSocket;
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                connectCallback.onMessage(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                connectCallback.onMessage(bytes);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }
        });
    }

    /**
     * 发送即时消息
     * @param text
     */
    public static void sendMessage(String text){
        if(sWebSocket != null){
            sWebSocket.send(text);
        }
    }
}
