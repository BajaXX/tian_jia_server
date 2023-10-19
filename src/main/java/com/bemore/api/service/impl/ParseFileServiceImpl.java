package com.bemore.api.service.impl;

import com.bemore.api.config.OCRConfig;
import com.bemore.api.constant.OCRConstants;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.Person;
import com.bemore.api.service.ParseFileService;
import com.bemore.api.util.OCRParseUtil;
import com.bemore.api.util.StrUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.BizLicenseOCRResponse;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件解析
 */
@Service
@Slf4j
public class ParseFileServiceImpl implements ParseFileService {

    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";
    @Autowired
    OCRConfig ocrConfig;
    @Override
    public Person idCardFront(String filePath, Person person) {
        IDCardOCRResponse resp = OCRParseUtil.parseIdCard(ocrConfig, filePath, OCRConstants.ID_CARD.FRONT.getCode());
        person.setAddress(resp.getAddress());
        person.setBirthday(resp.getBirth());
        person.setIdcard(resp.getIdNum());
        person.setName(resp.getName());
        person.setNation(resp.getNation());
        person.setSex(resp.getSex());
        person.setFront(filePath);
        if(!StringUtils.isEmpty(resp.getBirth())){
            String[] date = resp.getBirth().split("/");
            if (date!=null&&date.length==3){
                if(date[1].length()==1){
                    date[1] = "0"+date[1];
                }
                if(date[2].length()==1){
                    date[2] = "0"+date[2];
                }
                person.setBirthday(date[0]+"-"+date[1]+"-"+date[2]);
            }
        }
        return person;
    }

    @Override
    public Person idCardBack(String filePath, Person person) {
        IDCardOCRResponse resp = OCRParseUtil.parseIdCard(ocrConfig, filePath, OCRConstants.ID_CARD.BACK.getCode());
        person=person==null?new Person():person;
        person.setAuthority(resp.getAuthority());
        person.setBack(filePath);
        if(StringUtils.hasLength(resp.getValidDate())){
            String[] date = resp.getValidDate().split("-");
            if (date.length >= 2){
                person.setStartDate(date[0].replaceAll("\\.", "-"));
                person.setEndDate(date[1].replaceAll("\\.", "-"));
            }
        }
        return person;
    }

    @Override
//    public Enterprise bizLicense(MultipartFile srcFile) {
    public Enterprise bizLicense(String filePath) {
//        BizLicenseOCRResponse resp = null;
//        try {
//            resp = OCRParseUtil.parseBizLicense(ocrConfig, srcFile.getInputStream());
//        } catch (IOException e) {
//            log.error("获取文件流失败！", e);
//        } finally {
//        }
        try{

        BizLicenseOCRResponse resp = OCRParseUtil.parseBizLicense(ocrConfig, filePath);
        Enterprise enterprise = new Enterprise();
        enterprise.setName(resp.getName());
        enterprise.setType(resp.getType());
        enterprise.setBusiness(resp.getBusiness());
        enterprise.setRegisterAddress(resp.getAddress());
        enterprise.setRegisterNum(resp.getRegNum());
        enterprise.setCapital(StrUtils.blackIfNull(resp.getCapital()).replaceAll(REGEX_CHINESE, ""));
        if (resp.getPeriod()!=null){
            String[] dates = resp.getPeriod().split("至");
            if (dates !=null && dates.length==2){
                enterprise.setStartDate(dates[0].replaceAll(REGEX_CHINESE, "-"));
                enterprise.setEndDate(dates[1].replaceAll(REGEX_CHINESE, "-"));
            }
        }

//        enterprise.setPaperName(srcFile.getOriginalFilename());
        enterprise.setPaperName(filePath);
        return enterprise;
        }catch (Exception e){
            return null;
        }
    }


}
