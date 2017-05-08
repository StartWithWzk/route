package com.qg.route.bean;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 聊天室实体
 * Created by FunriLy on 2017/5/2.
 * From small beginnings comes great things.
 */
public class ChatRoom {

    private int id;             //房间id
    private int routeId;        //路线id
    private String roomName;    //房间名字
    private int roomCount;      //房间人数
    private String destination; //终点名
    private int type;           //类型
    private double x;
    private double y;
    private long createTime;
    private String description;

    public ChatRoom(int id, int routeId, String roomName, double x, double y, String destination, int type){
        this.id = id;
        this.routeId = routeId;
        this.roomName = roomName;
        this.destination = destination;
        this.roomCount = 1;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
