package com.bemore.api.handler;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bemore.api.exception.WebException;
import com.bemore.api.util.GsonUtil;

@RestControllerAdvice
public class BadRequestExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
    public String validationBodyException(MethodArgumentNotValidException exception){
        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            FieldError fieldError = (FieldError)errors.get(0);            
            return GsonUtil.build(1,fieldError.getDefaultMessage(),null);
        }
        return GsonUtil.build();
    }	
	
	@ExceptionHandler(WebException.class)
    public String errorHandler(WebException ex) {
    	return GsonUtil.build(ex.getCode(),ex.getMsg(),null);
    }
	
//    @ExceptionHandler(Exception.class)
//    public String errorHandler(Exception ex) {
//    	return GsonUtil.build(100,"系统异常",null);
//    }
    
}
