package com.qg.route.moments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qg.route.R;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Created by Mr_Do on 2017/5/5.
 */

public class PublishFragment extends Fragment {

    private static final int SIGN_SEND_FAIL = 0;
    private static final String SEND_FAIL = "发送失败";
    private static final String PUBLISH_URL = Constant.MomentsUrl.MOMENT_PUBLISH;
    private static final int LACK_IMAGE_OR_MESSAGE = 1;
    private static final String LACK_THING = "图片或文字都有才丰富";
    private String mImagePath = null;
    private EditText mEditText;
    private ImageView mImageView;
    private String mContent;
    private Button mButton;
    private Handler mHandler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish , container , false);
        mEditText = (EditText) view.findViewById(R.id.moments_content);
        mImageView = (ImageView) view.findViewById(R.id.add_photo);
        mButton = (Button) view.findViewById(R.id.publish_button);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SIGN_SEND_FAIL:
                        Toast.makeText(getActivity() , SEND_FAIL ,Toast.LENGTH_LONG).show();
                        mButton.setEnabled(true);
                        break;
                    case LACK_IMAGE_OR_MESSAGE:
                        Toast.makeText(getActivity() , LACK_THING ,Toast.LENGTH_LONG).show();
                        mButton.setEnabled(true);
                        break;
                }

                super.handleMessage(msg);
            }
        };
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local,1);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButton.setEnabled(false);
                mContent = mEditText.getText().toString();
                mEditText.setText("");
                List<File> files = new ArrayList<File>();
                if(mImagePath != null) {
                    File file = new File(mImagePath);
                    files.add(file);
                }
                List<String> pic_keys = new ArrayList<String>();
                pic_keys.add("file");
                if(files.size() != 0 && mContent != null) {
                    HttpUtil.PostPicAndText(PUBLISH_URL, files, pic_keys, mContent, "message", new HttpUtil.HttpConnectCallback() {
                        @Override
                        public void onSuccess(Response response) {

                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(IOException e) {
                            mHandler.sendEmptyMessage(SIGN_SEND_FAIL);
                        }

                        @Override
                        public void onFailure() {

                        }
                    }, false);
                }else {
                    mHandler.sendEmptyMessage(LACK_IMAGE_OR_MESSAGE);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            Uri uri = data.getData();
            mImageView.setImageURI(uri);
            ContentResolver cr = getActivity().getContentResolver();
            Cursor c = cr.query(uri, null, null, null, null);
            c.moveToFirst();
            //这是获取的图片保存在sdcard中的位置  
            mImagePath = c.getString(c.getColumnIndex("_data"));
        }
    }
}
