package com.bemore.api.service;

import com.bemore.api.entity.EnterpriseTexTable;
import com.bemore.api.entity.request.*;
import com.bemore.api.entity.response.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ReportService {

    void regionIncomeStatExportByExcel(TaxRequest request, HttpServletResponse resp);

    RegionIncomeExcelView findRegionIncomeTaxByExcel(TaxRequest request);

    void enterpriseSupportDetailExport(ReportBaseRequest request, HttpServletResponse response);

    List<EnterpriseSupportDetailView> findEnterpriseSupportDetail(ReportBaseRequest request);

    void taxMarginTableExport(ReportBaseRequest request, HttpServletResponse response);

    List<TaxMarginTableView> findTaxMarginTable(ReportBaseRequest request);

    void newAddedEnterpriseTaxExport(ReportBaseRequest request, HttpServletResponse response);

    void newEnterpriseTaxExport(NewEnterpriseTaxParam request, HttpServletResponse response);

    void districtInputSituationExport(ReportBaseRequest request, HttpServletResponse response);

    void monthlyMainIndicatorExport(ReportBaseRequest request, HttpServletResponse response);

    void taxRankingExport(ReportBaseRequest request, HttpServletResponse response);

    void taxPayerComparisonExport(ReportBaseRequest request, HttpServletResponse resp);

    void regionIncomeStatExport(ReportBaseRequest request,TaxRequest excelRequest, HttpServletResponse resp);

    List<NewAddedEnterpriseTaxView> findNewAddedEnterpriseTax(ReportBaseRequest request);

    List<NewEnterpriseTaxView> findNewEnterpriseTax(NewEnterpriseTaxParam param);

    List<EnterpriseTexTable> findDistrictInputSituation(ReportBaseRequest request);

    List<TaxPayerComparisonView> findTaxPayerComparison(ReportBaseRequest request);

    List<MonthlyMainIndicatorView> findMonthlyMainIndicator(ReportBaseRequest request);

    List<TaxRankingView> findTaxRankingList(ReportBaseRequest request);

    List<RegionIncomeView> findRegionIncomeList(ReportBaseRequest request);

    void exportTaxMarginRanking(ReportBaseRequest request, HttpServletResponse response);

    void exportTaxMarginComparison(ReportBaseRequest request, HttpServletResponse response);

    void exportTaxDetailComparison(ReportBaseRequest request, HttpServletResponse response);

    List<TaxMarginRankingView> taxMarginRanking(ReportBaseRequest request);

    List<TaxMarginComparisonView> taxMarginComparison(ReportBaseRequest request);

    List<TaxDetailComparisonView> taxDetailComparison(ReportBaseRequest request);

    /**
     * @Description 查询企业综合报表的字段
     * @Title getComprehensiveReportColumns
     * @Param []
     * @Return com.bemore.api.entity.response.EnterpriseComprehensiveReportColumnView
     * @Author Louis
     * @Date 2022/04/21 17:59
     */
    EnterpriseComprehensiveReportColumnView getComprehensiveReportColumns();

    /**
     * @Description 通过excel查询企业综合报表
     * @Title findComprehensiveReportByExcel
     * @Param [file]
     * @Return com.bemore.api.entity.response.EnterpriseComprehensiveExcelView
     * @Author Louis
     * @Date 2022/04/21 15:58
     */
    EnterpriseComprehensiveExcelView findComprehensiveReportByExcel(MultipartFile file);

    /**
     * @Description 导出企业综合报表
     * @Title exportComprehensiveReport
     * @Param [param, resp]
     * @Return void
     * @Author Louis
     * @Date 2022/04/21 15:58
     */
    void exportComprehensiveReport(EnterpriseComprehensiveReportParam param, HttpServletResponse resp);


}
