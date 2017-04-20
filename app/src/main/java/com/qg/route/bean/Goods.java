package com.qg.route.bean;

/**
 * 商品实体类
 * Created by FunriLy on 2017/4/11.
 * From small beginnings comes great things.
 */
public class Goods {

    private int id;             //商品id
    private int busiId;         //商家id
    private String classify;    //分类
    private String goodsName;   //商品名
    private double price;          //价格
    private int number;         //数量

    public Goods(){

    }

    public Goods(int id, int busiId, String classify, String goodsName, double price, int number){
        this.id = id;
        this.busiId = busiId;
        this.classify = classify;
        this.goodsName = goodsName;
        this.price = price;
        this.number = number;
    }

    // get & set
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBusiId() {
        return busiId;
    }

    public void setBusiId(int busiId) {
        this.busiId = busiId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    // toString

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", busiId=" + busiId +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", number=" + number +
                '}';
    }

    // clone

    @Override
    public Object clone() {
        Object object = null;
        try {
            object = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return object;
    }
}
