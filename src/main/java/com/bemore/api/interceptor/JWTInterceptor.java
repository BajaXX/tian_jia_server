package com.bemore.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bemore.api.exception.WebException;
import com.bemore.api.util.Constant;

@Component
public class JWTInterceptor implements HandlerInterceptor {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws WebException {
        final String authHeader = request.getHeader("authorization");
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        } else {
            if (null == authHeader || !authHeader.startsWith("bearer")) {
               throw new WebException(Constant.SERVER_ERROR_CODE, "没有访问权限");
            }
        }
        final String token = authHeader.substring(7);           
        request.setAttribute("userId", token);
        throw new WebException(Constant.SERVER_ERROR_CODE, "没有访问权限");
	}
	
}
