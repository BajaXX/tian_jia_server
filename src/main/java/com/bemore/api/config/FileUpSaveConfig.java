package com.bemore.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file.save")
public class FileUpSaveConfig {

    private String pdfFileDir;
    private String idCardFileDir;
    private String idCardUrl;
    private String bizFileDir;
    private String bizFileUrl;
    private String docFileDir;
}
