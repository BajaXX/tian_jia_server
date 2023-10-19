package com.bemore.api.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

public class CpSignUtil {

    private static String[] keyPair = new String[]{"PUubcmUDP5oiufUH", "UXXIo8Day0L0XysFaxJcaNRij0lhVw"};

    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    public static JSONObject openApi(String url, JSONArray body) {
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String sign = DigestUtils.md5Hex(keyPair[0] + keyPair[1] + timestamp);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accessKeyId", keyPair[0]);
        headers.set("sign", sign);
        headers.set("region", "cn-north-2");
        headers.set("timestamp", timestamp);
        HttpEntity<JSONArray> httpEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.postForObject(url, httpEntity, JSONObject.class);
    }

    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    public static <T> T openApiObject(String url, JSONObject body, Class<T> clazz) {
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String sign = DigestUtils.md5Hex(keyPair[0] + keyPair[1] + timestamp);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accessKeyId", keyPair[0]);
        headers.set("sign", sign);
        headers.set("region", "cn-north-2");
        headers.set("timestamp", timestamp);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.postForObject(url, httpEntity, clazz);
    }
}
