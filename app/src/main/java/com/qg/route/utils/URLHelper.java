package com.qg.route.utils;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricco on 2017/5/4.
 */

public class URLHelper {
    public static Map<String, String> sendLogin(String userId, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("userid", userId);
        map.put("password", password);
        return map;
    }
    public static Map<String, String> sendRoute(String routeJson) {
        Map<String, String> map = new HashMap<>();
        map.put("route", routeJson);
        return map;
    }
    public static String getPic(int userId) {
        return Constant.PICTURE + userId + ".jpg";
    }

    public static String getPic(String userId) {
        return Constant.PICTURE + userId + ".jpg";
    }

    public static String getBusinessPic(int businessId) {
        return Constant.BUSINESS_PICTURE + businessId + ".jpg";
    }
}
