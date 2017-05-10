package com.qg.route.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatActivity extends SingleFragmentActivity{

    public static Intent newIntent(Context context , String name , String id , Boolean isCircle){
        Intent intent = new Intent(context , ChatActivity.class);
        intent.putExtra(ChatFragment.USER_NAME , name);
        intent.putExtra(ChatFragment.USER_ID , id);
        intent.putExtra(ChatFragment.IS_CIRCLE , isCircle);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String name = null;
        String id = null;
        Boolean isCircle;
        Intent i = getIntent();
        name = i.getStringExtra(ChatFragment.USER_NAME);
        id = i.getStringExtra(ChatFragment.USER_ID);
        isCircle= i.getBooleanExtra(ChatFragment.IS_CIRCLE ,true);
        getSupportActionBar().setTitle(name);
        return ChatFragment.newInstance(name , id , isCircle);
    }
}
