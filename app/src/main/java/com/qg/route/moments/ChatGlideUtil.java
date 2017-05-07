package com.qg.route.moments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.Date;

/**
 * Created by Mr_Do on 2017/5/6.
 */

public class ChatGlideUtil {

    private static StringSignature stringSignature = new StringSignature(new Date().getTime()+"");

    public static void setStringSignature(StringSignature stringSignature) {
        ChatGlideUtil.stringSignature = stringSignature;
    }

    public static void loadImageByUrl(Context context , String Url , ImageView imageView ){
        Glide.with(context).load(Url).signature(stringSignature).into(imageView);
    }
    public static void loadImageByUrl(Context context , String Url , ImageView imageView , int resId){
        Glide.with(context).load(Url).signature(stringSignature).error(resId).into(imageView);
    }

    public static void loadImageByUrl(Fragment fragment , String Url , ImageView imageView ){
        Glide.with(fragment).load(Url).signature(stringSignature).into(imageView);
    }

    public static void loadImageByUrl(Fragment fragment , String Url , ImageView imageView , int resId){
        Glide.with(fragment).load(Url).error(resId).signature(stringSignature).into(imageView);
    }
}
