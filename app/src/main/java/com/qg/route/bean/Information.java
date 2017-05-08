package com.qg.route.bean;
import com.qg.route.utils.CommonDate;

import java.util.Date;

/**
 * 系统通知实体类
 * Created by FunriLy on 2017/4/15.
 * From small beginnings comes great things.
 */
public class Information {

    private int id;             //通知id
    private int sendId;         //发送者id
    private int receiveId;      //接收者id
    private String content;     //主要内容
    private int flag;           //记录标志
                                // 0为还未任何处理，1同意好友申请，
                                // 2忽略好友申请，3拒绝好友申请
    private long sendTime;      //发送时间
    private int deleFlag;       //删除标志，初始化为0，发送者删除为1，接收者删除为2

    public Information(){
        this.sendTime = CommonDate.getNowDate().getTime();
    }

    public Information(int sendId, int receiveId, int flag){
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.flag = flag;
    }

    public Information(int sendId, int receiveId, String content, int flag, int deleFlag){
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.content = content;
        this.flag = flag;
        this.deleFlag = deleFlag;
    }

    //get && set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSendId() {
        return sendId;
    }

    public void setSendId(int sendid) {
        this.sendId = sendid;
    }

    public int getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(int receiveId) {
        this.receiveId = receiveId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getDeleFlag() {
        return deleFlag;
    }

    public void setDeleFlag(int deleFlag) {
        this.deleFlag = deleFlag;
    }

    //toString

    @Override
    public String toString() {
        return "Information{" +
                "id=" + id +
                ", sendid=" + sendId +
                ", receiveId=" + receiveId +
                ", content='" + content + '\'' +
                ", flag=" + flag +
                ", sendTime=" + sendTime +
                ", deleFlag=" + deleFlag +
                '}';
    }
}
