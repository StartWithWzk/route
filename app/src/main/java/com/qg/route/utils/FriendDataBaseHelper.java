package com.qg.route.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mr_Do on 2017/5/7.
 */

public class FriendDataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "friend_list_db";
    public static final String TABLE_NAME = "friend_list_tb";
    public static final String NAME = "_name";
    public static final String LAST_CONTENT = "_last_content";
    public static final String LAST_TIME = "_last_time";
    public static final String IS_CIRCLE = "_is_circle";
    public static final String SEX = "_sex";
    public static final String INTRODUCTION = "_introduction";
    public static final String USER_ID = "_user_id";
    private SQLiteDatabase mDatabase;
    private Boolean mIsInitializing;


    private static final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" +
            USER_ID + " TEXT PRIMARY KEY, " +
            NAME + " TEXT, " +
            LAST_CONTENT + " TEXT, " +
            LAST_TIME + " TEXT, " +
            IS_CIRCLE + " TEXT, " +
            SEX + " TEXT, " +
            INTRODUCTION + " TEXT "+
            " )";

    private static FriendDataBaseHelper mInstance = null;

    private FriendDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static FriendDataBaseHelper getDataBaseInstance(Context context){
        if(mInstance == null){
            synchronized (FriendDataBaseHelper.class){
                if(mInstance == null){
                    mInstance = new FriendDataBaseHelper(context, DATABASE_NAME ,null , DATABASE_VERSION);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TEST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public SQLiteDatabase getReadableDataBase(){
        return getDatabaseLocked(false);
    }

    public SQLiteDatabase getWritableDataBase(){
        return getDatabaseLocked(true);
    }

    private SQLiteDatabase getDatabaseLocked(boolean writable) {
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                // 说明用户手动调用close方法将mDatabase关闭了,需要重启打开
                mDatabase = null;
            } else if (!writable || !mDatabase.isReadOnly()) {
                return mDatabase;
            }
        }

        if (mIsInitializing) {
            // 防止递归调用.
            throw new IllegalStateException("getDatabase called recursively");
        }

        SQLiteDatabase db = mDatabase;

        // 打开数据库前将mIsInitializing置为true,结束时置为false.
        mIsInitializing = true;
        if (writable) {
            db = mInstance.getWritableDatabase();
        }else {
            db = mInstance.getReadableDatabase();
        }


        mIsInitializing = false;
        if (db != null && db != mDatabase) {
            db.close();
        }
        return db;
    }
}
