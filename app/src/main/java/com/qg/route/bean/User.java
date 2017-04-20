package com.qg.route.bean;

import java.util.Date;

/**
 * 用户实体类
 * Created by FunriLy on 2017/4/5.
 * From small beginnings comes great things.
 */
public class User {

    private int userid;            //userid
    private String password;        //密码
    private String name;            //姓名
    private int sex;                //性别
    private Date createDate;        //创建时间
    private String introduction;    //个人简介

    public User(){}

    // get && set
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "userid='" + userid + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", createDate=" + createDate +
                ", introduction='" + introduction + '\'' +
                '}';
    }
}
