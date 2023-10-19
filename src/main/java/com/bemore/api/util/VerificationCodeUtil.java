package com.bemore.api.util;

import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class VerificationCodeUtil {

    private static final String HOST = "https://cxkjsms.market.alicloudapi.com";
    private static final String PATH = "/chuangxinsms/dxjk";
    private static final String METHOD  = "POST";
    private static final String APPCODE = "7a024e57b2404c2ab82f6114b39d87bb";

    public static void sendMessage(String code,String phone) {
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + APPCODE);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("content", "您好，您的服务验证码是：" + code + "【长三角金融产业园】");
        querys.put("mobile", phone);
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(HOST, PATH, METHOD, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
