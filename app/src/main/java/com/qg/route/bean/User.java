package com.qg.route.bean;

import java.util.Date;

/**
 * 用户实体类
 * Created by FunriLy on 2017/4/5.
 * From small beginnings comes great things.
 */
public class User {

    private int userid;            //userid
    private String name;            //姓名
    private int sex;                //性别
    private long createDate;        //创建时间
    private String introduction;    //个人简介
    private String suitability;

    public User(){}

    // get && set
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSuitability() {
        return suitability;
    }

    public void setSuitability(String suitability) {
        this.suitability = suitability;
    }

}
