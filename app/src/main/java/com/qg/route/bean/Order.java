package com.qg.route.bean;

import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 * Created by FunriLy on 2017/4/11.
 * From small beginnings comes great things.
 */
public class Order {

    private int id;                 //订单id
    private Date time;              //下单时间
    private int userid;                //用户id
    private int busiId;             //商家id
    private List<Goods> goodses;    //商品列表
    private double allprice;        //总价格

    public Order(){}

    public Order(int id, Date time, int userid, int busiId){
        this.id = id;
        this.time = time;
        this.userid = userid;
        this.busiId = busiId;
    }

    // get & set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getBusiId() {
        return busiId;
    }

    public void setBusiId(int busiId) {
        this.busiId = busiId;
    }

    public List<Goods> getGoodses() {
        return goodses;
    }

    public void setGoodses(List<Goods> goodses) {
        this.goodses = goodses;
    }

    public double getAllprice() {
        return allprice;
    }

    public void setAllprice(double allprice) {
        this.allprice = allprice;
    }

    // toSrting

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", time=" + time +
                ", userid=" + userid +
                ", busiId=" + busiId +
                ", goodses=" + goodses +
                ", allprice=" + allprice +
                '}';
    }
}
