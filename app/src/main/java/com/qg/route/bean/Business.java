package com.qg.route.bean;

/**
 * 商家实体类
 * Created by FunriLy on 2017/4/11.
 * From small beginnings comes great things.
 */
public class Business {

    private int id;                 //商家id
    private String busiName;        //商家店名
    private String busiAddress;     //商家地址
    private String busiPhone;       //商家联系方式
    private double busiGrade;          //商家评分
    private int consumption;        //人均消费

    /**
     * 构造器
     */
    public Business(){

    }

    public Business(int id, String busiName, String busiAddress, String busiPhone,
                    double busiGrade, int consumption){
        this.id = id;
        this.busiName = busiName;
        this.busiAddress = busiAddress;
        this.busiPhone = busiPhone;
        this.busiGrade = busiGrade;
        this.consumption = consumption;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getBusiAddress() {
        return busiAddress;
    }

    public void setBusiAddress(String busiAddress) {
        this.busiAddress = busiAddress;
    }

    public String getBusiPhone() {
        return busiPhone;
    }

    public void setBusiPhone(String busiPhone) {
        this.busiPhone = busiPhone;
    }

    public double getBusiGrade() {
        return busiGrade;
    }

    public void setBusiGrade(double busiGrade) {
        this.busiGrade = busiGrade;
    }

    public int getConsumption() {
        return consumption;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    @Override
    public String toString() {
        return "Business{" +
                "id=" + id +
                ", busiName='" + busiName + '\'' +
                ", busiAddress='" + busiAddress + '\'' +
                ", busiPhone='" + busiPhone + '\'' +
                ", busiGrade=" + busiGrade +
                ", consumption=" + consumption +
                '}';
    }
}
