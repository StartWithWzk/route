package com.qg.route.chat;

/**
 * Created by Mr_Do on 2017/5/7.
 */

public class ChatBean {
    private   String name ;
    private   String last_content;
    private   String last_time;
    private   String is_circle;
    private   String sex;
    private   String introduction;
    private   String user_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_content() {
        return last_content;
    }

    public void setLast_content(String last_content) {
        this.last_content = last_content;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getIs_circle() {
        return is_circle;
    }

    public void setIs_circle(String is_circle) {
        this.is_circle = is_circle;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
