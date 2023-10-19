package com.bemore.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sms.config")
@Data
public class SMSConfig {
    private String appKey;
    private String appSecret;
    private String appCode;
}
