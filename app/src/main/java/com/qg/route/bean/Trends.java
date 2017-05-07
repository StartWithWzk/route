package com.qg.route.bean;



import com.qg.route.utils.CommonDate;

import java.util.Date;

/**
 * Created by FunriLy on 2017/4/25.
 * From small beginnings comes great things.
 */
public class Trends {

    private String id;          //uuid,用于存储图片
    private int userid;         //用户id
    private String message;     //文字消息
    private long sendTime;      //发送
    private int photoCount;     //相片数量
    private int power;          //权限等级

    public Trends(){

    }

    public Trends(String id, int userid, String message, int photoCount){
        this.id = id;
        this.userid = userid;
        this.message = message;
        this.photoCount = photoCount;
    }

    // get & set

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    // toString

    @Override
    public String toString() {
        return "Trends{" +
                "id='" + id + '\'' +
                ", userid=" + userid +
                ", message='" + message + '\'' +
                ", sendTime=" + sendTime +
                ", photoCount=" + photoCount +
                '}';
    }
}
