package com.bemore.api.util;

public class StrUtils {

    /**
     * null 返回“”空字符串
     * @param str
     * @return
     */
    public static String blackIfNull(String str){
        return str==null?"":str;
    }
}
