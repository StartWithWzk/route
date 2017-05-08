package com.qg.route.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qg.route.bean.ChatLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr_Do on 2017/4/23.
 */

public class ChatDataBaseUtil {
    private static SQLiteDatabase sSQLiteDatabase;

    public static void insert(Context context , Map<String , String> map){
         sSQLiteDatabase = ChatDataBaseHelper.getDataBaseInstance(context).getWritableDatabase();
        sSQLiteDatabase.insert(ChatDataBaseHelper.TABLE_NAME , null ,getContentValues(map));
    }

    private static ContentValues getContentValues(Map<String , String> map){
        ContentValues contentValues = new ContentValues();
        for(Map.Entry<String , String> entry : map.entrySet()){
            contentValues.put(entry.getKey() , entry.getValue());
        }
        return contentValues;
    }

    public static void delete(Context context , String[] which , String[] values){
        sSQLiteDatabase = ChatDataBaseHelper.getDataBaseInstance(context).getWritableDatabase();
        sSQLiteDatabase.delete(ChatDataBaseHelper.TABLE_NAME , getWhich(which) ,values);
    }

    private static String getWhich(String[] which){
        if(which!=null) {
            String sql = "";
            int k = 0;
            for (; k < which.length - 1; k++) {
                sql += which[k] + " = ?" + " and ";
            }
            sql += which[k] + " = ?";
            return sql;
        }else return null;
    }

    public static void updata(Context context , Map<String , String> map , String[] which , String[] values){
        sSQLiteDatabase = ChatDataBaseHelper.getDataBaseInstance(context).getWritableDatabase();
        sSQLiteDatabase.update(ChatDataBaseHelper.TABLE_NAME , getContentValues(map) , getWhich(which) , values);
    }

    public static List<ChatLog> query(Context context , String[] which , String[] values , String orderBy){
        List<ChatLog> logs = new ArrayList<>();
        sSQLiteDatabase = ChatDataBaseHelper.getDataBaseInstance(context).getReadableDatabase();
        Cursor cursor = sSQLiteDatabase.query(ChatDataBaseHelper.TABLE_NAME ,null , getWhich(which) , values , null , null ,orderBy);
        while (cursor.moveToNext()){
            ChatLog chatLog = new ChatLog();
            chatLog.setSendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.FROM))));
            chatLog.setReceiveId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.TO))));
            chatLog.setContent(cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.CONTENT)));
            chatLog.setSendTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.DATE))));
            logs.add(chatLog);
        }
        return logs;
    }
}
