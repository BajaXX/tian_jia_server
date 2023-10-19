package com.bemore.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ocr.config")
@Data
public class OCRConfig {

    private String secretId;
    private String secretKey;
    private String endPoint;
    private String region;
}
