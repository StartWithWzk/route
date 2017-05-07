package com.qg.route.moments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.qg.route.R;

/**
 * Created by Mr_Do on 2017/5/4.
 */

public class PublishActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_publish);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.moments_publish_layout);
        if(fragment == null){
            fragment = new PublishFragment();
            fm.beginTransaction()
                    .add(R.id.moments_publish_layout , fragment)
                    .commit();
        }
    }
}
