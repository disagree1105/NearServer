package com.yukiww233.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yukiww233.bean.resultBean.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by disagree on 2017/3/20.
 */
public class Utils {
    @Autowired
    static BaseModel baseModel;
    private static double EARTH_RADIUS = 6378.137;

    public static boolean checkAllNumbers(String str) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence)str);
        return matcher.matches();
    }

    public static String getResult(int ret_code1, int ret_code2, String message, Map<String, Object> _ret) {
        baseModel = new BaseModel(ret_code1, ret_code2, message, _ret);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(baseModel);
    }

    public static String getResult2(int ret_code1, int ret_code2, String message, Object _ret) {
        baseModel = new BaseModel(ret_code1, ret_code2, message, _ret);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(baseModel);
    }

    public static double getDistanceOfMeter(double lat1, double lng1,
                                            double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

}
