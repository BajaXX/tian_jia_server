package com.bemore.api.exception;

import com.bemore.api.enums.ResultCodeEnum;
import com.google.gson.Gson;

/**
 * @author : jackie.yao
 * @date: 2021/3/4 11:01 PM
 */
public class GlobalException extends RuntimeException {

    private ResultCodeEnum message;
    private Gson fullMessage;

    public GlobalException(ResultCodeEnum message) {
        super(message.name());
        this.message = message;
    }
    public GlobalException(ResultCodeEnum message, Gson fullMessage) {
        super(message.name());
        this.message = message;
        this.fullMessage=fullMessage;
    }

    public GlobalException(ResultCodeEnum message, String detail) {
        super(message.name() + ":" + detail);
    }


    public ResultCodeEnum getMessageInfo() {
        return message;
    }
    public Gson getFullMessage() {
        return fullMessage;
    }
}