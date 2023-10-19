package com.bemore.api.controller;

import com.bemore.api.common.BaseResponseData;
import com.bemore.api.entity.request.*;
import com.bemore.api.entity.response.*;
import com.bemore.api.service.ReportService;
import com.bemore.api.util.GsonUtil;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/regionIncomeStatExportByExcel")
    @ApiOperation(value = "通过excel导出年度区级收入统计表")
    public void regionIncomeStatExportByExcel(TaxRequest request, HttpServletResponse resp) {
        reportService.regionIncomeStatExportByExcel(request, resp);
    }

    @PostMapping("/getRegionIncomeTaxByExcel")
    @ApiOperation(value = "通过excel查询年度区级收入统计表")
    public String getRegionIncomeTaxByExcel(TaxRequest request) {
        return GsonUtil.build(reportService.findRegionIncomeTaxByExcel(request));
    }

    @GetMapping("/exportTaxDetailComparison")
    @ApiOperation(value = "导出企业税收同比明细表")
    public void exportTaxDetailComparison(ReportBaseRequest request, HttpServletResponse response) {
        reportService.exportTaxDetailComparison(request,response);
    }

    @GetMapping("/taxDetailComparison")
    @ApiOperation(value = "企业税收同比明细表")
    public BaseResponseData<List<TaxDetailComparisonView>> taxDetailComparison(ReportBaseRequest request) {
        return BaseResponseData.success(reportService.taxDetailComparison(request));
    }

    @GetMapping("/exportTaxMarginComparison")
    @ApiOperation(value = "导出三年同比振幅表 ")
    public void exportTaxMarginComparison(ReportBaseRequest request, HttpServletResponse response) {
        reportService.exportTaxMarginComparison(request,response);
    }

    @GetMapping("/taxMarginComparison")
    @ApiOperation(value = "三年同比振幅表 ")
    public BaseResponseData<List<TaxMarginComparisonView>> taxMarginComparison(ReportBaseRequest request) {
        return BaseResponseData.success(reportService.taxMarginComparison(request));
    }

    @GetMapping("/exportTaxMarginRanking")
    @ApiOperation(value = "导出税收增幅减幅排名表")
    public void exportTaxMarginRanking(ReportBaseRequest request, HttpServletResponse response) {
        reportService.exportTaxMarginRanking(request,response);
    }

    @GetMapping("/taxMarginRanking")
    @ApiOperation(value = "税收增幅减幅排名表")
    public BaseResponseData<List<TaxMarginRankingView>> taxMarginRanking(ReportBaseRequest request) {
        return BaseResponseData.success(reportService.taxMarginRanking(request));
    }

    @GetMapping("/taxMarginTableExport")
    @ApiOperation(value = "导出园区企业总体税收增减幅度对比表")
    public void taxMarginTableExport(ReportBaseRequest request, HttpServletResponse resp) {
        reportService.taxMarginTableExport(request, resp);
    }

    @GetMapping("/taxMarginTable")
    @ApiOperation(value = "园区企业总体税收增减幅度对比表")
    public BaseResponseData<List<TaxMarginTableView>> getTaxMarginTable(ReportBaseRequest request) {
        return BaseResponseData.success(reportService.findTaxMarginTable(request));
    }

    @GetMapping("/enterpriseSupportDetailExport")
    @ApiOperation(value = "导出企业扶持明细表")
    public void enterpriseSupportDetailExport(ReportBaseRequest request, HttpServletResponse resp) {
        reportService.enterpriseSupportDetailExport(request,resp);
    }

    @GetMapping("/enterpriseSupportDetail")
    @ApiOperation(value = "企业扶持明细表")
    public String getEnterpriseSupportDetail(ReportBaseRequest request) {
        return GsonUtil.build(reportService.findEnterpriseSupportDetail(request));
    }

    @GetMapping("/newAddedEnterpriseTaxExport")
    @ApiOperation(value = "导出新增纳税企业明细表")
    public void newAddedEnterpriseTaxExport(ReportBaseRequest request, HttpServletResponse resp) {
        reportService.newAddedEnterpriseTaxExport(request,resp);
    }

    @GetMapping("/newAddedEnterpriseTax")
    @ApiOperation(value = "新增纳税企业明细表")
    public String getNewAddedEnterpriseTax(ReportBaseRequest request) {
        return GsonUtil.build(reportService.findNewAddedEnterpriseTax(request));
    }

    @GetMapping("/newEnterpriseTaxExport")
    @ApiOperation(value = "导出新注册户纳税情况")
    public void newEnterpriseTaxExport(NewEnterpriseTaxParam request, HttpServletResponse resp){
        reportService.newEnterpriseTaxExport(request, resp);
    }

    @GetMapping("/newEnterpriseTax")
    @ApiOperation(value = "新注册户纳税情况")
    public String getNewEnterpriseTax(NewEnterpriseTaxParam param) {
        return GsonUtil.build(reportService.findNewEnterpriseTax(param));
    }

    @GetMapping("/districtInputSituationExport")
    @ApiOperation(value = "导出经济小区录入情况")
    public void districtInputSituationExport(ReportBaseRequest request, HttpServletResponse resp){
        reportService.districtInputSituationExport(request, resp);
    }

    @GetMapping("/districtInputSituation")
    @ApiOperation(value = "经济小区录入情况")
    public String getDistrictInputSituation(ReportBaseRequest reportBaseRequest) {
        return GsonUtil.build(reportService.findDistrictInputSituation(reportBaseRequest));
    }

    @GetMapping("/monthlyMainIndicatorExport")
    @ApiOperation(value = "导出企业主要指标月报表")
    public void monthlyMainIndicatorExport(ReportBaseRequest request, HttpServletResponse resp){
        reportService.monthlyMainIndicatorExport(request, resp);
    }

    @GetMapping("/monthlyMainIndicator")
    @ApiOperation(value = "企业主要指标月报表")
    public String getMonthlyMainIndicator(ReportBaseRequest reportBaseRequest) {
        return GsonUtil.build(reportService.findMonthlyMainIndicator(reportBaseRequest));
    }

    @GetMapping("/taxRankingExport")
    @ApiOperation(value = "导出税收排名")
    public void taxRankingExport(ReportBaseRequest request, HttpServletResponse resp){
        reportService.taxRankingExport(request, resp);
    }

    @GetMapping("/taxRanking")
    @ApiOperation(value = "税收排名")
    public String getTaxRanking(ReportBaseRequest reportBaseRequest) {
        return GsonUtil.build(reportService.findTaxRankingList(reportBaseRequest));
    }

    @GetMapping("/taxPayerComparisonExport")
    @ApiOperation(value = "导出纳税大户同期对比表")
    public void taxPayerComparisonExport(ReportBaseRequest request, HttpServletResponse resp){
        reportService.taxPayerComparisonExport(request, resp);
    }

    @GetMapping("/taxPayerComparison")
    @ApiOperation(value = "纳税大户同期对比表")
    public String getTaxPayerComparison(ReportBaseRequest reportBaseRequest) {
        return GsonUtil.build(reportService.findTaxPayerComparison(reportBaseRequest));
    }

    @GetMapping("/regionIncomeStatExport")
    @ApiOperation(value = "导出年度区级收入统计表")
    public void regionIncomeStatExport(ReportBaseRequest request, HttpServletResponse resp) {
        reportService.regionIncomeStatExport(request,null, resp);
    }

    @GetMapping("/regionIncomeStat")
    @ApiOperation(value = "年度区级收入统计表")
    public String regionIncomeStat(ReportBaseRequest request) {
        return GsonUtil.build(reportService.findRegionIncomeList(request));
    }

    /**
     * @Description 查询企业综合报表的字段
     * @Title getComprehensiveReportColumns
     * @Param []
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/04/21 17:58
     */
    @GetMapping("/getComprehensiveReportColumns")
    @ApiOperation(value = "查询企业综合报表的字段")
    public String getComprehensiveReportColumns() {
        return GsonUtil.build(reportService.getComprehensiveReportColumns());
    }

    /**
     * @Description 通过excel查询企业综合报表
     * @Title getComprehensiveReportByExcel
     * @Param [request]
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/04/21 15:58
     */
    @PostMapping("/getComprehensiveReportByExcel")
    @ApiOperation(value = "通过excel查询企业综合报表")
    public String getComprehensiveReportByExcel(@RequestPart("file") @NonNull MultipartFile file) {
        return GsonUtil.build(reportService.findComprehensiveReportByExcel(file));
    }

    /**
     * @Description 导出企业综合报表
     * @Title exportComprehensiveReport
     * @Param [param, resp]
     * @Return void
     * @Author Louis
     * @Date 2022/04/21 15:58
     */
    @PostMapping("/exportComprehensiveReport")
    @ApiOperation(value = "导出企业综合报表")
    public void exportComprehensiveReport(@RequestBody EnterpriseComprehensiveReportParam param, HttpServletResponse resp) {
        reportService.exportComprehensiveReport(param, resp);
    }

}
