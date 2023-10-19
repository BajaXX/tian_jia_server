package com.bemore.api.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.sun.jmx.snmp.Timestamp;

/**
 * Created by Ximi on 2018/8/28 0028 16:19.
 */
public class TokenUtils {

    public static final String CITROEN_ACCESS_TOKEN_KEY = "access_token_cache_wxa0d59d5a3705a608";
    public static final String PEUGEOT_ACCESS_TOKEN_KEY = "access_token_cache_peugeot";

    /**
     * 获取东雪的token
     * @return
     */
//    public static JsonObject getCitroenToken() {
//        //获取微信矩阵信息
//        String uri = "http://oauth.df-citroenclub.com.cn/wx/getToken";
//        //准备接口参数信息
//        long time = System.currentTimeMillis();
//        String timestampString = "20210625153000";
//        JsonObject json = new JsonObject();
//        json.addProperty("appid", "wxa0d59d5a3705a608");
//        json.addProperty("timestamp", time);
//        json.addProperty("sign", md5("wxa0d59d5a3705a608"+time,"39b46f4b341b85663e3dcfd8bf32bc02"));
//        try {
//            String jsonStr = RestTempComponent.sendPostRequest(uri,json.toString());
//            json = new JsonParser().parse(jsonStr).getAsJsonObject();
//            return json;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new JsonObject();
//    }    

    /**
     * 获取东标的token
     * @return
     */
    public static JsonObject getPeugetoToken() {
        String uri = "http://wechat.mypeugeot.com.cn/wxapi/Home/index/get_access_token";
        //准备接口参数信息
        JsonObject json = new JsonObject();
        try {
            String jsonStr = RestTempComponent.sendPostRequest(uri, json.toString());
            json = new JsonParser().parse(jsonStr).getAsJsonObject();
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }
    
    public static String md5(String text, String key) {
    	//加密后的字符串
    	String encodeStr= DigestUtils.md5Hex(text + key);
//    	System.out.println("MD5加密后的字符串为:encodeStr="+encodeStr);
    	return encodeStr;
    }

    public static void main(String[] args) {
        System.out.println(getPeugetoToken());
//        System.out.println(getPeugetoToken().get("access_token").getAsString());
//    	System.out.println(getCitroenToken());
    }
    
    
}
