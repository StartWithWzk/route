package com.qg.route.bean;

/**
 * 好友实体类
 * Created by FunriLy on 2017/4/8.
 * From small beginnings comes great things.
 */
public class Relation {

    private int id;
    private int minUserId;
    private int maxUserId;

    public Relation(){}

    // get && set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinUserId() {
        return minUserId;
    }

    public void setMinUserId(int minUserId) {
        this.minUserId = minUserId;
    }

    public int getMaxUserId() {
        return maxUserId;
    }

    public void setMaxUserId(int maxUserId) {
        this.maxUserId = maxUserId;
    }
}
