package com.qg.route.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.qg.route.R;

/**
 * Created by Mr_Do on 2017/5/8.
 */

public class InformationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.information_list_container);

        if(fragment == null){
            fragment = new InformationFragment();
            fm.beginTransaction()
                    .add(R.id.information_list_container , fragment)
                    .commit();
        }
    }
}
