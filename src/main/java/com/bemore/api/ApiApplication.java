package com.bemore.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
@MapperScan("com.bemore.api.dao.mapper")
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
	
	@Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

//	@Configuration
//	public class CrossConfig implements WebMvcConfigurer {
//		@Override
//		public void addCorsMappings(CorsRegistry registry) {
//			registry.addMapping("/**")
//					.allowedOrigins("*")
//					.allowedMethods("GET","HEAD","POST","PUT","DELETE","OPTIONS")
//					.allowCredentials(true)
//					.maxAge(604800)
//					.allowedHeaders("*");
//		}
//	}
}
