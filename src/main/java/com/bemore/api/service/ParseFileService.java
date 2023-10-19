package com.bemore.api.service;

import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.Person;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件解析
 */
public interface ParseFileService {

    Person idCardFront(String filePath,Person person);

    Person idCardBack(String filePath,Person person);

//    Enterprise bizLicense(MultipartFile srcFile);
    Enterprise bizLicense(String filePath);



}
