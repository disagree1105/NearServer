package com.yukiww233.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yukiww233.bean.resultBean.BaseModel;
import com.yukiww233.mapper.TokenMapper;
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


}
