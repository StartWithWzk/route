package com.qg.route.chat;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        String name = null;
        String id = null;
        Intent i = getIntent();
        name = i.getStringExtra(ChatFragment.USER_NAME);
        id = i.getStringExtra(ChatFragment.USER_ID);
        return ChatFragment.newInstance(name , id);
    }
}
