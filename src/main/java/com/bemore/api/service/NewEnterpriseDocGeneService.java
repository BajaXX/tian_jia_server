package com.bemore.api.service;

import javax.servlet.http.HttpServletResponse;

public interface NewEnterpriseDocGeneService {

    /**
     * 生成个人独资企业设立(变更)登记审核表
     * @param enterpriseId
     * @param fileName
     * @param response
     */
    void generate1401Doc(String enterpriseId, String fileName, HttpServletResponse response);

    void generate1409Doc(String enterpriseId,String fileName,HttpServletResponse response);

    void generate1501Doc(String enterpriseId, String fileName, HttpServletResponse response);

    void generate1010Doc(String enterpriseId, String fileName, HttpServletResponse response);
}
