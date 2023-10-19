package com.bemore.api.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

public class RestTempComponent {

    public static RestTemplate restTemplate=null;

    static {
        restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public static String sendGetRequest(String url) {
        url = url.replaceAll(" ", "%20");
        return restTemplate.getForObject(url,String.class);
    }
    
    public static String sendPostRequest(String url,String entityString){
        url = url.replaceAll(" ", "%20");        
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
        headers.setContentType(type);        
        HttpEntity<String> entity = new HttpEntity<String>(entityString,headers);        
        return restTemplate.postForObject(url,entity,String.class);
    }
    
    public static String sendPostRequestJson(String url,String entityString){
        url = url.replaceAll(" ", "%20");        
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);        
        HttpEntity<String> entity = new HttpEntity<String>(entityString,headers);        
        return restTemplate.postForObject(url,entity,String.class);
    }

}
