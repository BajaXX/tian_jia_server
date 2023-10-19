package com.bemore.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

/**
 * @author 余江俊
 */
public class BaseController {

    private int codeSuccess=200;
    private int codeError=500;
    private String msgSuccess="success";
    private String msgError="fail";

    public <T> HashMap success(T t){
        HashMap<String,Object> result=new HashMap<>();

        result.put("code",codeSuccess);
        result.put("msg",msgSuccess);
        result.put("data",t);
        return result;
    }

    public <T> HashMap success(T t,int code,String msg){
        HashMap<String,Object> result=new HashMap<>();

        result.put("code",code);
        result.put("msg",msg);
        result.put("data",result);
        return result;
    }

    public <T> HashMap error(){
        HashMap<String,Object> result=new HashMap<>();

        result.put("code",codeError);
        result.put("msg",msgError);
        return result;
    }

    public <T> HashMap error(int code,String msg){
        HashMap<String,Object> result=new HashMap<>();

        result.put("code",code);
        result.put("msg",msg);
        return result;
    }
}
