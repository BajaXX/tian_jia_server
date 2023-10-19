package com.bemore.api.exception;

import com.bemore.api.common.BaseResponseData;
import com.bemore.api.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yaobo
 * @version 1.0.0
 * @date 2021/3/4 10:57 PM
 * @description
 */
@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler {


    @ExceptionHandler(value = GlobalException.class)
    @ResponseBody
    public BaseResponseData defaultHandler01(Exception e){
        ResultCodeEnum codeEnum = ResultCodeEnum.getInstanceByDesc(e.getMessage());
        if (codeEnum != null) {
            return BaseResponseData.fail(codeEnum);
        }else {
            return BaseResponseData.fail(ResultCodeEnum.SYSTEM_ERROR.getCode().toString(),e.getMessage());
        }
    }
}
