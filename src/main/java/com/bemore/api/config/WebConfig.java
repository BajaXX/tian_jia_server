package com.bemore.api.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bemore.api.interceptor.JWTInterceptor;


@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Bean
    JWTInterceptor jwtInterceptor(){
        return new JWTInterceptor();
    }
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor()).addPathPatterns("/wx/**")
        .excludePathPatterns(Arrays.asList("/wx/**"));
    }

}
