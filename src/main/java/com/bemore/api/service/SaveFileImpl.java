package com.bemore.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SaveFileImpl {

    /**
     * 保存身份证正面
     * @param path
     * @param srcFile
     * @return
     */
    String saveIdCard(String path, MultipartFile srcFile) throws IOException;

    /**
     * 保存身份证反面
     * @param path
     * @param srcFile
     * @return
     */
    String saveIdCardReverse(String path,MultipartFile srcFile) throws IOException;

    /**
     * 保存营业执照
     * @param path
     * @param srcFile
     * @return
     */
    String saveBusinessLicense(String path,MultipartFile srcFile) throws IOException;

    /**
     * 保存企业信息pdf
     * @param path
     * @param srcFile
     * @return
     */
    String saveCompanyInfoPdf(String path,MultipartFile srcFile) throws IOException;
    String saveSupportFilesDir(MultipartFile srcFile) throws IOException;
}
