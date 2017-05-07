package com.qg.route.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qg.route.bean.ChatLog;
import com.qg.route.chat.ChatBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr_Do on 2017/5/7.
 */

public class FriendDataBaseUtil {
    private static SQLiteDatabase sSQLiteDatabase;

    public static void insert(Context context , Map<String , String> map){
        sSQLiteDatabase = FriendDataBaseHelper.getDataBaseInstance(context).getWritableDatabase();
        sSQLiteDatabase.insert(FriendDataBaseHelper.TABLE_NAME , null ,getContentValues(map));
    }

    private static ContentValues getContentValues(Map<String , String> map){
        ContentValues contentValues = new ContentValues();
        for(Map.Entry<String , String> entry : map.entrySet()){
            contentValues.put(entry.getKey() , entry.getValue());
        }
        return contentValues;
    }

    public static void delete(Context context , String[] which , String[] values){
        sSQLiteDatabase = FriendDataBaseHelper.getDataBaseInstance(context).getWritableDatabase();
        sSQLiteDatabase.delete(FriendDataBaseHelper.TABLE_NAME , getWhich(which) ,values);
    }

    private static String getWhich(String[] which){
        String sql = "";
        int k = 0 ;
        for(;k < which.length - 1 ; k++){
            sql += which[k] + " = ?" + " and ";
        }
        sql += which[k] + " = ?";
        return sql;
    }

    public static void updata(Context context , Map<String , String> map , String[] which , String[] values){
        sSQLiteDatabase = FriendDataBaseHelper.getDataBaseInstance(context).getWritableDatabase();
        sSQLiteDatabase.update(FriendDataBaseHelper.TABLE_NAME , getContentValues(map) , getWhich(which) , values);
    }

    public static void repleace(Context context , Map<String , String> map){
        sSQLiteDatabase = FriendDataBaseHelper.getDataBaseInstance(context).getWritableDatabase();
        sSQLiteDatabase.replace(FriendDataBaseHelper.TABLE_NAME , null , getContentValues(map));
    }

    public static List<ChatBean> query(Context context , String[] which , String[] values , String orderBy){
        List<ChatBean> logs = new ArrayList<>();
        sSQLiteDatabase = FriendDataBaseHelper.getDataBaseInstance(context).getReadableDatabase();
        Cursor cursor = sSQLiteDatabase.query(FriendDataBaseHelper.TABLE_NAME ,null , getWhich(which) , values , null , null ,orderBy);
        while (cursor.moveToNext()){
            ChatBean chatBean = new ChatBean();
            chatBean.setName(cursor.getString(cursor.getColumnIndex(FriendDataBaseHelper.NAME)));
            chatBean.setIntroduction(cursor.getString(cursor.getColumnIndex(FriendDataBaseHelper.INTRODUCTION)));
            chatBean.setIs_circle(cursor.getString(cursor.getColumnIndex(FriendDataBaseHelper.IS_CIRCLE)));
            chatBean.setLast_content(cursor.getString(cursor.getColumnIndex(FriendDataBaseHelper.LAST_CONTENT)));
            chatBean.setSex(cursor.getString(cursor.getColumnIndex(FriendDataBaseHelper.SEX)));
            chatBean.setUser_id(cursor.getString(cursor.getColumnIndex(FriendDataBaseHelper.USER_ID)));
            chatBean.setLast_time(cursor.getString(cursor.getColumnIndex(FriendDataBaseHelper.LAST_TIME)));
            logs.add(chatBean);
        }
        return logs;
    }
}
