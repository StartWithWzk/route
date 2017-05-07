package com.qg.route.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.chat.ChatService;
import com.qg.route.contacts.ContactsActivity;
import com.qg.route.moments.MomentsActivity;
import com.qg.route.utils.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by Mr_Do on 2017/4/30.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText mCountEdit;
    private EditText mPassWord;
    private Button mLoginButton;
    private Button mSignButton;
    private Handler mHandler;
    public static String sMyId;
    public static String sMyName;

    private final static int SIGN_NETWORK_UNUSABLE = 0;
    private final static int SIGN_NOT_CORRECT = 1;
    private final static String NETWORK_UNUSABLE = "网络连接出错";
    private final static String NOT_CORRECT = "账号或密码出错";
    private final static String LAND_URL = "http://118.89.54.17:8080/onway/user/login";
            //"http://118.89.54.17:8080/onway/user/login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mCountEdit = (EditText) findViewById(R.id.count_edit);
        mPassWord = (EditText) findViewById(R.id.password_edit);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mSignButton = (Button) findViewById(R.id.sign_button);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SIGN_NETWORK_UNUSABLE :
                        Toast.makeText(LoginActivity.this , NETWORK_UNUSABLE , Toast.LENGTH_LONG).show();
                        mLoginButton.setEnabled(true);
                        break;
                    case SIGN_NOT_CORRECT :
                        Toast.makeText(LoginActivity.this , NOT_CORRECT , Toast.LENGTH_LONG).show();
                        mLoginButton.setEnabled(true);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                land();
                mLoginButton.setEnabled(false);
            }
        });
    }

    private void land() {
        Map<String , String> map = new HashMap<>();
        map.put("userid",mCountEdit.getText().toString());
        map.put("password",mPassWord.getText().toString());
        Log.e("MAP",map.toString());
        HttpUtil.PostMap(LAND_URL, map, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                RequestResult requestResult = null;
                try {
                    Gson gson = new Gson();
                    requestResult = gson.fromJson(response.body().charStream(), new TypeToken<RequestResult<User>>() {
                    }.getType());
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (requestResult != null && requestResult.getState() == 121) {
                    User user = (User) requestResult.getData();
                    sMyId = user.getUserid() + "";
                    sMyName = user.getName();
                    Headers headers = response.headers();
                    Log.d("info_headers", "header " + headers);
                    List<String> cookies = headers.values("Set-Cookie");
                    if (cookies != null && cookies.size() > 0) {
                        String session = cookies.get(0);
                        HttpUtil.setSession(session);
                    }
                    Intent intent = ChatService.newIntent(LoginActivity.this);
                    startService(intent);
                    //intent = new Intent(LoginActivity.this, ContactsActivity.class);
                    intent = MomentsActivity.newIntent(sMyId , LoginActivity.this);
                    startActivity(intent);
                    finish();
                } else {
                    mHandler.sendEmptyMessage(SIGN_NOT_CORRECT);
                }

            }

            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(SIGN_NETWORK_UNUSABLE);
            }

            @Override
            public void onFailure() {

            }
        } , false);
    }
}
