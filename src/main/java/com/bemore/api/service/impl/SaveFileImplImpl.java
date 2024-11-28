package com.bemore.api.service.impl;

import com.bemore.api.config.FileUpSaveConfig;
import com.bemore.api.service.SaveFileImpl;
import com.bemore.api.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class SaveFileImplImpl implements SaveFileImpl {

    @Autowired
    FileUpSaveConfig fileUpSaveConfig;
    @Override
    public String saveIdCard(String path, MultipartFile srcFile) throws IOException {

//        String name = srcFile.getOriginalFilename().substring(0, srcFile.getOriginalFilename().lastIndexOf("."));
        String suffix = srcFile.getOriginalFilename().substring(srcFile.getOriginalFilename().lastIndexOf(".") + 1);
        String fileName = System.currentTimeMillis()+"."+suffix;
        String filePath = path +fileName ;
        File file = new File(filePath);
        FileUtil.createFile(filePath);
        srcFile.transferTo(file);
        return fileUpSaveConfig.getIdCardUrl()+fileName;
    }

    @Override
    public String saveIdCardReverse(String path, MultipartFile srcFile) throws IOException{
        String suffix = srcFile.getOriginalFilename().substring(srcFile.getOriginalFilename().lastIndexOf(".") + 1);
        String fileName = System.currentTimeMillis()+"."+suffix;
        String filePath = path +fileName ;
        File file = new File(filePath);
        FileUtil.createFile(filePath);
        srcFile.transferTo(file);
        return fileUpSaveConfig.getIdCardUrl()+fileName;
    }

    @Override
    public String saveBusinessLicense(String path, MultipartFile srcFile) throws IOException{
        String suffix = srcFile.getOriginalFilename().substring(srcFile.getOriginalFilename().lastIndexOf(".") + 1);
        String fileName = System.currentTimeMillis()+"."+suffix;
        String filePath = path +fileName ;
        File file = new File(filePath);
        FileUtil.createFile(filePath);
        srcFile.transferTo(file);
        return fileUpSaveConfig.getBizFileUrl()+fileName;
    }

    @Override
    public String saveCompanyInfoPdf(String path, MultipartFile srcFile) throws IOException{
        String name = System.currentTimeMillis() + "";
        String fileName = path + name +".pdf";
        File file = new File(fileName);
        FileUtil.createFile(fileName);
        srcFile.transferTo(file);
        return fileName;
    }
    @Override
    public String saveSupportFilesDir( MultipartFile srcFile) throws IOException{
        String suffix = srcFile.getOriginalFilename().substring(srcFile.getOriginalFilename().lastIndexOf(".") + 1);
        String fileName = System.currentTimeMillis()+"."+suffix;
        String filePath = fileUpSaveConfig.getSupportFilesDir() +fileName ;
        File file = new File(filePath);
        FileUtil.createFile(filePath);
        srcFile.transferTo(file);
        return srcFile.getOriginalFilename()+","+fileName;
    }
}
