package com.qg.route;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qg.route.route.RouteActivity;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_PERMISSION = 0;

    private static final String[] PERMISSION_LIST = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission(REQUEST_PERMISSION, PERMISSION_LIST);
    }

    public void onMap(View view) {
        startActivity(new Intent(this, RouteActivity.class));
    }
}
