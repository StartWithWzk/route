package com.qg.route.chat;

import android.support.v4.app.Fragment;

/**
 * Created by Mr_Do on 2017/4/19.
 */

public class ChatListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ChatListFragment();
    }
}
