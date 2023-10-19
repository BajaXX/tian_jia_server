package com.bemore.api.common;

import com.bemore.api.enums.ResultCodeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;


/**
 * @author : jackie.yao
 * @date: 2021/3/4 10:57 PM
 * @param <T>
 */
@Data
@Slf4j
public class BaseResponseData<T> implements Serializable {

    private static final String SUCCESS_CODE = ResultCodeEnum.SUCCESS.getCode()+"";
    private static final String ERROR_CODE = ResultCodeEnum.SYSTEM_ERROR.getCode()+"";

    protected String resultCode;

    /**
     * 枚举类型的toString值
     */
    protected String msg;

    /**
     * 枚举类型描述
     */
    protected String description;


    /**
     * 返回主数据
     */
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rspTime = new Date();

    private BaseResponseData() {
        setSuccess();
    }

    private BaseResponseData(T data) {
        setSuccess();
        this.data = data;
    }

    private BaseResponseData(String resultCode, String msg) {
        this.resultCode = resultCode;
        this.msg = msg;
    }


    private void setSuccess() {
        resultCode = SUCCESS_CODE;
        msg = "SUCCESS";
    }

    private BaseResponseData(String resultCode, String msg,String description) {
        this.description = description;
        this.resultCode = resultCode;
        this.msg = msg;
    }


    /**
     * 只返回成功状态，不包含主数据
     * @param <T>
     * @return
     */
    public static <T> BaseResponseData<T> success() {
        return new BaseResponseData<T>();
    }

    /**
     * 只返回成功状态，并包含相应数据
     * @param <T>
     * @return
     */
    public static <T> BaseResponseData<T> success(T data) {
        return new BaseResponseData<T>(data);
    }

    /**
     * 请求业务异常，返回对应状态码数据
     * @param codeEnum
     * @param <T>
     * @return
     */
    public static <T> BaseResponseData<T> fail(ResultCodeEnum codeEnum ) {
        return new BaseResponseData<T>(codeEnum.getCode().toString(), codeEnum.toString(),codeEnum.getDesc());
    }


    /**
     * 自定义，返回异常信息业务
     * @param resultCode
     * @param status
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> BaseResponseData<T> fail(String resultCode, String status, String msg) {
        return new BaseResponseData<T>(resultCode, status, msg);
    }

    public static <T> BaseResponseData<T> fail(String resultCode, String msg) {
        return new BaseResponseData<T>(resultCode,msg);
    }
    /**
     * 返回相关报错信息
     * @param e
     * @param <T>
     * @return
     */
    public static <T> BaseResponseData<T> error(Exception e) {
        BaseResponseData baseResponseData = new BaseResponseData();
        String errEnum = "";
        if (e.getMessage() != null) {
            errEnum = e.getMessage().split(":")[0];
        }
        ResultCodeEnum codeEnum = ResultCodeEnum.getInstanceByDesc(errEnum);
        if (codeEnum != null) {
            Integer resultCode = codeEnum.getCode();
            String msg = e.getMessage();
            baseResponseData.setResultCode(resultCode.toString());
            baseResponseData.setMsg(msg);
            log.error("[" + resultCode + "]" + msg, e.getMessage());
        } else {
            baseResponseData.setResultCode(ERROR_CODE);
            baseResponseData.setMsg(ResultCodeEnum.SYSTEM_ERROR.getDesc());
            log.error("[system error]:" + e.getMessage());

        }

        return baseResponseData;
    }

    public Boolean isSuccess() {
        if (SUCCESS_CODE.equals(resultCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
