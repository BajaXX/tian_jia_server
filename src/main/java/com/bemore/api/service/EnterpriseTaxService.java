package com.bemore.api.service;

import com.bemore.api.entity.EnterpriseTaxPlus;
import com.bemore.api.entity.request.EnterpriseTaxParam;
import com.bemore.api.entity.request.TaxExportRequest;
import com.bemore.api.entity.request.TaxRequest;
import com.bemore.api.entity.response.EnterpriseTaxExcelView;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface EnterpriseTaxService {

    void cleanIndustryService();

    EnterpriseTaxExcelView findEnterpriseTaxByExcel(TaxRequest request);

    void cleanTable();

    void exportEnterpriseTax(TaxExportRequest request, HttpServletResponse resp);

    List<EnterpriseTaxPlus> getEnterpriseTaxByPage(TaxRequest request);

    List<EnterpriseTaxParam> preview(MultipartFile[] files);

    String findNewestRollbackLog();

    void cleanDataByMonthService(String date);

    void importEnterpriseTaxService(MultipartFile files,String date);

    void batchImportEnterpriseTaxService(MultipartFile[] files);

    String findNewestTaxMonth();
}
