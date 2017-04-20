package com.qg.route;


public enum StatEnum {


    DEFAULT_WRONG(-1,"其他错误"),
    /**
     * 登录板块
     */
    LOGIN_SUCCESS(121,"登录成功"),
    LOGIN_NOT_EXIT_USER(122,"不存在的用户"),
    LOGIN_USER_MISMATCH(123,"用户名或密码错误"),
    /**
     * 忘记密码板块
     */
    PASSWORD_CHANGE_SUCCESS(131,"修改密码成功"),
    PASSWORD_EMPTY_USER(132,"空用户对象"),
    PASSWORD_FAMMTER_FAULT(134,"修改密码格式错误"),
    /**
     * 修改用户信息板块
     */
    INFORMATION_CHANGE_SUCCESS(141,"修改信息成功"),
    INFORMATION_EMPTY_USER(142,"空用户对象"),
    INFORMATION_FORMMATTER_FAULT(143,"修改信息格式错误"),
    /**
     * 系统通知信息
     */
    INFO_LIST(151, "获得用户系统通知列表"),
    INFO_INSERT_SUCCESS(152, "插入系统通知成功"),
    INFO_INSERT_FAULT(153, "插入系统通知失败"),
    INFO_DELETE_SUCCESS(154,"删除系统通知成功"),
    INFO_DELETE_FAULT(155,"删除系统通知失败"),
    INFO_UPDATE_SUCCESS(156,"更新系统通知成功"),
    INFO_UPDATE_FAULT(157, "更新系统通知失败"),
    /**
     * 好友关系列表
     */
    RELATION_LIST(161,"获取好友关系列表"),
    RELATION_ADD_SUCCESS(162, "添加好友成功"),
    RELATION_ADD_FAULT(163, "添加好友失败"),
    RELATION_DELETE_SUCCESS(164, "删除好友成功"),
    RELATION_DELETE_FAULT(165, "删除好友失败"),
    RELATION_IS_EXIT(166, "已经存在了好友关系"),
    RELATION_ISNOT_EXIT(167, "不存在好友关系"),
    /**
     * 消息记录
     */
    CHAT_LOG_List(171, "获得未读消息记录"),
    /**
     * 商家信息板块
     */
    BUSINESS_LIST(601, "获取商家列表"),
    GOODS_LIST(602, "获取商品列表"),
    ORDER_LIST(603, "获取订单列表"),
    ALL(999,"test");

    private  int state;
    private  String stateInfo;

    StatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }
    public  static  StatEnum statOf(int index) {
        for (StatEnum state : values()) {
            if (state.getState() == index) {
                return  state;
            }
        }
        return  null;
    }
}
