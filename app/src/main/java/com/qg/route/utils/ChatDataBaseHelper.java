package com.qg.route.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mr_Do on 2017/4/23.
 */

public class ChatDataBaseHelper extends SQLiteOpenHelper{


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "chat_content_db";
    public static final String TABLE_NAME = "chat_content_tb";
    public static final String _ID = "_id";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String DATE = "date";
    public static final String CONTENT = "content";
    public static final String IS_NEW = "is_new";
    public static final String USER_ID = "user_id";
    private SQLiteDatabase mDatabase;
    private Boolean mIsInitializing;

    private static final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FROM + " TEXT, " +
            TO + " TEXT, " +
            CONTENT + " TEXT, " +
            USER_ID + " TEXT, " +
            DATE + " TEXT, " +
            IS_NEW + " TEXT, "+
            " )";


    private static ChatDataBaseHelper mInstance = null;
    private ChatDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static ChatDataBaseHelper getDataBaseInstance(Context context){
        if(mInstance == null){
            synchronized (ChatDataBaseHelper.class){
                if(mInstance == null){
                    mInstance = new ChatDataBaseHelper(context, DATABASE_NAME ,null , DATABASE_VERSION);
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
