package com.qg.route.utils;

/**
 * Created by Ricco on 2017/4/13.
 */

public class Constant {
    public static String USER_ID = "22222";
    public static String PASSWORD = "123456";
    // base url
    // 远程URL
    public static final String BASE_URL = "http://118.89.54.17:8080/onway";
    // 本地URL
//    public static final String BASE_URL = "http://4ze9d6.natappfree.cc:80/onway";

    // Default Location:广工图书馆
    public static final String GDUT_LONGITUDE = "113.395639";
    public static final String GDUT_LATITUDE = "23.037608";

    // current city
    public static String CURRENT_CITY = "广州";

    // route type
    public static final int ROUTE_TYPE_WALK = 1;
    public static final int ROUTE_TYPE_RIDE = 2;
    public static final int ROUTE_TYPE_BUS = 3;
    public static final int ROUTE_TYPE_DRIVE= 4;

    /**
     * URL
     */
    // business
    public static final class BusinessUrl {
        // 商家列表
        public static final String BUSINESS_LIST = BASE_URL + "/busilists";
        // 商家订单
        public static final String BUSINESS_ORDER = BASE_URL + "/orderlists/";
        // 商家商品列表
        public static final String BUSINESS_GOODS = BASE_URL + "/goodslists/";
    }

    // user
    public static final class UserUrl {
        // login
        public static final String LOGIN =  BASE_URL + "/user/login";
    }

    // route
    public static final class RouteUrl {
        public static final String ROUTE_LIST = BASE_URL + "/route/list";
        public static final String ROUTE_SAVE = BASE_URL + "/route/save";
    }

    // group
    public static final class GroupUrl {
        public static final String GROUP_GET = BASE_URL + "/route/path/"; // 获得路线所对应的圈子
        public static final String GROUP_GET_LIST = BASE_URL + "/chat/list/";
        public static final String GROUP_ADD = BASE_URL + "/chat/add/";
        public static final String GROUP_CREATE = BASE_URL + "/chat/create/";
        public static final String GROUP_ALL = BASE_URL + "/allroom";
        public static final String GROUP_INFOMATION = BASE_URL + "/route/";
    }

    // chat
    public static final class ChatUrl {
        public static final String CHAT_HEAD_IMAGE_GET = BASE_URL + "/picture/";

        //public static final String WEB_SOCKET = "ws://4ze9d6.natappfree.cc:80/onway/websocket";
        public static final String WEB_SOCKET = "ws://118.89.54.17:8080/onway/websocket";

        public static final String FRIEND_LIST_GET = BASE_URL + "/relation/list";
        public static final String ROOM_LIST_GET = BASE_URL + "/allroom";
        public static final String OFFLINE_MESSAGE_URL = BASE_URL + "/chat/list";
    }

    // 动态
    public static final class MomentsUrl{
        public static final String PERSON_DATA_GET = BASE_URL + "/user/get/";
        public static final String PERSON_HEAD_IMAGE = BASE_URL + "/picture/";
        public static final String UPDATE_IMAGE = BASE_URL + "/user/send/picture";
        public static final String DELETE_FRIEND = BASE_URL + "/user/relation/delete/";
        public static final String LOAD_MOMENT_IMAGE = BASE_URL + "/photo/";
        public static final String DELETE_MOMENT = BASE_URL + "/trends/delete/";
        public static final String MOMENT_GET  = BASE_URL + "/trends/personal/";
        public static final String MOMENT_PUBLISH = BASE_URL + "/trends/send";
        public static final String UPDATE_DATA = BASE_URL + "/user/info/update";
    }

    //information
    public static final class InformationUrl{
        public static final String INFORMATION_GET = BASE_URL + "/info/list/";
        public static final String HANDLE_INFORMATION = BASE_URL + "/info/handle/";
    }

    // 获得头像
    public static final String RECOMMEND_FRIEND = BASE_URL + "/recommend";

    // 头像
    public static final String PICTURE = BASE_URL + "/picture/";

    // 商家
    public static final String BUSINESS_PICTURE = BASE_URL + "/image/";
}
