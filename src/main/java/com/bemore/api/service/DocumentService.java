package com.bemore.api.service;

import com.bemore.api.dto.resp.DocResp;
import com.bemore.api.entity.request.DocumentRequest;
import com.bemore.api.exception.WebException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DocumentService {
    void downLoadDocumentById(HttpServletResponse response, DocumentRequest documentRequest);

    /**
     * 根据企业ID查询到当前企业下的doc列表
     * @param companyId 企业ID
     * @return doc列表
     */
    List<DocResp> getCompanyListByCompanyId(String companyId) throws Throwable;

    /**
     * 下载企业所需文档
     * @param filePath 文件全路径
     * @param enterpriseId 企业ID
     * @param response 响应流
     */
    void downLoadDoc(String filePath, String enterpriseId, HttpServletResponse response) throws WebException;

    void downloadSupport(String filePath, String enterpriseName, int downDate,HttpServletResponse response) throws WebException;
}
