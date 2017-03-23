package com.yukiww233.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by disagree on 2017/3/20.
 */
public class Utils {
    public static boolean checkAllNumbers(String str) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence)str);
        return matcher.matches();
    }
}
