package com.qg.route.moments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.User;
import com.qg.route.chat.ChatBean;
import com.qg.route.login.LoginActivity;
import com.qg.route.utils.ChatDataBaseUtil;
import com.qg.route.utils.Constant;
import com.qg.route.utils.FriendDataBaseHelper;
import com.qg.route.utils.FriendDataBaseUtil;
import com.qg.route.utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Response;

/**
 * Created by Mr_Do on 2017/4/25.
 */

public class MomentsActivity extends AppCompatActivity {


    private static final String PICTURE_KEY = "file";
    private static final int SIGN_NETWORK_UNUSABLE = 0;
    private static final int SIGN_SUCCESSFUL = 1;
    private static final int SIGN_CHANGE_DATA = 2;
    private String mId = "";//要显示的id
    private String mMyId = Constant.USER_ID;//自己的id
    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mPersonImage;

    private TextView mName;
    private TextView mIntroduction;
    private ImageView mSexImage;

    private User mData;
    private Handler mHandler ;
    private Uri mImageUri;

    private final static String MOMENTS_FRAGMENT_TITLE = "动态";
    private final static String DATA_FRAGMENT_TITLE = "我的资料";
    private final static String DATA_FRAGMENT_FRIEND_TITLE = "TA的资料";
    private final static String NETWORK_UNUSABLE = "网络连接错误";

    private String mFriendImageUrl = Constant.MomentsUrl.PERSON_HEAD_IMAGE;
    private String mUpdateImageUrl = Constant.MomentsUrl.UPDATE_IMAGE;
    private String mDeleteFriendUrl = Constant.MomentsUrl.DELETE_FRIEND;
    private String mGetPersonDataUrl = Constant.MomentsUrl.PERSON_DATA_GET;
    private String mAddFriendUrl = Constant.ChatUrl.ADD_FRIEND;

    private MomentsPagerAdapter mAdapter;

    public static final String ID = "id";
    public static Intent newIntent(String id , Context context){
        Intent intent = new Intent(context , MomentsActivity.class);
        intent.putExtra(ID , id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mToolBar = (Toolbar) findViewById(R.id.moments_toolbar);
        mViewPager = (ViewPager) findViewById(R.id.moments_view_pager);
        mPersonImage = (ImageView) findViewById(R.id.person_image);
        mName = (TextView) findViewById(R.id.moments_title_name);
        mIntroduction = (TextView) findViewById(R.id.moments_title_introduction);
        mSexImage = (ImageView) findViewById(R.id.sex_image);
        mId = getIntent().getStringExtra(ID);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleMessage();
        getPersonData();
    }

    private void getPersonData() {
        HttpUtil.DoGet(mGetPersonDataUrl + mId,
                new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                Gson gson = new Gson();
                RequestResult<User> result = null;
                try {
                    result = gson.fromJson(response.body().charStream(), new TypeToken<RequestResult<User>>(){}.getType());
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(result != null && result.getState() == 145){
                    mData = result.getData();
                    mHandler.sendEmptyMessage(SIGN_CHANGE_DATA);
                }
            }
            @Override
            public void onFailure(IOException e) {

            }
            @Override
            public void onFailure() {
            }
        },false);
    }

    private void handleMessage() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SIGN_NETWORK_UNUSABLE :
                        Toast.makeText(MomentsActivity.this , NETWORK_UNUSABLE , Toast.LENGTH_LONG).show();
                        break;
                    case SIGN_SUCCESSFUL :
                        mPersonImage.setImageURI(mImageUri);
                        ChatGlideUtil.setStringSignature(new StringSignature(new Date().getTime()+""));
                        break;
                    case SIGN_CHANGE_DATA :
                        mName.setText(mData.getName());
                        mIntroduction.setText(mData.getIntroduction());
                        if(mData.getSex() == 1) {
                            mSexImage.setImageResource(R.drawable.man);
                        }else{
                            mSexImage.setImageResource(R.drawable.woman);
                        }
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initUI() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new MomentsPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(MomentsFragment.newInstance(mId) , MOMENTS_FRAGMENT_TITLE);
        if(mId.equals(mMyId))
            mAdapter.addFragment(DataFragment.newInstance(mId) , DATA_FRAGMENT_TITLE);
        else mAdapter.addFragment(DataFragment.newInstance(mId) , DATA_FRAGMENT_FRIEND_TITLE);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.addTab(mTabLayout.newTab().setText("ONE"));
        mTabLayout.addTab(mTabLayout.newTab().setText("TWO"));
        mTabLayout.setupWithViewPager(mViewPager);
        ChatGlideUtil.loadImageByUrl(this ,mFriendImageUrl + mId +".jpg" , mPersonImage ,R.drawable.normal_person_image);

        mPersonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mId.equals(mMyId)) {
                    Intent local = new Intent();
                    local.setType("image/*");
                    local.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(local, 1);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mId.equals(mMyId)) {
            getMenuInflater().inflate(R.menu.moment_and_data, menu);
        }else{
            List< ChatBean> list = FriendDataBaseUtil.query(this , new String[]{FriendDataBaseHelper.USER_ID} , new String[]{mId} , null);
            if(list != null && list.size() > 0){
                getMenuInflater().inflate(R.menu.friend_operation , menu);
            }else {
                getMenuInflater().inflate(R.menu.stranger , menu);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_friend :
                HttpUtil.DoGet(mDeleteFriendUrl + mId, new HttpUtil.HttpConnectCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        if(response != null){
                            RequestResult requestResult = null;
                            Gson gson = new Gson();
                            try{
                                requestResult = gson.fromJson(response.body().charStream() , RequestResult.class);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if(requestResult != null){
                                Log.e("State",requestResult.getState()+ " " +requestResult.getStateInfo());
                            }
                            if(requestResult != null && requestResult.getState() == 167) {
                                FriendDataBaseUtil.delete(MomentsActivity.this, new String[]{FriendDataBaseHelper.USER_ID}, new String[]{mId});
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
                break;
            case R.id.publish_moment:
                Intent intent = new Intent(this , PublishActivity.class);
                startActivity(intent);
                break;
            case R.id.update_data:
                intent = new Intent(this , UpdateDataActivity.class);
                startActivity(intent);
                break;
            case R.id.add_friend :
                addFriend();
                break;
            case android.R.id.home :
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void addFriend(){
        HttpUtil.DoGet(mAddFriendUrl + mId, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {

            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        } , false);
    }

    private class MomentsPagerAdapter extends FragmentPagerAdapter{

        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public MomentsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment ,String name){
            mFragments.add(fragment);
            mFragmentTitles.add(name);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            mImageUri = data.getData();
            // TODO: 2017/5/9 这里需要再兼容小米机型
            ContentResolver cr = this.getContentResolver();
            Cursor c = cr.query(mImageUri, null, null, null, null);
            c.moveToFirst();
            //这是获取的图片保存在sdcard中的位置  
            String imagePath = c.getString(c.getColumnIndex("_data"));
            List<File> files = new ArrayList<>();
            files.add(new File(imagePath));
            List<String> picKeys = new ArrayList<>();
            picKeys.add(PICTURE_KEY);
            HttpUtil.PostPicAndText(mUpdateImageUrl,
                    files , picKeys , null, null ,
                    new HttpUtil.HttpConnectCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            mHandler.sendEmptyMessage(SIGN_SUCCESSFUL);
                        }

                        @Override
                        public void onFailure(IOException e) {
                            mHandler.sendEmptyMessage(SIGN_NETWORK_UNUSABLE);
                        }

                        @Override
                        public void onFailure() {

                        }
                    } , false);
        }
    }

}
