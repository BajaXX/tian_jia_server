package com.bemore.api.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.annotation.ExcelColumn;
import com.bemore.api.dao.PersonDao;
import com.bemore.api.dao.mapper.*;
import com.bemore.api.entity.*;
import com.bemore.api.entity.request.*;
import com.bemore.api.entity.response.*;
import com.bemore.api.enums.ResultCodeEnum;
import com.bemore.api.exception.GlobalException;
import com.bemore.api.service.EnterpriseTaxService;
import com.bemore.api.service.ReportService;
import com.bemore.api.util.DateUtil;
import com.bemore.api.util.FileUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    // Service
    @Autowired
    private EnterpriseTaxService enterpriseTaxService;
    // Dao
    @Autowired
    private PersonDao personDao;
    // Mapper
    private final EnterpriseMapper enterpriseMapper;
    private final EnterpriseTaxMapper enterpriseTaxMapper;
    private final SupportAgreementMapper supportAgreementMapper;
    private final SupportMonthLogMapper supportMonthLogMapper;
    private final NewValidAccountMapper newValidAccountMapper;

    public ReportServiceImpl(EnterpriseMapper enterpriseMapper,
                             EnterpriseTaxMapper enterpriseTaxMapper,
                             SupportAgreementMapper supportAgreementMapper,
                             SupportMonthLogMapper supportMonthLogMapper,
                             NewValidAccountMapper newValidAccountMapper) {
        this.enterpriseMapper = enterpriseMapper;
        this.enterpriseTaxMapper = enterpriseTaxMapper;
        this.supportAgreementMapper = supportAgreementMapper;
        this.supportMonthLogMapper = supportMonthLogMapper;
        this.newValidAccountMapper = newValidAccountMapper;
    }

    @Override
    public void regionIncomeStatExportByExcel(TaxRequest request, HttpServletResponse resp) {
        ReportBaseRequest baseRequest = new ReportBaseRequest();
        BeanUtils.copyProperties(request, baseRequest);
        regionIncomeStatExport(baseRequest, request, resp);
    }

    @Override
    public RegionIncomeExcelView findRegionIncomeTaxByExcel(TaxRequest request) {
        MultipartFile file = request.getFile();
        List<String> nameList = new ArrayList<>();
        if (!Objects.isNull(file)) {
            nameList = FileUtil.getEnterpriseNameByExcel(file);
        }
        List<String> finalNameList = new ArrayList<String>(nameList);
        RegionIncomeExcelView view = new RegionIncomeExcelView();

        if (nameList.isEmpty()) return view;
        view.setCount(nameList.size());

        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        String industry = request.getIndustry();
        String economy = request.getEconomy();
        String queryString = request.getQueryString();
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(!nameList.isEmpty(), EnterpriseTaxPlus::getEnterpriseName, nameList)
                .like(StringUtils.hasLength(queryString), EnterpriseTaxPlus::getEnterpriseName, queryString)
                .eq(StringUtils.hasLength(industry), EnterpriseTaxPlus::getIndustry, industry)
                .eq(StringUtils.hasLength(economy), EnterpriseTaxPlus::getEnterpriseType, economy)
                .between(EnterpriseTaxPlus::getDate, startDate, endDate)
//                .gt(EnterpriseTaxPlus::getTotalTax, 0)
                .groupBy(EnterpriseTaxPlus::getEnterpriseName);
//                .orderByDesc(EnterpriseTaxPlus::getEnterpriseName)
//                .orderByDesc(EnterpriseTaxPlus::getDate);
        queryWrapper.select(
                "garden",
                "enterprise_no",
                "enterprise_name",
                "industry",
                "found_date",
                "sum(added_tax) as addedTax",
                "sum(land_tax) as landTax",
                "sum(excise) as excise",
                "sum(car_tax) as carTax",
                "sum(to_added_tax) as toAddedTax",
                "sum(income_tax) as incomeTax",
                "sum(person_tax) as personTax",
                "sum(city_tax) as cityTax",
                "sum(added_land_tax) as addedLandTax",
                "sum(house_tax) as houseTax",
                "sum(stamp_tax) as stampTax"
        );
        List<EnterpriseTaxPlus> contentList = enterpriseTaxMapper.selectList(queryWrapper);

        // 获取查询失败的企业
        for (EnterpriseTaxPlus tax : contentList) {
            nameList.removeIf(name -> Objects.equals(name, tax.getEnterpriseName()));
        }
        List<EnterpriseParam> failedList = nameList.stream().map(name -> {
            EnterpriseParam nameView = new EnterpriseParam();
            nameView.setName(name);
            return nameView;
        }).collect(Collectors.toList());

        // 计算转换
        List<RegionIncomeView> regionIncomeViews = contentList.stream().map(content -> {
            String districtIncomeTotal = convertValue(
                    content.getAddedTax() * 0.325 +
                            content.getAddedLandTax() * 0.8 +
                            content.getPersonTax() * 0.22 +
                            content.getIncomeTax() * 0.2 +
                            content.getCityTax() * 0.65 +
                            content.getAddedLandTax() * 0.32 +
                            content.getHouseTax() * 0.8 +
                            content.getStampTax());
            RegionIncomeView regionIncomeView = RegionIncomeView.builder()
                    .garden(content.getGarden())
                    .enterpriseNo(content.getEnterpriseNo())
                    .enterpriseName(content.getEnterpriseName())
                    .industry(content.getIndustry())
                    .foundDate(Objects.isNull(content.getFoundDate()) ? null : content.getFoundDate().toString())
                    .addedTax(convertValue(content.getAddedTax()))
                    .toAddedTax(convertValue(content.getToAddedTax()))
                    .incomeTax(convertValue(content.getIncomeTax()))
                    .personTax(convertValue(content.getPersonTax()))
                    .cityTax(convertValue(content.getCityTax()))
                    .addedLandTax(convertValue(content.getAddedLandTax()))
                    .houseTax(convertValue(content.getHouseTax()))
                    .stampTax(convertValue(content.getStampTax()))
                    .build();
            regionIncomeView.setThreeTaxCombined(convertValue(content.getExcise() + content.getLandTax() + content.getCarTax()));
            regionIncomeView.setDistrictAddedTax(convertValue(content.getAddedTax() * 0.325));
            regionIncomeView.setDistrictToAddedTax(convertValue(content.getToAddedTax() * 0.8));
            regionIncomeView.setDistrictPersonTax(convertValue(content.getPersonTax() * 0.22));
            regionIncomeView.setDistrictIncomeTax(convertValue(content.getIncomeTax() * 0.2));
            regionIncomeView.setDistrictCityTax(convertValue(content.getCityTax() * 0.65));
            regionIncomeView.setDistrictAddedLandTax(convertValue(content.getAddedLandTax() * 0.32));
            regionIncomeView.setDistrictHouseTax(convertValue(content.getHouseTax() * 0.8));
            regionIncomeView.setDistrictStampTax(convertValue(content.getStampTax()));
            regionIncomeView.setOtherTax(convertValue(content.getExcise() + content.getLandTax() + content.getCarTax()));
            regionIncomeView.setDistrictIncomeTotal(districtIncomeTotal);
            return regionIncomeView;
        }).collect(Collectors.toList());

        view.setFailedList(failedList);

        regionIncomeViews.sort(Comparator.comparingInt(o -> finalNameList.indexOf(o.getEnterpriseName())));

        view.setRegionIncomeViewList(regionIncomeViews);
        return view;
    }

    @Override
    public void enterpriseSupportDetailExport(ReportBaseRequest request, HttpServletResponse response) {
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        List<EnterpriseSupportDetailView> viewList = findEnterpriseSupportDetail(request);

        List<EnterpriseSupportDetailView> rows = CollUtil.newArrayList(viewList);
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.addHeaderAlias("financialLocation", "财政所");
        excelWriter.addHeaderAlias("garden", "经济区");
        excelWriter.addHeaderAlias("operation", "操作方式");
        excelWriter.addHeaderAlias("enterpriseName", "企业名称");
        excelWriter.addHeaderAlias("industry", "产业类别");
        excelWriter.addHeaderAlias("supportProject", "扶持项目");
        excelWriter.addHeaderAlias("depositBank", "开户银行");
        excelWriter.addHeaderAlias("bankAccount", "银行帐号");
        excelWriter.addHeaderAlias("supportAmount", "扶持金额");
        excelWriter.addHeaderAlias("surplus", "留存金额");
        excelWriter.addHeaderAlias("remark", "备注");
        excelWriter.addHeaderAlias("date", "所属月度");
        StringBuffer buffer = new StringBuffer();
        buffer.append(startDate);
        buffer.append("~");
        buffer.append(endDate);
        buffer.append("月企业扶持明细");
        excelWriter.merge(12, buffer.toString());
        excelWriter.write(rows, true);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode("企业扶持明细表", "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    @Override
    public List<EnterpriseSupportDetailView> findEnterpriseSupportDetail(ReportBaseRequest request) {
        String queryString = request.getQueryString();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        QueryWrapper<SupportMonthLog> logQueryWrapper = new QueryWrapper<>();
        logQueryWrapper.lambda().eq(StringUtils.hasLength(queryString), SupportMonthLog::getEnterpriseName, queryString)
                .between(SupportMonthLog::getDate, startDate, endDate);
        List<SupportMonthLog> supportMonthLogs = supportMonthLogMapper.selectList(logQueryWrapper);
        List<EnterpriseSupportDetailView> viewList = supportMonthLogs.stream().map(supportMonthLog -> {
            EnterpriseSupportDetailView view = EnterpriseSupportDetailView.builder().build();
            BeanUtils.copyProperties(supportMonthLog, view);
            view.setSupportAmountValue(Double.parseDouble(supportMonthLog.getSupportAmount()));
            view.setMonthAmountValue(Double.parseDouble(supportMonthLog.getMonthAmount()));
            view.setSurplusValue(Double.parseDouble(supportMonthLog.getSurplus()));
            return view;
        }).collect(Collectors.toList());
        viewList = viewList.stream().sorted(Comparator.comparing(EnterpriseSupportDetailView::getSupportAmountValue).reversed()).collect(Collectors.toList());
        return viewList;


//        if (!StringUtils.hasLength(queryDate)) queryDate = LocalDateTime.now().getYear() + "-" + LocalDateTime.now().getMonthValue();
//        // 扶持协议表，把符合查询时间范围的扶持企业找出来
//        LocalDate startDate = LocalDate.parse(queryDate + "-01");
//        LocalDate with = startDate.with(TemporalAdjusters.lastDayOfMonth());
//        LocalDate endDate = LocalDate.parse(queryDate + "-" + with.getDayOfMonth());
//        QueryWrapper<SupportAgreement> supportWrapper = new QueryWrapper<>();
//        supportWrapper.lambda().ge(SupportAgreement::getStartDate,startDate)
//                .ge(SupportAgreement::getEndDate,endDate)
//                .eq(SupportAgreement::getState,"启用");
//        List<SupportAgreement> supportAgreements = supportAgreementMapper.selectList(supportWrapper);
//        Set<String> enterpriseNameSet = supportAgreements.stream().map(SupportAgreement::getEnterpriseName).collect(Collectors.toSet());
//        // 按条件查询企业税收数据
//        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(EnterpriseTaxPlus::getDate,queryDate)
//                .in(!enterpriseNameSet.isEmpty(),EnterpriseTaxPlus::getEnterpriseName,enterpriseNameSet);
//        List<EnterpriseTaxPlus> taxList = enterpriseTaxMapper.selectList(queryWrapper);
//        // 计算组装扶持明细
//        List<EnterpriseSupportDetailView> viewList = new ArrayList<>();
//        supportAgreements.forEach(supportAgreement -> {
//            for (EnterpriseTaxPlus enterpriseTax : taxList) {
//                if (Objects.equals(supportAgreement.getEnterpriseName(),enterpriseTax.getEnterpriseName())) {
//                    // 增值税
//                    Double addedTax = Objects.isNull(enterpriseTax.getAddedTax()) ? 0 : enterpriseTax.getAddedTax();
//                    // 企业所得税
//                    Double incomeTax = Objects.isNull(enterpriseTax.getIncomeTax()) ? 0 : enterpriseTax.getIncomeTax();
//                    // 个人所得税
//                    Double personTax = Objects.isNull(enterpriseTax.getPersonTax()) ? 0 : enterpriseTax.getPersonTax();
////                    // 集团率
////                    String groupRate = supportAgreement.getGroupRate();
////                    // 增值税企业率
////                    String addedTaxRate = supportAgreement.getAddedTaxRate();
////                    // 企业所得税企业率
////                    String incomeTaxRate = supportAgreement.getIncomeTaxRate();
////                    // 个人所得税企业率
////                    String personTaxRate = supportAgreement.getPersonTaxRate();
//                    // 增税率计算值
//                    String addedTaxRateValue = supportAgreement.getAddedTaxRateValue();
//                    // 企税率计算值
//                    String incomeTaxRateValue = supportAgreement.getIncomeTaxRateValue();
//                    // 个税率计算值
//                    String personTaxRateValue = supportAgreement.getPersonTaxRateValue();
//                    // 扶持金额 增值税*增税率计算值 + 企业所得税*企税率计算值 + 个人所得税*个税率计算值
//                    double supportAmount = addedTax * Double.parseDouble(addedTaxRateValue) +
//                            incomeTax * Double.parseDouble(incomeTaxRateValue) +
//                            personTax * Double.parseDouble(personTaxRateValue);
//                    EnterpriseSupportDetailView view =EnterpriseSupportDetailView.builder()
//                            .garden(enterpriseTax.getGarden())
//                            .enterpriseNo(enterpriseTax.getEnterpriseNo())
//                            .enterpriseName(enterpriseTax.getEnterpriseName())
//                            .supportAreas(supportAgreement.getSupportAreas())
//                            .supportProject(supportAgreement.getSupportProject())
//                            .supportAmount(convertValue(supportAmount))
//                            .date(enterpriseTax.getDate())
//                            .build();
//                    viewList.add(view);
//                    break;
//                }
//            }
//        });
//        return viewList;
    }

    @Override
    public void taxMarginTableExport(ReportBaseRequest request, HttpServletResponse response) {
        int year = request.getYear();
        List<TaxMarginTableView> viewList = findTaxMarginTable(request);

//        double addedTaxCount = 0;
//        double toAddedTaxCount = 0;
//        double businessTaxCount = 0;
//        double cityTaxCount = 0;
//        double incomeTaxCount = 0;
//        double personTaxCount = 0;
//        double exciseCount = 0;
//        double addedLandTaxCount = 0;
//        double houseTaxCount = 0;
//        double stampTaxCount = 0;
//        double totalTaxTotalCount = 0;
//        if (!viewList.isEmpty()) {
//            addedTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getAddedTax).sum();
//            toAddedTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getToAddedTax).sum();
//            businessTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getBusinessTax).sum();
//            cityTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getCityTax).sum();
//            incomeTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getIncomeTax).sum();
//            personTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getPersonTax).sum();
//            exciseCount = viewList.stream().mapToDouble(TaxMarginTableView::getExcise).sum();
//            addedLandTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getAddedLandTax).sum();
//            houseTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getHouseTax).sum();
//            stampTaxCount = viewList.stream().mapToDouble(TaxMarginTableView::getStampTax).sum();
//            totalTaxTotalCount = viewList.stream().mapToDouble(TaxMarginTableView::getTotalTaxTotal).sum();
//        }

        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setBorder(BorderStyle.NONE, IndexedColors.BLACK);
        styleSet.setWrapText();
        CellStyle cellStyleForNumber = styleSet.getCellStyleForNumber();
        cellStyleForNumber.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle cellStyle = styleSet.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

        excelWriter.addHeaderAlias("date", "年月").setColumnWidth(0, 15);
        excelWriter.addHeaderAlias("addedTax", "增值税").setColumnWidth(1, 15);
        excelWriter.addHeaderAlias("toAddedTax", "营改增").setColumnWidth(2, 15);
        excelWriter.addHeaderAlias("businessTax", "营业税").setColumnWidth(3, 15);
        excelWriter.addHeaderAlias("cityTax", "城建税").setColumnWidth(4, 15);
        excelWriter.addHeaderAlias("incomeTax", "企业所得税").setColumnWidth(5, 15);
        excelWriter.addHeaderAlias("personTax", "个人所得税").setColumnWidth(6, 15);
        excelWriter.addHeaderAlias("excise", "消费税").setColumnWidth(7, 15);
        excelWriter.addHeaderAlias("addedLandTax", "土地增值税").setColumnWidth(8, 15);
        excelWriter.addHeaderAlias("houseTax", "房产税").setColumnWidth(9, 15);
        excelWriter.addHeaderAlias("stampTax", "印花税").setColumnWidth(10, 15);
        excelWriter.addHeaderAlias("totalTaxTotal", "税金合计").setColumnWidth(11, 15);
        excelWriter.addHeaderAlias("differenceRatio", "增减率").setColumnWidth(12, 15);
        StringBuffer buffer = new StringBuffer();
        buffer.append(year);
        buffer.append("年年税收与");
        buffer.append(year - 1);
        buffer.append("年园区企业总体税收增减幅度对比表");
        excelWriter.merge(12, buffer.toString());
        excelWriter.write(viewList, true);
//        excelWriter.writeCellValue(0,viewList.size() + 2,"总计");
//        excelWriter.writeCellValue(1,viewList.size() + 2,addedTaxCount);
//        excelWriter.writeCellValue(2,viewList.size() + 2,toAddedTaxCount);
//        excelWriter.writeCellValue(3,viewList.size() + 2,businessTaxCount);
//        excelWriter.writeCellValue(4,viewList.size() + 2,cityTaxCount);
//        excelWriter.writeCellValue(5,viewList.size() + 2,incomeTaxCount);
//        excelWriter.writeCellValue(6,viewList.size() + 2,personTaxCount);
//        excelWriter.writeCellValue(7,viewList.size() + 2,exciseCount);
//        excelWriter.writeCellValue(8,viewList.size() + 2,addedLandTaxCount);
//        excelWriter.writeCellValue(9,viewList.size() + 2,houseTaxCount);
//        excelWriter.writeCellValue(10,viewList.size() + 2,stampTaxCount);
//        excelWriter.writeCellValue(11,viewList.size() + 2,totalTaxTotalCount);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    @Override
    public List<TaxMarginTableView> findTaxMarginTable(ReportBaseRequest request) {
        if (request.getYear() == null) {
            throw new GlobalException(ResultCodeEnum.INVALID_PARAM);
        }
        int year = request.getYear();
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getYear, year - 1, year)
                .gt(EnterpriseTaxPlus::getTotalTax, 0);
        List<EnterpriseTaxPlus> results = enterpriseTaxMapper.selectList(queryWrapper);

        List<TaxMarginTableView> viewList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(results)) {
            for (int i = 0; i < 12; i++) {
                int tmpMonth = i + 1;
                // 获取月集合
                List<EnterpriseTaxPlus> currentMonthResult = results.stream()
                        .filter(enterpriseTax ->
                                enterpriseTax.getMonth() != null
                                        && tmpMonth == enterpriseTax.getMonth())
                        .collect(Collectors.toList());
                // 计算各项税金
                BigDecimal currentYearAddedTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearAddedTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearToAddedTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getToAddedTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearToAddedTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getToAddedTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearBusinessTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getBusinessTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearBusinessTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getBusinessTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearCityTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getCityTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearCityTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getCityTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearIncomeTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getIncomeTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearIncomeTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getIncomeTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearPersonTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getPersonTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearPersonTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getPersonTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearExciseTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getExcise()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearExciseTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getExcise()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearAddedLandTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedLandTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearAddedLandTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedLandTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearHouseTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getHouseTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearHouseTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getHouseTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal currentYearStampTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getStampTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastYearStampTax = currentMonthResult.stream()
                        .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                        .map(enterpriseTax -> new BigDecimal(enterpriseTax.getStampTax()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                Double currentYearTotalTax = currentYearAddedTax
                        .add(currentYearCityTax
                                .add(currentYearIncomeTax
                                        .add(currentYearPersonTax
                                                .add(currentYearExciseTax
                                                        .add(currentYearAddedLandTax
                                                                .add(currentYearHouseTax
                                                                        .add(currentYearStampTax))))))).doubleValue();
                Double lastYearTotalTax = lastYearAddedTax
                        .add(lastYearCityTax
                                .add(lastYearIncomeTax
                                        .add(lastYearPersonTax
                                                .add(lastYearExciseTax
                                                        .add(lastYearAddedLandTax
                                                                .add(lastYearHouseTax
                                                                        .add(lastYearStampTax))))))).doubleValue();
                // 计算增减率
                String differenceRatioStr = "";
                if (lastYearTotalTax > 0 && currentYearTotalTax > 0) {
                    double differenceRatio = new BigDecimal(convertValue((currentYearTotalTax / lastYearTotalTax) - 1)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    differenceRatioStr = convertValue(differenceRatio * 100) + "%";
                }
                TaxMarginTableView currentYearView = TaxMarginTableView.builder()
                        .date(year + "-" + (tmpMonth < 10 ? "0" + tmpMonth : tmpMonth))
                        .addedTax(HALF_UP(currentYearAddedTax.doubleValue()))
                        .toAddedTax(HALF_UP(currentYearToAddedTax.doubleValue()))
                        .businessTax(HALF_UP(currentYearBusinessTax.doubleValue()))
                        .cityTax(HALF_UP(currentYearCityTax.doubleValue()))
                        .incomeTax(HALF_UP(currentYearIncomeTax.doubleValue()))
                        .personTax(HALF_UP(currentYearPersonTax.doubleValue()))
                        .excise(HALF_UP(currentYearExciseTax.doubleValue()))
                        .addedLandTax(HALF_UP(currentYearAddedLandTax.doubleValue()))
                        .houseTax(HALF_UP(currentYearHouseTax.doubleValue()))
                        .stampTax(HALF_UP(currentYearStampTax.doubleValue()))
                        .totalTaxTotal(HALF_UP(currentYearTotalTax))
                        .differenceRatio(differenceRatioStr)
                        .build();
                viewList.add(currentYearView);
                TaxMarginTableView lastYearView = TaxMarginTableView.builder()
                        .date(year - 1 + "-" + (tmpMonth < 10 ? "0" + tmpMonth : tmpMonth))
                        .addedTax(HALF_UP(lastYearAddedTax.doubleValue()))
                        .toAddedTax(HALF_UP(lastYearToAddedTax.doubleValue()))
                        .businessTax(HALF_UP(lastYearBusinessTax.doubleValue()))
                        .cityTax(HALF_UP(lastYearCityTax.doubleValue()))
                        .incomeTax(HALF_UP(lastYearIncomeTax.doubleValue()))
                        .personTax(HALF_UP(lastYearPersonTax.doubleValue()))
                        .excise(HALF_UP(lastYearExciseTax.doubleValue()))
                        .addedLandTax(HALF_UP(lastYearAddedLandTax.doubleValue()))
                        .houseTax(HALF_UP(lastYearHouseTax.doubleValue()))
                        .stampTax(HALF_UP(lastYearStampTax.doubleValue()))
                        .totalTaxTotal(HALF_UP(lastYearTotalTax))
                        .build();

                viewList.add(lastYearView);
            }
        }
        return viewList;
    }

    @Override
    public void newAddedEnterpriseTaxExport(ReportBaseRequest request, HttpServletResponse response) {
//        Integer year = request.getYear();// 成立年份
        String queryDate = request.getQueryDate();// 起缴月份


//        if (Objects.isNull(year)) year = LocalDateTime.now().getYear();

//        LocalDate start = LocalDate.parse(year + "-01-01");
//        LocalDate end = LocalDate.parse(year + "-12-31");

        if (!StringUtils.hasLength(queryDate)) {
            if (LocalDateTime.now().getMonthValue() == 1) {
                queryDate = LocalDateTime.now().getYear() - 1 + "-12";
            } else {
                queryDate = LocalDateTime.now().getYear() + "-" + (LocalDateTime.now().getMonthValue() - 1);
            }
        }

        List<NewAddedEnterpriseTaxView> viewList = findNewAddedEnterpriseTax(request);
        double totalTaxCount = 0;
        double totalTaxTotalCount = 0;
        int width = 15;
        int newValidAccount = 0;
        double newValidAccountTaxTotal = 0;
        if (!viewList.isEmpty()) {
            String enterpriseName = viewList.stream().max(Comparator.comparingInt((NewAddedEnterpriseTaxView p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width += enterpriseName.length();
            totalTaxCount = viewList.stream().mapToDouble(NewAddedEnterpriseTaxView::getTotalTax).sum();
            totalTaxTotalCount = viewList.stream().mapToDouble(NewAddedEnterpriseTaxView::getTotalTaxTotal).sum();
            // 新增有效户
            List<NewAddedEnterpriseTaxView> newValidAccountList = viewList.stream().filter(view -> Objects.equals(view.getNewValidAccount(), "Y")).collect(Collectors.toList());
            if (!newValidAccountList.isEmpty()) {
                newValidAccount = newValidAccountList.size();
                newValidAccountTaxTotal = newValidAccountList.stream().mapToDouble(NewAddedEnterpriseTaxView::getTotalTaxTotal).sum();
            }
        }
        String newValidAccountMsg = "其中累计税收大于1000元的" + newValidAccount + "户，纳税总额" + newValidAccountTaxTotal + "元";

        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setBorder(BorderStyle.NONE, IndexedColors.BLACK);
        styleSet.setWrapText();
        CellStyle cellStyleForNumber = styleSet.getCellStyleForNumber();
        cellStyleForNumber.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle cellStyle = styleSet.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

//        Font font = excelWriter.createFont();
//        font.setColor(Font.COLOR_RED);

        excelWriter.addHeaderAlias("enterpriseNo", "企业编号").setColumnWidth(0, 15);
        excelWriter.addHeaderAlias("registerNum", "组织机构代码").setColumnWidth(1, 15);
        excelWriter.addHeaderAlias("enterpriseName", "企业名称").setColumnWidth(2, width);
        excelWriter.addHeaderAlias("source", "招商来源").setColumnWidth(3, width);
        excelWriter.addHeaderAlias("actContactAddress", "企业地址").setColumnWidth(4, width);
        excelWriter.addHeaderAlias("process", "状态");
        excelWriter.addHeaderAlias("foundDate", "成立时间").setColumnWidth(6, 15);
        excelWriter.addHeaderAlias("reportDate", "建档时间").setColumnWidth(7, 15);
        excelWriter.addHeaderAlias("paymentDate", "启缴时间").setColumnWidth(8, 15);
        excelWriter.addHeaderAlias("totalTax", "当月纳税额").setColumnWidth(9, 15);
        excelWriter.addHeaderAlias("totalTaxTotal", "累计纳税额").setColumnWidth(10, 15);
//        2022年01月新增纳税企业明细
        StringBuffer buffer = new StringBuffer();
        buffer.append(Integer.valueOf(queryDate.split("-")[0]));
        buffer.append("年");
        buffer.append(Integer.valueOf(queryDate.split("-")[1]));
        buffer.append("月");
        buffer.append("新增纳税企业明细");
        excelWriter.setOnlyAlias(true);
        excelWriter.merge(10, buffer.toString());
        excelWriter.merge(10, newValidAccountMsg, false);
        excelWriter.write(viewList, true);
        excelWriter.writeCellValue(0, viewList.size() + 3, "总计");
        excelWriter.writeCellValue(9, viewList.size() + 3, totalTaxCount);
        excelWriter.writeCellValue(10, viewList.size() + 3, totalTaxTotalCount);
//        excelWriter.writeCellValue(0,1,"经济小区名称：长三角");
//        excelWriter.writeCellValue(1,1,String.format("%d年%d月份",Integer.valueOf(queryDate.split("-")[0]),Integer.valueOf(queryDate.split("-")[1])));
//        excelWriter.writeCellValue(0,4,"合计");
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");


//            List<Double> totalTaxList = records.stream().filter(item -> item.getTotalTaxTotal() != null).map(EnterpriseTaxPlus::getTotalTaxTotal).collect(Collectors.toList());
//            double totalTax = totalTaxList.stream().mapToDouble(item -> item).sum();
//            List<Double> curMonthTaxList = records.stream().filter(item -> item.getTotalTax() != null).map(EnterpriseTaxPlus::getTotalTax).collect(Collectors.toList());
//            double curMonthTax = curMonthTaxList.stream().mapToDouble(item -> item).sum();
//            excelWriter.writeCellValue(10,4,totalTax);
//            excelWriter.writeCellValue(11,4,curMonthTax);
            out = response.getOutputStream();
            excelWriter.flush(out);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    @Override
    public void newEnterpriseTaxExport(NewEnterpriseTaxParam param, HttpServletResponse response) {

        Integer taxYear = param.getTaxYear();
        Integer taxStartMonth = param.getTaxStartMonth();
        Integer taxEndMonth = param.getTaxEndMonth();
        Integer foundYear = param.getFoundYear();

        // 默认查询今年当月(当前月的上个月)税收
        if (Objects.isNull(taxYear)) {
            taxYear = LocalDateTime.now().getYear();
            taxEndMonth = LocalDateTime.now().getMonthValue() - 1;
        }

        List<NewEnterpriseTaxView> newEnterpriseTax = findNewEnterpriseTax(param);
        double capitalTotal = 0;
        int width = 15;
        if (!newEnterpriseTax.isEmpty()) {
            String enterpriseName = newEnterpriseTax.stream().max(Comparator.comparingInt((NewEnterpriseTaxView p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width += enterpriseName.length();
            capitalTotal = newEnterpriseTax.stream().mapToDouble(NewEnterpriseTaxView::getCapital).sum();
        }

        //        2020年新注册户2021年7月纳税情况表
        StringBuffer buffer = new StringBuffer();
        if (!Objects.isNull(foundYear)) {
            buffer.append(foundYear);
            buffer.append("年");
        }
        buffer.append("新注册户");
        buffer.append(taxYear);
        buffer.append("年");
        buffer.append(taxStartMonth);
        buffer.append("~");
        buffer.append(taxEndMonth);
        buffer.append("月纳税情况表");

        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setBorder(BorderStyle.NONE, IndexedColors.BLACK);
        styleSet.setWrapText();
        CellStyle cellStyleForNumber = styleSet.getCellStyleForNumber();
        cellStyleForNumber.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle cellStyle = styleSet.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

        excelWriter.addHeaderAlias("enterpriseNo", "企业编号").setColumnWidth(0, 15);
        excelWriter.addHeaderAlias("process", "开业类型").setColumnWidth(1, 15);
        excelWriter.addHeaderAlias("enterpriseName", "企业名称").setColumnWidth(2, width);
        excelWriter.addHeaderAlias("registerNum", "机构代码证").setColumnWidth(3, 15);
        excelWriter.addHeaderAlias("registerAddress", "注册地址").setColumnWidth(4, width);
        excelWriter.addHeaderAlias("enterpriseType", "经济性质").setColumnWidth(5, width);
        excelWriter.addHeaderAlias("industry", "行业");
        excelWriter.addHeaderAlias("capital", "注册资本").setColumnWidth(7, 15);
        excelWriter.addHeaderAlias("foundDate", "成立日期").setColumnWidth(8, 15);
        excelWriter.addHeaderAlias("paymentDate", "启缴日期").setColumnWidth(9, 15);
        excelWriter.addHeaderAlias("monthTax", taxYear + "年" + taxEndMonth + "月税收").setColumnWidth(10, 15);
        excelWriter.addHeaderAlias("lastYearLastMonthTax", (taxYear - 1) + "年" + taxEndMonth + "月税收").setColumnWidth(11, 15);
        excelWriter.addHeaderAlias("monthDifferential", "当月增减").setColumnWidth(12, 15);
        excelWriter.addHeaderAlias("monthDifferenceRatio", "当月增减率").setColumnWidth(13, 15);
        excelWriter.addHeaderAlias("thisYearLastMonthTax", taxYear + "年至上月").setColumnWidth(14, 15);
        excelWriter.addHeaderAlias("selectedMonthTax", taxYear + "年" + taxStartMonth + "~" + taxEndMonth + "月累计税收").setColumnWidth(15, 20);
        excelWriter.addHeaderAlias("lastYearSelectedMonthTax", (taxYear - 1) + "年" + taxStartMonth + "~" + taxEndMonth + "月累计税收").setColumnWidth(16, 20);
        excelWriter.addHeaderAlias("yearDifferential", "累计增减").setColumnWidth(17, 15);
        excelWriter.addHeaderAlias("differenceRatio", "累计增减率").setColumnWidth(18, 15);
        excelWriter.addHeaderAlias("follower", "招商人员").setColumnWidth(19, 15);
        excelWriter.setOnlyAlias(true);
        excelWriter.merge(19, buffer.toString());
        excelWriter.write(newEnterpriseTax, true);
        excelWriter.writeCellValue(0, newEnterpriseTax.size() + 2, "总计");
        excelWriter.writeCellValue(7, newEnterpriseTax.size() + 2, capitalTotal);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }

    }

    @Override
    public void districtInputSituationExport(ReportBaseRequest request, HttpServletResponse response) {
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();
        String industry = request.getIndustry();
        String economy = request.getEconomy();
//        int page = request.getPage();
//        if (page > 0)
//            page = page - 1;

//        IPage<EnterpriseTaxPlus> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page,request.getSize());

        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .eq(StringUtils.hasLength(industry), EnterpriseTaxPlus::getIndustry, industry)
                .eq(StringUtils.hasLength(economy), EnterpriseTaxPlus::getEnterpriseType, economy)
                .orderByDesc(EnterpriseTaxPlus::getTotalTax)
                .orderByDesc(EnterpriseTaxPlus::getSales);
        queryWrapper.select(
                "enterprise_no",
                "enterprise_name",
                "industry",
                "enterprise_type",
                "process",
                "garden",
                "register_num",
                "found_date",
                "source",
                "capital",
                "register_address",
                "act_contact_address",
                "date",
                "month",
                "total_tax",
                "report_date",
                "payment_date"
        );
        List<EnterpriseTaxPlus> districtInputSituation = enterpriseTaxMapper.selectList(queryWrapper);

        int width = 15;
        if (!districtInputSituation.isEmpty()) {
            String enterpriseName = districtInputSituation.stream().max(Comparator.comparingInt((EnterpriseTaxPlus p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width += enterpriseName.length();
        }

//        List<EnterpriseTaxPlus> rows = CollUtil.newArrayList(districtInputSituation);
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);

        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setBorder(BorderStyle.NONE, IndexedColors.BLACK);
        styleSet.setWrapText();
        CellStyle cellStyleForNumber = styleSet.getCellStyleForNumber();
        cellStyleForNumber.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle cellStyle = styleSet.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
//        CellStyle cellStyle = styleSet.getCellStyleForNumber();
//        cellStyle.setAlignment(HorizontalAlignment.LEFT);


        excelWriter.addHeaderAlias("date", "所属月份");
        excelWriter.addHeaderAlias("process", "项目类型");
//        excelWriter.addHeaderAlias("industry","行业");
        excelWriter.addHeaderAlias("enterpriseName", "企业名称").setColumnWidth(2, width);
//        excelWriter.addHeaderAlias("reportDate","上报日期");
//        excelWriter.addHeaderAlias("thisRegion","本区");
        excelWriter.addHeaderAlias("actContactAddress", "实际经营地址").setColumnWidth(3, width);
        excelWriter.addHeaderAlias("registerAddress", "注册地址").setColumnWidth(4, width);
//        excelWriter.addHeaderAlias("month","所属月份");
        excelWriter.addHeaderAlias("enterpriseType", "经济性质").setColumnWidth(5, width);
        excelWriter.addHeaderAlias("capital", "注册资本（万元）").setColumnWidth(6, 15);
//        excelWriter.addHeaderAlias("paymentDate","启缴月份");
//        excelWriter.addHeaderAlias("totalTax","启缴金额");
        excelWriter.addHeaderAlias("source", "招商来源").setColumnWidth(7, width);
//        excelWriter.addHeaderAlias("inType","入住类型");
//        2021年1~7月[长三角金融产业园]青浦区经济小区录入情况统计表（明细表）
        StringBuffer buffer = new StringBuffer();
        buffer.append(startDate);
        buffer.append("~");
        buffer.append(endDate);
        buffer.append("月经济小区录入情况");
        excelWriter.setOnlyAlias(true);
        excelWriter.merge(7, buffer.toString());
        excelWriter.write(districtInputSituation, true);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    @Override
    public void monthlyMainIndicatorExport(ReportBaseRequest request, HttpServletResponse response) {
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        List<MonthlyMainIndicatorView> viewList = findMonthlyMainIndicator(request);
        double salesTotal = 0;
        double salesTotalCount = 0;
        double totalTaxCount = 0;
        double totalTaxTotalValueCount = 0;
        int width = 15;
        if (!viewList.isEmpty()) {
            String enterpriseName = viewList.stream().max(Comparator.comparingInt((MonthlyMainIndicatorView p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width += enterpriseName.length();
            salesTotal = viewList.stream().mapToDouble(MonthlyMainIndicatorView::getSales).sum();
            salesTotalCount = viewList.stream().mapToDouble(MonthlyMainIndicatorView::getSalesTotal).sum();
            totalTaxCount = viewList.stream().mapToDouble(MonthlyMainIndicatorView::getTotalTax).sum();
            totalTaxTotalValueCount = viewList.stream().mapToDouble(MonthlyMainIndicatorView::getTotalTaxTotalValue).sum();
        }

//        List<MonthlyMainIndicatorView> rows = CollUtil.newArrayList(viewList);
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setBorder(BorderStyle.NONE, IndexedColors.BLACK);
        styleSet.setWrapText();
        CellStyle cellStyleForNumber = styleSet.getCellStyleForNumber();
        cellStyleForNumber.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle cellStyle = styleSet.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

        excelWriter.addHeaderAlias("enterpriseNo", "企业号").setColumnWidth(0, 15);
        excelWriter.addHeaderAlias("enterpriseName", "企业名称").setColumnWidth(1, width);
//        excelWriter.addHeaderAlias("enterpriseType","经济性质");
//        excelWriter.addHeaderAlias("industry","行业小类");
        excelWriter.addHeaderAlias("sales", "本月销售").setColumnWidth(2, 15);
        excelWriter.addHeaderAlias("salesTotal", "累计销售").setColumnWidth(3, 15);
        excelWriter.addHeaderAlias("totalTax", "本月税金").setColumnWidth(4, 15);
        excelWriter.addHeaderAlias("totalTaxTotalValue", "累计税金").setColumnWidth(5, 15);
        StringBuffer buffer = new StringBuffer();
        buffer.append(startDate);
        buffer.append("至");
        buffer.append(endDate);
        buffer.append("月企业经济主要指标月报表");
        excelWriter.setOnlyAlias(true);
        excelWriter.merge(5, buffer.toString());
        excelWriter.write(viewList, true);
        excelWriter.writeCellValue(0, viewList.size() + 2, "总计");
        excelWriter.writeCellValue(2, viewList.size() + 2, salesTotal);
        excelWriter.writeCellValue(3, viewList.size() + 2, salesTotalCount);
        excelWriter.writeCellValue(4, viewList.size() + 2, totalTaxCount);
        excelWriter.writeCellValue(5, viewList.size() + 2, totalTaxTotalValueCount);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    @Override
    public void taxRankingExport(ReportBaseRequest request, HttpServletResponse response) {
        Integer ranking = request.getRanking();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        List<TaxRankingView> taxRankingList = findTaxRankingList(request);
        double totalTaxCount = 0;
        double totalTaxTotalCount = 0;
        double addedTaxCount = 0;
        double addedTaxTotalCount = 0;
        double toAddedTaxCount = 0;
        double toAddedTaxTotalCount = 0;
        double incomeTaxCount = 0;
        double incomeTaxTotalCount = 0;
        double cityTaxCount = 0;
        double cityTaxTotalCount = 0;
        double personTaxCount = 0;
        double personTaxTotalCount = 0;
        double exciseCount = 0;
        double exciseTotalCount = 0;
        double addedLandTaxCount = 0;
        double addedLandTaxTotalCount = 0;
        double stampTaxCount = 0;
        double stampTaxTotalCount = 0;
        double houseTaxCount = 0;
        double houseTaxTotalCount = 0;
        double landTaxCount = 0;
        double landTaxTotalCount = 0;
        double carTaxCount = 0;
        double carTaxTotalCount = 0;
        double carPurchaseTaxCount = 0;
        double carPurchaseTaxTotalCount = 0;
        int width = 15;
        if (!taxRankingList.isEmpty()) {
            String enterpriseName = taxRankingList.stream().max(Comparator.comparingInt((TaxRankingView p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width += enterpriseName.length();
            totalTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getTotalTax).sum();
            totalTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getTotalTaxTotal).sum();
            addedTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getAddedTax).sum();
            addedTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getAddedTaxTotal).sum();
            toAddedTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getToAddedTax).sum();
            toAddedTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getToAddedTaxTotal).sum();
            incomeTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getIncomeTax).sum();
            incomeTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getIncomeTaxTotal).sum();
            cityTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getCityTax).sum();
            cityTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getCityTaxTotal).sum();
            personTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getPersonTax).sum();
            personTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getPersonTaxTotal).sum();
            exciseCount = taxRankingList.stream().mapToDouble(TaxRankingView::getExcise).sum();
            exciseTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getExciseTotal).sum();
            addedLandTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getAddedLandTax).sum();
            addedLandTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getAddedLandTaxTotal).sum();
            stampTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getStampTax).sum();
            stampTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getStampTaxTotal).sum();
            houseTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getHouseTax).sum();
            houseTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getHouseTaxTotal).sum();
            landTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getLandTax).sum();
            landTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getLandTaxTotal).sum();
            carTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getCarTax).sum();
            carTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getCarTaxTotal).sum();
            carPurchaseTaxCount = taxRankingList.stream().mapToDouble(TaxRankingView::getCarPurchaseTax).sum();
            carPurchaseTaxTotalCount = taxRankingList.stream().mapToDouble(TaxRankingView::getCarPurchaseTaxTotal).sum();
        }

        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setBorder(BorderStyle.NONE, IndexedColors.BLACK);
        styleSet.setWrapText();
        CellStyle cellStyleForNumber = styleSet.getCellStyleForNumber();
        cellStyleForNumber.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle cellStyle = styleSet.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

        excelWriter.addHeaderAlias("enterpriseName", "企业名称").setColumnWidth(0, width);
        excelWriter.addHeaderAlias("totalTax", "当月税金合计").setColumnWidth(1, 15);
        excelWriter.addHeaderAlias("totalTaxTotal", "累计税金合计").setColumnWidth(2, 15);
        excelWriter.addHeaderAlias("addedTax", "当月增值税").setColumnWidth(3, 15);
        excelWriter.addHeaderAlias("addedTaxTotal", "累计增值税").setColumnWidth(4, 15);
        excelWriter.addHeaderAlias("toAddedTax", "当月营改增").setColumnWidth(5, 15);
        excelWriter.addHeaderAlias("toAddedTaxTotal", "累计营改增").setColumnWidth(6, 15);
        excelWriter.addHeaderAlias("cityTax", "当月城建税").setColumnWidth(7, 15);
        excelWriter.addHeaderAlias("cityTaxTotal", "累计城建税").setColumnWidth(8, 15);
        excelWriter.addHeaderAlias("incomeTax", "当月企业所得税").setColumnWidth(9, 15);
        excelWriter.addHeaderAlias("incomeTaxTotal", "累计企业所得税").setColumnWidth(10, 15);
        excelWriter.addHeaderAlias("personTax", "当月个人所得税").setColumnWidth(11, 15);
        excelWriter.addHeaderAlias("personTaxTotal", "累计个人所得税").setColumnWidth(12, 15);
        excelWriter.addHeaderAlias("excise", "当月消费税").setColumnWidth(13, 15);
        excelWriter.addHeaderAlias("exciseTotal", "累计消费税").setColumnWidth(14, 15);
        excelWriter.addHeaderAlias("addedLandTax", "当月土地增值税").setColumnWidth(15, 15);
        excelWriter.addHeaderAlias("addedLandTaxTotal", "累计土地增值税").setColumnWidth(16, 15);
        excelWriter.addHeaderAlias("houseTax", "当月房产税").setColumnWidth(17, 15);
        excelWriter.addHeaderAlias("houseTaxTotal", "累计房产税").setColumnWidth(18, 15);
        excelWriter.addHeaderAlias("stampTax", "当月印花税").setColumnWidth(19, 15);
        excelWriter.addHeaderAlias("stampTaxTotal", "累计印花税").setColumnWidth(20, 15);
        excelWriter.addHeaderAlias("landTax", "当月城镇土地使用税").setColumnWidth(21, 15);
        excelWriter.addHeaderAlias("landTaxTotal", "累计城镇土地使用税").setColumnWidth(22, 15);
        excelWriter.addHeaderAlias("carTax", "当月车船使用税").setColumnWidth(23, 15);
        excelWriter.addHeaderAlias("carTaxTotal", "累计车船使用税").setColumnWidth(24, 15);
        excelWriter.addHeaderAlias("carPurchaseTax", "当月车购税").setColumnWidth(25, 15);
        excelWriter.addHeaderAlias("carPurchaseTaxTotal", "累计车购税").setColumnWidth(26, 15);
        StringBuffer buffer = new StringBuffer();
        buffer.append(startDate);
        buffer.append("至");
        buffer.append(endDate);
        buffer.append("税收前");
        buffer.append(Objects.isNull(ranking) ? taxRankingList.size() : ranking);
        buffer.append("名企业排名表");
        excelWriter.setOnlyAlias(true);
        excelWriter.merge(26, buffer.toString());
        excelWriter.write(taxRankingList, true);
        excelWriter.writeCellValue(0, taxRankingList.size() + 2, "总计");
        excelWriter.writeCellValue(1, taxRankingList.size() + 2, totalTaxCount);
        excelWriter.writeCellValue(2, taxRankingList.size() + 2, totalTaxTotalCount);
        excelWriter.writeCellValue(3, taxRankingList.size() + 2, addedTaxCount);
        excelWriter.writeCellValue(4, taxRankingList.size() + 2, addedTaxTotalCount);
        excelWriter.writeCellValue(5, taxRankingList.size() + 2, toAddedTaxCount);
        excelWriter.writeCellValue(6, taxRankingList.size() + 2, toAddedTaxTotalCount);
        excelWriter.writeCellValue(7, taxRankingList.size() + 2, cityTaxCount);
        excelWriter.writeCellValue(8, taxRankingList.size() + 2, cityTaxTotalCount);
        excelWriter.writeCellValue(9, taxRankingList.size() + 2, incomeTaxCount);
        excelWriter.writeCellValue(10, taxRankingList.size() + 2, incomeTaxTotalCount);
        excelWriter.writeCellValue(11, taxRankingList.size() + 2, personTaxCount);
        excelWriter.writeCellValue(12, taxRankingList.size() + 2, personTaxTotalCount);
        excelWriter.writeCellValue(13, taxRankingList.size() + 2, exciseCount);
        excelWriter.writeCellValue(14, taxRankingList.size() + 2, exciseTotalCount);
        excelWriter.writeCellValue(15, taxRankingList.size() + 2, addedLandTaxCount);
        excelWriter.writeCellValue(16, taxRankingList.size() + 2, addedLandTaxTotalCount);
        excelWriter.writeCellValue(17, taxRankingList.size() + 2, houseTaxCount);
        excelWriter.writeCellValue(18, taxRankingList.size() + 2, houseTaxTotalCount);
        excelWriter.writeCellValue(19, taxRankingList.size() + 2, stampTaxCount);
        excelWriter.writeCellValue(20, taxRankingList.size() + 2, stampTaxTotalCount);
        excelWriter.writeCellValue(21, taxRankingList.size() + 2, landTaxCount);
        excelWriter.writeCellValue(22, taxRankingList.size() + 2, landTaxTotalCount);
        excelWriter.writeCellValue(23, taxRankingList.size() + 2, carTaxCount);
        excelWriter.writeCellValue(24, taxRankingList.size() + 2, carTaxTotalCount);
        excelWriter.writeCellValue(25, taxRankingList.size() + 2, carPurchaseTaxCount);
        excelWriter.writeCellValue(26, taxRankingList.size() + 2, carPurchaseTaxTotalCount);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }

    }

    @Override
    public void taxPayerComparisonExport(ReportBaseRequest request, HttpServletResponse response) {
        Integer year = request.getYear();
        Integer startMonth = request.getStartMonth();
        Integer endMonth = request.getEndMonth();
        Integer ranking = request.getRanking();
        // 默认查询今年的第一月到当月时间
        if (Objects.isNull(year)) year = LocalDateTime.now().getYear();
        if (Objects.isNull(startMonth)) startMonth = LocalDateTime.now().getMonthValue();
        if (Objects.isNull(endMonth)) endMonth = LocalDateTime.now().getMonthValue();
        int lastYear = year - 1;
        if (Objects.isNull(ranking)) ranking = 100;

        List<TaxPayerComparisonView> taxPayerComparison = findTaxPayerComparison(request);
        int width = 15;
        if (!taxPayerComparison.isEmpty()) {
            String enterpriseName = taxPayerComparison.stream().max(Comparator.comparingInt((TaxPayerComparisonView p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width = enterpriseName.length();
        }

        List<TaxPayerComparisonView> rows = CollUtil.newArrayList(taxPayerComparison);
        // 组装表头
        String currentYearTitle = year + "年" + startMonth + "-" + endMonth + "月";
        String lastYearTitle = (year - 1) + "年" + startMonth + "-" + endMonth + "月";
        String currentYearLastMonthTitle = year + "年" + endMonth + "月";
        String lastYearLastMonthTitle = (year - 1) + "年" + endMonth + "月";
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);

        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setWrapText();

        excelWriter.addHeaderAlias("enterpriseNo", "企业编号");
        excelWriter.addHeaderAlias("enterpriseName", "企业名称").setColumnWidth(1, width);
        excelWriter.addHeaderAlias("industry", "行业");
        excelWriter.addHeaderAlias("selectedMonthRanking", currentYearTitle + "排名").setColumnWidth(3, 15);
        excelWriter.addHeaderAlias("lastYearSelectedMonthRanking", lastYearTitle + "排名").setColumnWidth(4, 15);
        excelWriter.addHeaderAlias("selectedMonthTax", currentYearTitle + "税收").setColumnWidth(5, 15);
        excelWriter.addHeaderAlias("lastYearSelectedMonthTax", lastYearTitle + "税收").setColumnWidth(6, 15);
        excelWriter.addHeaderAlias("yearDifferential", year + "-" + (year - 1) + "差额").setColumnWidth(7, 15);
        excelWriter.addHeaderAlias("differenceRatio", "累计增减率").setColumnWidth(8, 15);
        excelWriter.addHeaderAlias("monthRanking", currentYearLastMonthTitle + "排名").setColumnWidth(9, 15);
        excelWriter.addHeaderAlias("lastYearMonthRanking", lastYearLastMonthTitle + "排名").setColumnWidth(10, 15);
        excelWriter.addHeaderAlias("monthTax", currentYearLastMonthTitle + "税收").setColumnWidth(11, 15);
        excelWriter.addHeaderAlias("lastYearMonthTax", lastYearLastMonthTitle + "税收").setColumnWidth(12, 15);
        excelWriter.addHeaderAlias("monthDifferential", year + "-" + (year - 1) + "当月差额").setColumnWidth(13, 15);
        excelWriter.addHeaderAlias("monthDifferenceRatio", "当月增减率").setColumnWidth(14, 15);
        StringBuffer buffer = new StringBuffer();
        buffer.append(year);
        buffer.append("年");
        buffer.append(startMonth);
        buffer.append("-");
        buffer.append(endMonth);
        buffer.append("月份前");
        buffer.append(Objects.isNull(ranking) ? 200 : ranking);
        buffer.append("名纳税大户与");
        buffer.append(lastYear);
        buffer.append("年");
        buffer.append(startMonth);
        buffer.append("-");
        buffer.append(endMonth);
        buffer.append("月份同期对比表（名次对比）");
        excelWriter.merge(14, buffer.toString());
        excelWriter.write(rows, true);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }

    }

    @Override
    public void regionIncomeStatExport(ReportBaseRequest request, TaxRequest excelRequest, HttpServletResponse response) {
        List<String> nameList = new ArrayList<>();
        if (!Objects.isNull(excelRequest)) {
            MultipartFile file = excelRequest.getFile();
            if (!Objects.isNull(file)) {
                nameList = FileUtil.getEnterpriseNameByExcel(file);
            }
        }

        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        String industry = request.getIndustry();
        String economy = request.getEconomy();
        String queryString = request.getQueryString();

        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(!nameList.isEmpty(), EnterpriseTaxPlus::getEnterpriseName, nameList)
                .like(StringUtils.hasLength(queryString), EnterpriseTaxPlus::getEnterpriseName, queryString)
                .eq(StringUtils.hasLength(industry), EnterpriseTaxPlus::getIndustry, industry)
                .eq(StringUtils.hasLength(economy), EnterpriseTaxPlus::getEnterpriseType, economy)
                .between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .groupBy(EnterpriseTaxPlus::getEnterpriseName)
                .gt(EnterpriseTaxPlus::getTotalTax, 0)
                .orderByDesc(EnterpriseTaxPlus::getEnterpriseName)
                .orderByDesc(EnterpriseTaxPlus::getDate);
        queryWrapper.select(
                "garden",
                "enterprise_no",
                "enterprise_name",
                "industry",
                "found_date",
                "sum(added_tax) as addedTax",
                "sum(land_tax) as landTax",
                "sum(excise) as excise",
                "sum(car_tax) as carTax",
                "sum(to_added_tax) as toAddedTax",
                "sum(income_tax) as incomeTax",
                "sum(person_tax) as personTax",
                "sum(city_tax) as cityTax",
                "sum(added_land_tax) as addedLandTax",
                "sum(house_tax) as houseTax",
                "sum(stamp_tax) as stampTax"
        );
        List<EnterpriseTaxPlus> enterpriseTaxPluses = enterpriseTaxMapper.selectList(queryWrapper);
        // 计算转换
        List<RegionIncomeView> regionIncomeViews = enterpriseTaxPluses.stream().map(content -> {
            String districtIncomeTotal = convertValue(
                    content.getAddedTax() * 0.32 +
                            content.getToAddedTax() * 0.32 +
                            content.getPersonTax() * 0.32 +
                            content.getIncomeTax() * 0.32 +
                            content.getCityTax() * 0.32 +
                            content.getAddedLandTax() * 0.32 +
                            content.getHouseTax() * 0.32 +
                            content.getStampTax() * 0.32);
            RegionIncomeView regionIncomeView = RegionIncomeView.builder().build();
//            BeanUtils.copyProperties(content, regionIncomeView);
            regionIncomeView.setGarden(content.getGarden());
            regionIncomeView.setEnterpriseNo(content.getEnterpriseNo());
            regionIncomeView.setEnterpriseName(content.getEnterpriseName());
            regionIncomeView.setFoundDate(Objects.isNull(content.getFoundDate()) ? null : content.getFoundDate().toString());
            regionIncomeView.setAddedTax(convertValue(content.getAddedTax()));
            regionIncomeView.setToAddedTax(convertValue(content.getToAddedTax()));
            regionIncomeView.setIncomeTax(convertValue(content.getIncomeTax()));
            regionIncomeView.setPersonTax(convertValue(content.getPersonTax()));
            regionIncomeView.setCityTax(convertValue(content.getCityTax()));
            regionIncomeView.setAddedLandTax(convertValue(content.getAddedLandTax()));
            regionIncomeView.setHouseTax(convertValue(content.getHouseTax()));
            regionIncomeView.setStampTax(convertValue(content.getStampTax()));
            regionIncomeView.setThreeTaxCombined(convertValue(content.getExcise() + content.getLandTax() + content.getCarTax()));
            regionIncomeView.setDistrictAddedTax(convertValue(content.getAddedTax() * 0.32));
            regionIncomeView.setDistrictToAddedTax(convertValue(content.getToAddedTax() * 0.32));
            regionIncomeView.setDistrictPersonTax(convertValue(content.getPersonTax() * 0.32));
            regionIncomeView.setDistrictIncomeTax(convertValue(content.getIncomeTax() * 0.32));
            regionIncomeView.setDistrictCityTax(convertValue(content.getCityTax() * 0.32));
            regionIncomeView.setDistrictAddedLandTax(convertValue(content.getAddedLandTax() * 0.32));
            regionIncomeView.setDistrictHouseTax(convertValue(content.getHouseTax() * 0.32));
            regionIncomeView.setDistrictStampTax(convertValue(content.getStampTax() * 0.32));
            regionIncomeView.setOtherTax(convertValue(content.getExcise() + content.getLandTax() + content.getCarTax()));
            regionIncomeView.setDistrictIncomeTotal(districtIncomeTotal);
            return regionIncomeView;
        }).collect(Collectors.toList());
        List<RegionIncomeView> rows = CollUtil.newArrayList(regionIncomeViews);

        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.addHeaderAlias("garden", "开发区");
        excelWriter.addHeaderAlias("enterpriseNo", "企业编号");
        excelWriter.addHeaderAlias("enterpriseName", "企业名称");
        excelWriter.addHeaderAlias("foundDate", "成立日期");
        excelWriter.addHeaderAlias("threeTaxCombined", "全口径合计（包括消费税、城镇土地使用税、车船使用税）");
        excelWriter.addHeaderAlias("addedTax", "增值税");
        excelWriter.addHeaderAlias("districtAddedTax", "区级增值税");
        excelWriter.addHeaderAlias("toAddedTax", "营改增");
        excelWriter.addHeaderAlias("districtToAddedTax", "区级营改增");
        excelWriter.addHeaderAlias("incomeTax", "企业所得税");
        excelWriter.addHeaderAlias("districtIncomeTax", "区级所得税");
        excelWriter.addHeaderAlias("personTax", "个人所得税");
        excelWriter.addHeaderAlias("districtPersonTax", "区级个人所得税");
        excelWriter.addHeaderAlias("cityTax", "城建税");
        excelWriter.addHeaderAlias("districtCityTax", "区级城建税");
        excelWriter.addHeaderAlias("addedLandTax", "土地增值税");
        excelWriter.addHeaderAlias("districtAddedLandTax", "区级土地增值税");
        excelWriter.addHeaderAlias("houseTax", "房产税");
        excelWriter.addHeaderAlias("districtHouseTax", "区级房产税");
        excelWriter.addHeaderAlias("stampTax", "印花税");
        excelWriter.addHeaderAlias("districtStampTax", "区级印花税");
        excelWriter.addHeaderAlias("districtIncomeTotal", "区级收入合计");
        excelWriter.addHeaderAlias("otherTax", "其他税（消费税、城镇土地使用税、车船使用税）");
        excelWriter.addHeaderAlias("industry", "行业");
        StringBuffer buffer = new StringBuffer();
//        buffer.append(Objects.isNull(request.getYear()) ? LocalDateTime.now().getYear() : request.getYear());
        buffer.append(startDate.replace("-", "年"));
        buffer.append("月~");
        buffer.append(endDate.replace("-", "年"));
        buffer.append("月年度区级收入统计数据");
        excelWriter.merge(24, buffer.toString());
        excelWriter.write(rows, true);

        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode("年度区级收入统计表", "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = response.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    @Override
    public List<NewAddedEnterpriseTaxView> findNewAddedEnterpriseTax(ReportBaseRequest request) {
//        Integer year = request.getYear();// 成立年份
        String queryDate = request.getQueryDate();// 起缴月份


//        if (Objects.isNull(year)) year = LocalDateTime.now().getYear();

//        LocalDate start = LocalDate.parse(year + "-01-01");
//        LocalDate end = LocalDate.parse(year + "-12-31");

        if (!StringUtils.hasLength(queryDate)) {
            if (LocalDateTime.now().getMonthValue() == 1) {
                queryDate = LocalDateTime.now().getYear() - 1 + "-12";
            } else {
                queryDate = LocalDateTime.now().getYear() + "-" + (LocalDateTime.now().getMonthValue() - 1);
            }
        }

        // 查询新增有效户
        List<EnterpriseTaxPlus> newValidAccountList = enterpriseTaxMapper.selectNewValidAccountList(queryDate);

        String finalQueryDate = queryDate;
        List<NewAddedEnterpriseTaxView> validViewList = newValidAccountList.stream().map(record -> {

            NewAddedEnterpriseTaxView view = new NewAddedEnterpriseTaxView();
            view.setEnterpriseName(record.getEnterpriseName());
            view.setRegisterNum(record.getRegisterNum());
            view.setSource(record.getSource());
            view.setActContactAddress(record.getActContactAddress());
            view.setProcess(record.getProcess());
            view.setFoundDate(Objects.isNull(record.getFoundDate()) ? null : record.getFoundDate().toString());
            view.setReportDate(record.getReportDate());
            if (!Objects.isNull(record.getPaymentDate()))
                view.setPaymentDate(record.getPaymentDate().toString().substring(0, 7));
            view.setTotal(getTotalTax(record.getEnterpriseName(), finalQueryDate));
            view.setTotalTax(HALF_UP(record.getTotalTax()));
            view.setTotalTaxTotal(HALF_UP(record.getTotalTaxTotal()));
            view.setNewValidAccount("Y");
            return view;
        }).collect(Collectors.toList());


        // 查询新增纳税户
        List<EnterpriseTaxPlus> newTaxAccountList = enterpriseTaxMapper.selectNewTaxAccount(queryDate);
//        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().ge(EnterpriseTaxPlus::getFoundDate,start)
//                .le(!Objects.isNull(year),EnterpriseTaxPlus::getFoundDate,end)
//                .eq(StringUtils.hasLength(queryDate),EnterpriseTaxPlus::getDate,queryDate)
//                .orderByDesc(EnterpriseTaxPlus::getPaymentDate);
//        List<EnterpriseTaxPlus> records = enterpriseTaxMapper.selectList(queryWrapper);
        if (!newTaxAccountList.isEmpty()) {
            newTaxAccountList = newTaxAccountList.stream().filter(f -> !validViewList.stream().anyMatch(m -> m.getEnterpriseName().equals(f.getEnterpriseName()))).collect(Collectors.toList());
        }

        List<NewAddedEnterpriseTaxView> viewList = newTaxAccountList.stream().map(record -> {

            NewAddedEnterpriseTaxView view = new NewAddedEnterpriseTaxView();
            view.setEnterpriseName(record.getEnterpriseName());
            view.setRegisterNum(record.getRegisterNum());
            view.setSource(record.getSource());
            view.setActContactAddress(record.getActContactAddress());
            view.setProcess(record.getProcess());
            view.setFoundDate(Objects.isNull(record.getFoundDate()) ? null : record.getFoundDate().toString());
            view.setReportDate(record.getReportDate());
            view.setTotal(getTotalTax(record.getEnterpriseName(), finalQueryDate));
            if (!Objects.isNull(record.getPaymentDate()))
                view.setPaymentDate(record.getPaymentDate().toString().substring(0, 7));
            view.setTotalTax(HALF_UP(record.getTotalTax()));
            view.setTotalTaxTotal(HALF_UP(record.getTotalTaxTotal()));
            return view;
        }).collect(Collectors.toList());


        if (!viewList.isEmpty()) {
            validViewList.addAll(viewList);
        }


       /* QueryWrapper<NewValidAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(NewValidAccount::getDate,queryDate);
        List<NewValidAccount> newValidAccounts = newValidAccountMapper.selectList(queryWrapper);
        Set<String> nameList = newValidAccounts.stream().map(NewValidAccount::getEnterpriseName).collect(Collectors.toSet());
        if (!nameList.isEmpty()) {
            QueryWrapper<EnterpriseTaxPlus> taxPlusQueryWrapper = new QueryWrapper<>();
            taxPlusQueryWrapper.lambda().in(!nameList.isEmpty(),EnterpriseTaxPlus::getEnterpriseName,nameList)
                    .eq(EnterpriseTaxPlus::getDate,queryDate);
            List<EnterpriseTaxPlus> taxPlusList = enterpriseTaxMapper.selectList(taxPlusQueryWrapper);
            List<NewAddedEnterpriseTaxView> newValidAccountList = taxPlusList.stream().map(record -> {
                NewAddedEnterpriseTaxView view = new NewAddedEnterpriseTaxView();
                view.setEnterpriseName(record.getEnterpriseName());
                view.setRegisterNum(record.getRegisterNum());
                view.setSource(record.getSource());
                view.setActContactAddress(record.getActContactAddress());
                view.setProcess(record.getProcess());
                view.setFoundDate(Objects.isNull(record.getFoundDate()) ? null : record.getFoundDate().toString());
                view.setReportDate(record.getReportDate());
                if (!Objects.isNull(record.getPaymentDate()))
                    view.setPaymentDate(record.getPaymentDate().toString().substring(0,7));
                view.setTotalTax(HALF_UP(record.getTotalTax()));
                view.setTotalTaxTotal(HALF_UP(record.getTotalTaxTotal()));
                view.setNewValidAccount("Y");
                return view;
            }).collect(Collectors.toList());
            viewList.addAll(newValidAccountList);
        }*/
        return validViewList;
    }

    private Double getTotalTax(String enterpriseName,String queryDate){
       Double totalTax = enterpriseTaxMapper.selectTotalTaxByName(enterpriseName,queryDate);

        if(Objects.isNull(totalTax)){
            return 0.0;
        }else{
            return totalTax;
        }
    }

    /**
     * @param param 1.当年查询的结束月份税收；2.倒推前一年的当月税收；3.当月增减；4.当月增减率；5.今年的到今年上个月税收；
     *              5.查询条件的税收开始月份到税收结束月份税收合计；6.查询条件的税收开始月份到税收结束月份倒推前一年相同月份的税收合计；
     *              7.（5和6） 的累计增减；8。 （5和6） 的累计增减率；
     */
    @Override
    public List<NewEnterpriseTaxView> findNewEnterpriseTax(NewEnterpriseTaxParam param) {
        Integer taxYear = param.getTaxYear();
        Integer taxStartMonth = param.getTaxStartMonth();
        Integer taxEndMonth = param.getTaxEndMonth();
        Integer foundYear = param.getFoundYear();
        Integer foundStartMonth = param.getFoundStartMonth();
        Integer foundEndMonth = param.getFoundEndMonth();

        // 默认查询今年当月(当前月的上个月)税收
        if (Objects.isNull(taxYear)) {
            taxYear = LocalDateTime.now().getYear();
            taxStartMonth = LocalDateTime.now().getMonthValue() - 1;
            taxEndMonth = LocalDateTime.now().getMonthValue() - 1;
        }
        if (Objects.isNull(foundYear)) foundYear = LocalDateTime.now().getYear() - 1;

        LocalDate foundStartDate = LocalDate.parse(foundYear + "-01-01");
        LocalDate foundEndDate = LocalDate.parse(foundYear + "-12-31");

        if (!Objects.isNull(foundYear) && !Objects.isNull(foundStartMonth)) {
            foundStartDate = LocalDate.parse(foundYear + "-" + (foundStartMonth > 9 ? foundStartMonth : "0" + foundStartMonth) + "-01");
        }

        if (!Objects.isNull(foundYear) && !Objects.isNull(foundEndMonth)) {
            LocalDate parse = LocalDate.parse(foundYear + "-" + (foundEndMonth > 9 ? foundEndMonth : "0" + foundEndMonth) + "-01");
            foundEndDate = parse.with(TemporalAdjusters.lastDayOfMonth());
        }
        int lastYear = taxYear - 1;

        // 今年查询条件
        QueryWrapper<EnterpriseTaxPlus> currentYearQuery = new QueryWrapper<>();
        currentYearQuery.lambda().eq(EnterpriseTaxPlus::getYear, taxYear)
                .ge(EnterpriseTaxPlus::getMonth, taxStartMonth)
                .le(EnterpriseTaxPlus::getMonth, taxEndMonth)
                .ge(!Objects.isNull(foundStartDate), EnterpriseTaxPlus::getFoundDate, foundStartDate.toString())
                .le(!Objects.isNull(foundEndDate), EnterpriseTaxPlus::getFoundDate, foundEndDate.toString())
                .orderByDesc(EnterpriseTaxPlus::getPaymentDate);
        currentYearQuery.select(
                "enterprise_no",
                "enterprise_name",
                "month",
                "process",
                "register_num",
                "enterprise_type",
                "register_address",
                "industry",
                "capital",
                "found_date",
                "date",
                "total_tax",
                "payment_date"
        );
        // 去年查询条件
        QueryWrapper<EnterpriseTaxPlus> lastYearQuery = new QueryWrapper<>();
        lastYearQuery.lambda().eq(EnterpriseTaxPlus::getYear, lastYear)
                .ge(EnterpriseTaxPlus::getMonth, taxStartMonth)
                .le(EnterpriseTaxPlus::getMonth, taxEndMonth)
                .ge(!Objects.isNull(foundStartDate), EnterpriseTaxPlus::getFoundDate, foundStartDate.toString())
                .le(!Objects.isNull(foundEndDate), EnterpriseTaxPlus::getFoundDate, foundEndDate.toString())
                .orderByDesc(EnterpriseTaxPlus::getPaymentDate);
        lastYearQuery.select(
                "enterprise_no",
                "enterprise_name",
                "month",
                "process",
                "register_num",
                "enterprise_type",
                "register_address",
                "industry",
                "capital",
                "found_date",
                "date",
                "total_tax",
                "payment_date"
        );

        // 按条件查询数据
        List<EnterpriseTaxPlus> currentYearData = enterpriseTaxMapper.selectList(currentYearQuery);
        List<EnterpriseTaxPlus> lastYearData = enterpriseTaxMapper.selectList(lastYearQuery);

        List<NewEnterpriseTaxView> viewList = new ArrayList<>();
        // 计算 当年查询的结束月份税收,查询条件的税收开始月份到税收结束月份税收合计
        newEnterpriseTaxYearData(viewList, currentYearData, taxEndMonth);
        // 计算 倒推前一年的结束月份税收,查询条件的税收开始月份到税收结束月份倒推前一年相同月份的税收合计
        newEnterpriseTaxYearData(viewList, lastYearData, taxEndMonth);
        // 今年与去年税收差异: 当月增减,当月增减率,累计增减,累计增减率
        viewList.forEach(view -> {
            // 今年与去年税收累计差额 今年减去去年
            double thisYearTotalTax = view.getSelectedMonthTax();
            double lastYearTotalTax = Objects.isNull(view.getLastYearSelectedMonthTax()) ? 0.0 : view.getLastYearSelectedMonthTax();
            double differential = thisYearTotalTax - lastYearTotalTax;
            view.setYearDifferential(HALF_UP(differential));
            // 累计增减率
            view.setDifferenceRatio("0");
            if (lastYearTotalTax > 0 && thisYearTotalTax > 0) {
                double differenceRatio = new BigDecimal((thisYearTotalTax / lastYearTotalTax) - 1).setScale(2, RoundingMode.HALF_UP).doubleValue();
                view.setDifferenceRatio(convertValue((differenceRatio * 100)) + "%");
            }
            // 去年今年当月差额
            double monthTax = view.getMonthTax();
            double lastYearMonthTax = Objects.isNull(view.getLastYearLastMonthTax()) ? 0.0 : view.getLastYearLastMonthTax();
            double monthDifferential = monthTax - lastYearMonthTax;
            view.setMonthDifferential(HALF_UP(monthDifferential));
            // 当月增减率
            view.setMonthDifferenceRatio("0");
            if (monthTax > 0 && lastYearMonthTax > 0) {
                double monthDifferenceRatio = new BigDecimal((monthTax / lastYearMonthTax) - 1).setScale(2, RoundingMode.HALF_UP).doubleValue();
                view.setMonthDifferenceRatio(convertValue(monthDifferenceRatio * 100) + "%");
            }
        });
        // 今年至上月(当前月 -2)税收
        int beforeLastMonth = LocalDateTime.now().getMonth().getValue() - 2 > 0 ? LocalDateTime.now().getMonth().getValue() - 2 : 1;
        QueryWrapper<EnterpriseTaxPlus> query = new QueryWrapper<>();
        query.lambda().eq(EnterpriseTaxPlus::getYear, taxYear)
                .ge(EnterpriseTaxPlus::getMonth, 1)
                .le(EnterpriseTaxPlus::getMonth, beforeLastMonth)
                .ge(!Objects.isNull(foundStartDate), EnterpriseTaxPlus::getFoundDate, foundStartDate.toString())
                .le(!Objects.isNull(foundEndDate), EnterpriseTaxPlus::getFoundDate, foundEndDate.toString())
                .orderByDesc(EnterpriseTaxPlus::getPaymentDate);
        List<EnterpriseTaxPlus> beforeLastMonthTaxList = enterpriseTaxMapper.selectList(query);

        log.info("beforeLastMonthTaxList...{}", beforeLastMonthTaxList.size());
        List<NewEnterpriseTaxView> beforeLastMonthTaxViews = new ArrayList<>();
        newEnterpriseTaxYearData(beforeLastMonthTaxViews, beforeLastMonthTaxList, beforeLastMonth);
        // 组装
        for (NewEnterpriseTaxView view : viewList) {
            for (NewEnterpriseTaxView lastMonthData : beforeLastMonthTaxViews) {
                if (Objects.equals(view.getEnterpriseName(), lastMonthData.getEnterpriseName())) {
                    view.setCurrentYearLastMonthTax(lastMonthData.getCurrentYearLastMonthTax());
                    break;
                }
            }
        }
        return viewList;
    }

    private void newEnterpriseTaxYearData(List<NewEnterpriseTaxView> viewList, List<EnterpriseTaxPlus> yearData, int month) {
        int flag = viewList.size();
        Map<String, List<EnterpriseTaxPlus>> yearDataMap = new HashMap<>();
        yearData.stream().collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(yearDataMap::put);
        yearDataMap.forEach((key, value) -> {
            value = value.stream().sorted(Comparator.comparing(EnterpriseTaxPlus::getMonth)).collect(Collectors.toList());
            // 查询条件的税收开始月份到税收结束月份税收合计
            double totalTax = value.stream().mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            // 启缴日期
            Optional<EnterpriseTaxPlus> optional = value.stream().filter(enterpriseTax -> !Objects.isNull(enterpriseTax.getPaymentDate())).findFirst();
            String paymentDate = optional.isPresent() ? optional.get().getPaymentDate().toString().substring(0, 7) : "";
            // 今年查询的结束月份税收
            Optional<EnterpriseTaxPlus> first = value.stream().filter(v -> v.getMonth() == month).findFirst();
            double thisMonthTotalTax = first.isPresent() ? first.get().getTotalTax() : 0.0;
            if (flag == 0) {
                String process = value.get(0).getProcess();
                String foundDate = null;
                if (!Objects.isNull(value.get(0).getFoundDate())) foundDate = value.get(0).getFoundDate().toString();

                NewEnterpriseTaxView taxView = NewEnterpriseTaxView.builder()
                        .enterpriseName(key)
                        .enterpriseNo(value.get(0).getEnterpriseNo())
                        .enterpriseType(value.get(0).getEnterpriseType())
                        .process(process)
                        .registerNum(value.get(0).getRegisterNum())
                        .industry(value.get(0).getIndustry())
                        .capital(value.get(0).getCapital())
                        .foundDate(foundDate)
                        .paymentDate(paymentDate)
                        .monthTax(HALF_UP(thisMonthTotalTax))
                        .selectedMonthTax(HALF_UP(totalTax))
                        .registerAddress(value.get(0).getRegisterAddress())
                        .currentYearLastMonthTax(HALF_UP(totalTax))
                        .build();
                viewList.add(taxView);
            } else {
                viewList.forEach(view -> {
                    if (Objects.equals(view.getEnterpriseName(), key)) {
                        view.setLastYearLastMonthTax(HALF_UP(thisMonthTotalTax));
                        view.setLastYearSelectedMonthTax(HALF_UP(totalTax));
                    }
                });
            }
        });
    }

    @Override
    public List<EnterpriseTexTable> findDistrictInputSituation(ReportBaseRequest request) {
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();
        String industry = request.getIndustry();
        String economy = request.getEconomy();
//        int page = request.getPage();
//        if (page > 0)
//            page = page - 1;

//        IPage<EnterpriseTaxPlus> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page,request.getSize());

        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .eq(StringUtils.hasLength(industry), EnterpriseTaxPlus::getIndustry, industry)
                .eq(StringUtils.hasLength(economy), EnterpriseTaxPlus::getEnterpriseType, economy)
                .orderByDesc(EnterpriseTaxPlus::getYear)
                .orderByDesc(EnterpriseTaxPlus::getMonth);
        queryWrapper.select(
                "enterprise_no",
                "enterprise_name",
                "industry",
                "enterprise_type",
                "process",
                "this_region",
                "register_num",
                "found_date",
                "source",
                "capital",
                "register_address",
                "act_contact_address",
                "date",
                "month",
                "total_tax",
                "report_date",
                "payment_date"
        );
//        IPage<EnterpriseTaxPlus> iPage1 = enterpriseTaxMapper.selectPage(iPage, queryWrapper);
//        List<EnterpriseTaxPlus> records = iPage1.getRecords();
        List<EnterpriseTaxPlus> records = enterpriseTaxMapper.selectList(queryWrapper);
        List<EnterpriseTexTable> collect = records.stream().map(record -> {
            EnterpriseTexTable texTable = new EnterpriseTexTable();
            BeanUtils.copyProperties(record, texTable);
            if (Objects.isNull(record.getCapital()))
                record.setCapital(0.0);
            if (!Objects.isNull(record.getPaymentDate()))
                texTable.setPaymentDate(record.getPaymentDate().toString().substring(0, 7));
            texTable.setCapital(record.getCapital());
            return texTable;
        }).collect(Collectors.toList());

//        Pageable pageRequest = PageRequest.of(page,request.getSize());
//        Page<EnterpriseTexTable> viewPage = new PageImpl(collect, pageRequest, iPage1.getTotal());
        return collect;

    }

    /**
     * @param request 根据前端查询的起始月和结束月查询对应数据
     *                自动根据起始月结束月倒推去年的起始月结束月，并查询对应数据
     *                计算每个企业：
     *                1-今年的起始月结束月的税收；2-今年的起始月结束月的排名；3-去年的起始月结束月的税收；4-去年的起始月结束月的排名；
     *                5-今年与去年税收差额；6-累计增减率；7-今年当月排名；8-去年当月排名；9-今年当月税收；10-去年当月税收；
     *                11-去年今年当月差额；12-当月增减率；
     */
    @Override
    public List<TaxPayerComparisonView> findTaxPayerComparison(ReportBaseRequest request) {
        Integer year = request.getYear();
        Integer startMonth = request.getStartMonth();
        Integer endMonth = request.getEndMonth();
        String sortParam = request.getSort();
        Sort.Direction direction = request.getDirection();
        Integer ranking = request.getRanking();
//        int page = request.getPage();
//        if (page > 0)
//            page = page - 1;

        // 默认查询今年的第一月到当月时间
        if (Objects.isNull(year)) year = LocalDateTime.now().getYear();
        if (Objects.isNull(startMonth)) startMonth = LocalDateTime.now().getMonthValue();
        if (Objects.isNull(endMonth)) endMonth = LocalDateTime.now().getMonthValue();
        int lastYear = year - 1;
        if (Objects.isNull(ranking)) ranking = 100;
        if (Objects.isNull(direction)) direction = Sort.Direction.ASC;

//        IPage<EnterpriseTaxPlus> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(0,ranking);
        // 今年查询条件
        QueryWrapper<EnterpriseTaxPlus> currentYearWrapper = new QueryWrapper<>();
        currentYearWrapper.lambda().eq(EnterpriseTaxPlus::getYear, year)
                .ge(!Objects.isNull(startMonth), EnterpriseTaxPlus::getMonth, startMonth)
                .le(!Objects.isNull(endMonth), EnterpriseTaxPlus::getMonth, endMonth);
        if (!StringUtils.hasLength(sortParam)) sortParam = "total_tax_total";
        currentYearWrapper.orderBy(StringUtils.hasLength(sortParam), direction.isAscending(), sortParam);
        currentYearWrapper.select(
                "enterprise_no",
                "enterprise_name",
                "month",
                "industry",
                "total_tax"
        );
        // 去年查询条件
        QueryWrapper<EnterpriseTaxPlus> lastYearWrapper = new QueryWrapper<>();
        lastYearWrapper.lambda().eq(EnterpriseTaxPlus::getYear, lastYear)
                .ge(!Objects.isNull(startMonth), EnterpriseTaxPlus::getMonth, startMonth)
                .le(!Objects.isNull(endMonth), EnterpriseTaxPlus::getMonth, endMonth);
        if (!StringUtils.hasLength(sortParam)) sortParam = "enterprise_name";
        lastYearWrapper.orderBy(StringUtils.hasLength(sortParam), direction.isAscending(), sortParam);
        lastYearWrapper.select(
                "enterprise_no",
                "enterprise_name",
                "month",
                "industry",
                "total_tax"
        );
        List<EnterpriseTaxPlus> currentYearRecords = enterpriseTaxMapper.selectList(currentYearWrapper);
        List<EnterpriseTaxPlus> lastYearRecords = enterpriseTaxMapper.selectList(lastYearWrapper);
        log.info("thisYearDate...{}", currentYearRecords.size());
        log.info("lastYearDate...{}", lastYearRecords.size());

        List<TaxPayerComparisonView> viewList = new ArrayList<>();

        if (currentYearRecords.size() == 0) {
            return viewList;
        }
        // 计算 今年起始月结束月累计税收,当月税收
        getYearTax(viewList, currentYearRecords, endMonth);
        // 计算 去年起始月结束月累计税收,当月税收
        getYearTax(viewList, lastYearRecords, endMonth);
        // 今年/去年的起始月结束月的税收排名
        totalRanking(viewList);
        // 今年/去年当月排名
        monthRanking(viewList);
        // 去掉税金全为0的企业
        viewList = viewList.stream().filter(view -> view.getMonthTax() > 0 || view.getSelectedMonthTax() > 0 || view.getLastYearSelectedMonthTax() > 0 || view.getLastYearMonthTax() > 0).collect(Collectors.toList());

        // 今年与去年税收差异
        viewList.forEach(view -> {
            // 今年与去年税收累计差额 今年减去去年？
            double thisYearTotalTax = view.getSelectedMonthTax();
            double lastYearTotalTax = view.getLastYearSelectedMonthTax();
            double differential = thisYearTotalTax - lastYearTotalTax;
            view.setYearDifferential(HALF_UP(differential));
            // 累计增减率
            view.setDifferenceRatio("0");
            if (lastYearTotalTax > 0 && thisYearTotalTax > 0) {
                double differenceRatio = new BigDecimal((thisYearTotalTax / lastYearTotalTax) - 1).setScale(2, RoundingMode.HALF_UP).doubleValue();
                view.setDifferenceRatio(convertValue(differenceRatio * 100) + "%");
            }
            // 去年今年当月差额
            double monthTax = view.getMonthTax();
            double lastYearMonthTax = view.getLastYearMonthTax();
            double monthDifferential = monthTax - lastYearMonthTax;
            view.setMonthDifferential(HALF_UP(monthDifferential));
            // 当月增减率
            view.setMonthDifferenceRatio("0");
            if (monthTax > 0 && lastYearMonthTax > 0) {
                double monthDifferenceRatio = new BigDecimal((monthTax / lastYearMonthTax) - 1).setScale(2, RoundingMode.HALF_UP).doubleValue();
                view.setMonthDifferenceRatio((monthDifferenceRatio * 100) + "%");
            }
        });

        // 按累今年计税收和当月税收排序 截取排名数据
        if (!Objects.isNull(ranking))
            viewList = viewList.stream().sorted(Comparator.comparing(TaxPayerComparisonView::getSelectedMonthRanking).thenComparing(TaxPayerComparisonView::getMonthRanking)).skip(0).limit(ranking).collect(Collectors.toList());
        viewList = viewList.stream().sorted(Comparator.comparing(TaxPayerComparisonView::getSelectedMonthRanking)).collect(Collectors.toList());

        log.info("result...{}", viewList.size());
        return viewList;
    }

    private void monthRanking(List<TaxPayerComparisonView> data) {
        data = data.stream().sorted(Comparator.comparing(TaxPayerComparisonView::getMonthTax).reversed()).collect(Collectors.toList());
        for (int i = 0; i < data.size(); i++) {
            TaxPayerComparisonView view = data.get(i);
            view.setMonthRanking(i + 1);
        }
        data = data.stream().sorted(Comparator.comparing(TaxPayerComparisonView::getLastYearMonthTax).reversed()).collect(Collectors.toList());
        for (int j = 0; j < data.size(); j++) {
            TaxPayerComparisonView view = data.get(j);
            view.setLastYearMonthRanking(j + 1);
        }
    }

    private void totalRanking(List<TaxPayerComparisonView> data) {
        data = data.stream().sorted(Comparator.comparing(TaxPayerComparisonView::getSelectedMonthTax).reversed()).collect(Collectors.toList());
        for (int i = 0; i < data.size(); i++) {
            TaxPayerComparisonView view = data.get(i);
            view.setSelectedMonthRanking(i + 1);
        }
        data = data.stream().sorted(Comparator.comparing(TaxPayerComparisonView::getLastYearSelectedMonthTax).reversed()).collect(Collectors.toList());
        for (int j = 0; j < data.size(); j++) {
            TaxPayerComparisonView view = data.get(j);
            view.setLastYearSelectedMonthRanking(j + 1);
        }
    }

    private void getYearTax(List<TaxPayerComparisonView> viewList, List<EnterpriseTaxPlus> yearData, int month) {
        int flag = viewList.size();
        Map<String, List<EnterpriseTaxPlus>> yearDataMap = new HashMap<>();
        yearData.stream().collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(yearDataMap::put);
        yearDataMap.forEach((key, value) -> {
            // 今年起始月结束月累计税收
            double totalTax = value.stream().mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            // 今年当月税收
            Optional<EnterpriseTaxPlus> first = value.stream().filter(v -> month == v.getMonth()).findFirst();
            double thisMonthTotalTax = first.isPresent() ? first.get().getTotalTax() : 0;
            if (flag == 0) {
                TaxPayerComparisonView view = TaxPayerComparisonView.builder()
                        .enterpriseNo(value.get(0).getEnterpriseNo())
                        .enterpriseName(key)
                        .industry(value.get(0).getIndustry())
                        .selectedMonthTax(HALF_UP(totalTax))
                        .lastYearSelectedMonthTax(0.0)
                        .monthTax(HALF_UP(thisMonthTotalTax))
                        .lastYearMonthTax(0.0)
                        .build();
                viewList.add(view);
            } else {
                viewList.forEach(view -> {
                    if (Objects.equals(view.getEnterpriseName(), key)) {
                        view.setLastYearSelectedMonthTax(HALF_UP(totalTax));
                        view.setLastYearMonthTax(HALF_UP(thisMonthTotalTax));
                    }
                });
            }
        });
    }

    @Override
    public List<MonthlyMainIndicatorView> findMonthlyMainIndicator(ReportBaseRequest request) {
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();
        String industry = request.getIndustry();
        String economy = request.getEconomy();
//        int page = request.getPage();
//        if (page > 0)
//            page = page - 1;

//        IPage<EnterpriseTaxPlus> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page,request.getSize());
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .eq(StringUtils.hasLength(industry), EnterpriseTaxPlus::getIndustry, industry)
                .eq(StringUtils.hasLength(economy), EnterpriseTaxPlus::getEnterpriseType, economy)
                .gt(EnterpriseTaxPlus::getTotalTax, 0);
        queryWrapper.select(
                "enterprise_no",
                "enterprise_name",
                "date",
                "industry",
                "enterprise_type",
                "sales",
                "total_tax"
        );
//        IPage<EnterpriseTaxPlus> iPage1 = enterpriseTaxMapper.selectPage(iPage, queryWrapper);
//        List<EnterpriseTaxPlus> records = iPage1.getRecords();
        List<EnterpriseTaxPlus> records = enterpriseTaxMapper.selectList(queryWrapper);
        List<MonthlyMainIndicatorView> viewList = new ArrayList<>();
        // 按企业分组
        Map<String, List<EnterpriseTaxPlus>> groupResult = Maps.newHashMap();
        records.stream().collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(groupResult::put);
        String currentMonth = startDate;
        for (String key : groupResult.keySet()) {
            List<EnterpriseTaxPlus> values = groupResult.get(key);
            // 累计
            double salesTotal = values.stream().mapToDouble(EnterpriseTaxPlus::getSales).sum();
            double totalTax = values.stream().mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            // 当月
            Optional<EnterpriseTaxPlus> first = values.stream().filter(enterpriseTax -> Objects.equals(currentMonth, enterpriseTax.getDate())).findFirst();
            double currentMonthTax = first.isPresent() ? first.get().getTotalTax() : 0;
            double currentSales = first.isPresent() ? first.get().getSales() : 0;
            MonthlyMainIndicatorView view = new MonthlyMainIndicatorView();
            view.setEnterpriseName(key);
            view.setEnterpriseNo(values.get(0).getEnterpriseNo());
            view.setEnterpriseType(values.get(0).getEnterpriseType());
            view.setIndustry(values.get(0).getIndustry());
            view.setSales(HALF_UP(currentSales));
            view.setTotalTax(HALF_UP(currentMonthTax));
            view.setSalesTotal(HALF_UP(salesTotal));
            view.setTotalTaxTotal(convertValue(totalTax));
            view.setTotalTaxTotalValue(totalTax);
            viewList.add(view);
        }
        log.info("viewList...{}", viewList.size());
        viewList = viewList.stream().sorted(Comparator.comparing(MonthlyMainIndicatorView::getTotalTaxTotalValue).reversed()).collect(Collectors.toList());
//        Pageable pageRequest = PageRequest.of(page,request.getSize());
//        Page<MonthlyMainIndicatorView> viewPage = new PageImpl(viewList, pageRequest, iPage1.getTotal());
        return viewList;
    }

    @Override
    public List<TaxRankingView> findTaxRankingList(ReportBaseRequest request) {
        Integer ranking = request.getRanking();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();
        if (Objects.isNull(ranking)) ranking = 100;
        // 根据排名分页,暂定根据累计税金升序
//        IPage<EnterpriseTaxPlus> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(0,ranking);
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .isNotNull(EnterpriseTaxPlus::getTotalTax)
                .gt(EnterpriseTaxPlus::getTotalTax, 0);
//        IPage<EnterpriseTaxPlus> iPage1 = enterpriseTaxMapper.selectPage(iPage, queryWrapper);
//        List<EnterpriseTaxPlus> records = iPage1.getRecords();
        List<EnterpriseTaxPlus> records = enterpriseTaxMapper.selectList(queryWrapper);
        // 按企业分组
        Map<String, List<EnterpriseTaxPlus>> groupResult = Maps.newHashMap();
        records.stream().collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(groupResult::put);

        // 组装当月和累计信息
        String currentMonth = startDate;
        List<TaxRankingView> taxRankingViews = new ArrayList<>();
        groupResult.forEach((key, values) -> {
            // 当月
//            EnterpriseTaxPlus enterpriseTaxPlus = values.stream().filter(v -> Objects.equals(currentMonth, v.getDate())).findFirst().get();
            Optional<EnterpriseTaxPlus> first = values.stream().filter(v -> Objects.equals(currentMonth, v.getDate())).findFirst();
            EnterpriseTaxPlus enterpriseTaxPlus = first.isPresent() ? first.get() : null;
            if (Objects.isNull(enterpriseTaxPlus)) {
                enterpriseTaxPlus = new EnterpriseTaxPlus();
                enterpriseTaxPlus.setDate(currentMonth);
            }
            TaxRankingView view = TaxRankingView.builder()
                    .enterpriseName(values.get(0).getEnterpriseName())
                    .totalTax(HALF_UP(enterpriseTaxPlus.getTotalTax()))
                    .totalTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getTotalTaxTotal).sum()))
                    .addedTax(HALF_UP(enterpriseTaxPlus.getAddedTax()))
                    .addedTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getAddedTax).sum()))
                    .toAddedTax(HALF_UP(enterpriseTaxPlus.getToAddedTax()))
                    .toAddedTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getToAddedTaxTotal).sum()))
                    .incomeTax(HALF_UP(enterpriseTaxPlus.getIncomeTax()))
                    .incomeTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getIncomeTaxTotal).sum()))
                    .cityTax(HALF_UP(enterpriseTaxPlus.getCityTax()))
                    .cityTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getCityTaxTotal).sum()))
                    .personTax(HALF_UP(enterpriseTaxPlus.getPersonTax()))
                    .personTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getPersonTaxTotal).sum()))
                    .excise(HALF_UP(enterpriseTaxPlus.getExcise()))
                    .exciseTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getExciseTotal).sum()))
                    .addedLandTax(HALF_UP(enterpriseTaxPlus.getAddedLandTax()))
                    .addedLandTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getAddedLandTaxTotal).sum()))
                    .stampTax(HALF_UP(enterpriseTaxPlus.getStampTax()))
                    .stampTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getStampTaxTotal).sum()))
                    .houseTax(HALF_UP(enterpriseTaxPlus.getHouseTax()))
                    .houseTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getHouseTaxTotal).sum()))
                    .landTax(HALF_UP(enterpriseTaxPlus.getLandTax()))
                    .landTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getLandTaxTotal).sum()))
                    .carTax(HALF_UP(enterpriseTaxPlus.getCarTax()))
                    .carTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getCarTaxTotal).sum()))
                    .carPurchaseTax(HALF_UP(enterpriseTaxPlus.getCarPurchaseTax()))
                    .carPurchaseTaxTotal(HALF_UP(values.stream().mapToDouble(EnterpriseTaxPlus::getCarPurchaseTaxTotal).sum()))
                    .build();
            taxRankingViews.add(view);
        });

        List<TaxRankingView> viewList = taxRankingViews.stream().sorted(Comparator.comparing(TaxRankingView::getTotalTax).reversed()).collect(Collectors.toList());

        return viewList.stream().skip(0).limit(ranking).collect(Collectors.toList());
    }

    @Override
    public List<RegionIncomeView> findRegionIncomeList(ReportBaseRequest request) {
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        String industry = request.getIndustry();
        String economy = request.getEconomy();
        String queryString = request.getQueryString();
//        int page = request.getPage();
//        if (page > 0)
//            page = page - 1;
//        IPage<EnterpriseTaxPlus> pageable = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page,request.getSize());
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(StringUtils.hasLength(queryString), EnterpriseTaxPlus::getEnterpriseName, queryString)
                .eq(StringUtils.hasLength(industry), EnterpriseTaxPlus::getIndustry, industry)
                .eq(StringUtils.hasLength(economy), EnterpriseTaxPlus::getEnterpriseType, economy)
                .between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .gt(EnterpriseTaxPlus::getTotalTax, 0)
                .groupBy(EnterpriseTaxPlus::getEnterpriseName)
                .orderByDesc(EnterpriseTaxPlus::getEnterpriseName)
                .orderByDesc(EnterpriseTaxPlus::getDate);
        queryWrapper.select(
                "garden",
                "enterprise_no",
                "enterprise_name",
                "industry",
                "found_date",
                "sum(added_tax) as addedTax",
                "sum(land_tax) as landTax",
                "sum(excise) as excise",
                "sum(car_tax) as carTax",
                "sum(to_added_tax) as toAddedTax",
                "sum(income_tax) as incomeTax",
                "sum(person_tax) as personTax",
                "sum(city_tax) as cityTax",
                "sum(added_land_tax) as addedLandTax",
                "sum(house_tax) as houseTax",
                "sum(stamp_tax) as stampTax"
        );
//        IPage<EnterpriseTaxPlus> all = enterpriseTaxMapper.selectPage(pageable, queryWrapper);
//        List<EnterpriseTaxPlus> contentList = all.getRecords();
        List<EnterpriseTaxPlus> contentList = enterpriseTaxMapper.selectList(queryWrapper);
        // 计算转换
        List<RegionIncomeView> regionIncomeViews = contentList.stream().map(content -> {
            String districtIncomeTotal = convertValue(
                    content.getAddedTax() * 0.325 +
                            content.getAddedLandTax() * 0.8 +
                            content.getPersonTax() * 0.22 +
                            content.getIncomeTax() * 0.2 +
                            content.getCityTax() * 0.65 +
                            content.getAddedLandTax() * 0.32 +
                            content.getHouseTax() * 0.8 +
                            content.getStampTax());
            Double otherTax = content.getExcise() + content.getLandTax() + content.getCarTax();
            String threeTaxCombined = convertValue(
                    content.getAddedTax() +
                            content.getToAddedTax() +
                            content.getPersonTax() +
                            content.getIncomeTax() +
                            content.getCityTax() +
                            content.getAddedLandTax() +
                            content.getHouseTax() +
                            content.getStampTax() + otherTax);
            RegionIncomeView regionIncomeView = RegionIncomeView.builder()
                    .garden(content.getGarden())
                    .enterpriseNo(content.getEnterpriseNo())
                    .enterpriseName(content.getEnterpriseName())
                    .industry(content.getIndustry())
                    .foundDate(Objects.isNull(content.getFoundDate()) ? null : content.getFoundDate().toString())
                    .addedTax(convertValue(content.getAddedTax()))
                    .toAddedTax(convertValue(content.getToAddedTax()))
                    .incomeTax(convertValue(content.getIncomeTax()))
                    .personTax(convertValue(content.getPersonTax()))
                    .cityTax(convertValue(content.getCityTax()))
                    .addedLandTax(convertValue(content.getAddedLandTax()))
                    .houseTax(convertValue(content.getHouseTax()))
                    .stampTax(convertValue(content.getStampTax()))
                    .build();
            regionIncomeView.setThreeTaxCombined((threeTaxCombined));
            regionIncomeView.setDistrictAddedTax(convertValue(content.getAddedTax() * 0.325));
            regionIncomeView.setDistrictToAddedTax(convertValue(content.getToAddedTax() * 0.8));
            regionIncomeView.setDistrictPersonTax(convertValue(content.getPersonTax() * 0.22));
            regionIncomeView.setDistrictIncomeTax(convertValue(content.getIncomeTax() * 0.2));
            regionIncomeView.setDistrictCityTax(convertValue(content.getCityTax() * 0.65));
            regionIncomeView.setDistrictAddedLandTax(convertValue(content.getAddedLandTax() * 0.32));
            regionIncomeView.setDistrictHouseTax(convertValue(content.getHouseTax() * 0.8));
            regionIncomeView.setDistrictStampTax(convertValue(content.getStampTax()));
            regionIncomeView.setOtherTax(convertValue(otherTax));
            regionIncomeView.setDistrictIncomeTotal(districtIncomeTotal);
            return regionIncomeView;
        }).collect(Collectors.toList());
//        Pageable pageRequest = PageRequest.of(page,request.getSize());
//        Page<RegionIncomeView> viewPage = new PageImpl(regionIncomeViews, pageRequest, all.getTotal());
        return regionIncomeViews;
    }

    private String convertValue(double value) {
        if (Objects.isNull(value)) return "0";
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private double HALF_UP(double value) {
        if (Objects.isNull(value)) return 0;
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public void exportTaxMarginRanking(ReportBaseRequest request, HttpServletResponse response) {
        if (request.getYear() == null || request.getStartMonth() == null || request.getStartMonth() == null) {
            throw new GlobalException(ResultCodeEnum.INVALID_PARAM);
        }
        Integer year = request.getYear();
        Integer startMonth = request.getStartMonth();
        Integer endMonth = request.getEndMonth();
        Integer ranking = request.getRanking();

//        String scope = request.getScope(); // 当月 累计
//        if (!StringUtils.hasLength(scope)) scope = "当月";
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getYear, year - 1, year)
                .ge(EnterpriseTaxPlus::getMonth, startMonth)
                .le(EnterpriseTaxPlus::getMonth, endMonth)
                .gt(EnterpriseTaxPlus::getTotalTax, 0);
        List<EnterpriseTaxPlus> results = enterpriseTaxMapper.selectList(queryWrapper);
        //符合条件的税收目标企业群体
        Map<String, List<EnterpriseTaxPlus>> groupResult = Maps.newHashMap();
        results.stream().filter(enterpriseTax ->
                !Objects.isNull(enterpriseTax.getTotalTax()) && StringUtils.hasLength(enterpriseTax.getEnterpriseName())
        ).collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(groupResult::put);
        //今年相比去年的税收幅度
//        Map<String,Double> resultMap = Maps.newHashMap();
//        for (String enterpriseName : groupResult.keySet()) {
//            List<EnterpriseTaxPlus> taxList = groupResult.get(enterpriseName);
//            double currentYearSum = taxList.stream().filter(enterpriseTax -> year == enterpriseTax.getYear())
//                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
//            double lastYearSum = taxList.stream().filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
//                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
//            resultMap.put(enterpriseName,currentYearSum - lastYearSum);
//        }
//        List<String> enterpriseTops;
//        Stream<Map.Entry<String, Double>> stream = resultMap.entrySet().stream();
//        if (Sort.Direction.ASC.equals(request.getDirection())) {
//            enterpriseTops = stream
//                    .sorted(Comparator.comparing(Map.Entry::getValue))
//                    .limit(request.getRanking() == null ? 20 : request.getRanking())
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.toList());
//        }else {
//            enterpriseTops = stream
//                    .sorted(Collections.reverseOrder(Comparator.comparing(Map.Entry::getValue)))
//                    .limit(request.getRanking() == null ? 20 : request.getRanking())
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.toList());
//        }
//        groupResult.entrySet().removeIf(entry -> !enterpriseTops.contains(entry.getKey()));

        List<TaxMarginRankingView> list = new ArrayList<>();
        groupResult.forEach((key, values) -> {
            double currentYearSum = values.stream().filter(enterpriseTax -> Objects.equals(year, enterpriseTax.getYear()))
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            double lastYearSum = values.stream().filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            TaxMarginRankingView view = TaxMarginRankingView.builder()
                    .enterpriseNo(values.get(0).getEnterpriseNo())
                    .enterpriseName(key)
                    .currentYearTax(convertValue(currentYearSum))
                    .currentYearTaxValue(currentYearSum)
                    .lastYearTax(convertValue(lastYearSum))
                    .differential(convertValue(currentYearSum - lastYearSum))
                    .build();
            list.add(view);
        });
        List<TaxMarginRankingView> viewList = list.stream().sorted(
                Comparator.comparing(TaxMarginRankingView::getCurrentYearTaxValue).reversed()
        ).collect(Collectors.toList());
        viewList = viewList.stream().skip(0).limit(ranking).collect(Collectors.toList());

        int width = 15;
        if (!viewList.isEmpty()) {
            String enterpriseName = viewList.stream().max(Comparator.comparingInt((TaxMarginRankingView p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width = enterpriseName.getBytes(StandardCharsets.UTF_8).length * 165;
        }

        //获取最终符合条件的公司数据
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        // 表头居中
        HSSFRow title = sheet.createRow(0);
        HSSFCell cell = title.createCell(0);
        HSSFCellStyle titleCellStyle = cell.getCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(titleCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        // 2021年1~8月税收增幅减幅排名前100
        String name = year + "年" + startMonth + "~" + endMonth + "月税收增幅减幅排名表";
        cell.setCellValue(name);
        HSSFRow row = sheet.createRow(1);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("企业名称");
        if (startMonth == endMonth) {
            row.createCell(2).setCellValue(year + "年" + startMonth + "月税收");
            row.createCell(3).setCellValue(year - 1 + "年" + startMonth + "月税收");
        } else {
            row.createCell(2).setCellValue(year + "年" + "1~" + startMonth + "月税收");
            row.createCell(3).setCellValue(year - 1 + "年" + "1~" + endMonth + "月税收");
        }
        row.createCell(4).setCellValue("增减幅（万元）");
        int rowIndex = 2;
        for (int i = 0; i < viewList.size(); i++) {
            row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(viewList.get(i).getEnterpriseName());
            row.createCell(2).setCellValue(viewList.get(i).getCurrentYearTax());
            row.createCell(3).setCellValue(viewList.get(i).getLastYearTax());
            row.createCell(4).setCellValue(viewList.get(i).getDifferential());
            rowIndex += 1;
        }
        sheet.setColumnWidth(1, width);
        sheet.setColumnWidth(2, 4096);
        sheet.setColumnWidth(3, 4096);
        sheet.setColumnWidth(4, 4096);
        OutputStream os = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(year + "年" + startMonth + "至" + endMonth + "月税收增幅减幅排名表", "utf-8") + ".xls");
            os = response.getOutputStream();
            wb.write(os);
            os.flush();
        } catch (Exception e) {
            log.error("ERROR-OCCURRED {}", e.getMessage());
        } finally {
            try {
                wb.close();
                if (os != null) {
                    IOUtils.closeQuietly(os);
                }
            } catch (IOException e) {
                log.error("ERROR-OCCURRED {}", e.getMessage());
            }
        }
    }

    @Override
    public void exportTaxMarginComparison(ReportBaseRequest request, HttpServletResponse response) {
        if (request.getYear() == null) {
            throw new GlobalException(ResultCodeEnum.INVALID_PARAM);
        }
        int year = request.getYear();
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getYear, year - 2, year);
        List<EnterpriseTaxPlus> results = enterpriseTaxMapper.selectList(queryWrapper);
        //符合条件的税收目标企业群体
        Map<String, List<EnterpriseTaxPlus>> groupResult = Maps.newHashMap();
        //当年税收总额
        Map<String, Double> currentYearTaxMap = Maps.newHashMap();
        //去年税收总额
        Map<String, Double> lastYearTaxMap = Maps.newHashMap();
        //前年税收总额
        Map<String, Double> theYearBeforeLastTaxMap = Maps.newHashMap();
        results.stream().filter(
                enterpriseTax -> !Objects.isNull(enterpriseTax.getTotalTax()) && StringUtils.hasLength(enterpriseTax.getEnterpriseName())
        ).collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(groupResult::put);
        groupResult.entrySet().removeIf(entry -> {
            String enterpriseName = entry.getKey();
            List<EnterpriseTaxPlus> taxList = entry.getValue();
            double theYearBeforeLastSum = taxList.stream().filter(enterpriseTax -> year - 2 == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            double lastYearSum = taxList.stream().filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            double currentYearSum = taxList.stream().filter(enterpriseTax -> year == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            if (lastYearSum - theYearBeforeLastSum <= request.getTaxIndices() * 10000 || currentYearSum - lastYearSum <= request.getTaxIndices() * 10000) {
                return true;
            }
            currentYearTaxMap.put(enterpriseName, currentYearSum);
            lastYearTaxMap.put(enterpriseName, lastYearSum);
            theYearBeforeLastTaxMap.put(enterpriseName, theYearBeforeLastSum);
            return false;
        });

        int width = 15;
        if (!results.isEmpty()) {
            String enterpriseName = results.stream().max(Comparator.comparingInt((EnterpriseTaxPlus p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width = enterpriseName.getBytes(StandardCharsets.UTF_8).length * 165;
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        // 表头居中
        HSSFRow title = sheet.createRow(0);
        HSSFCell cell = title.createCell(0);
        HSSFCellStyle titleCellStyle = cell.getCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(titleCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        // 2021年1~8月税收增幅减幅排名前100
        String name = "三年同比振幅大于" + request.getTaxIndices() * 10000 + "万";
        cell.setCellValue(name);
        HSSFRow row = sheet.createRow(1);
//        row.createCell(0).setCellValue("序号");
        row.createCell(0).setCellValue("企业名称");
        row.createCell(1).setCellValue(year);
        row.createCell(2).setCellValue(year - 1);
        row.createCell(3).setCellValue(year - 2);
        row.createCell(4).setCellValue(year + "减去" + (year - 1));
        row.createCell(5).setCellValue(year - 1 + "减去" + (year - 2));
        row.createCell(6).setCellValue(year + "减去" + (year - 2));
        int rowIndex = 2;
        for (String enterpriseName : groupResult.keySet()) {
            row = sheet.createRow(rowIndex);
//            row.createCell(0).setCellValue(i+1);
            row.createCell(0).setCellValue(enterpriseName);
            row.createCell(1).setCellValue(convertValue(currentYearTaxMap.get(enterpriseName)));
            row.createCell(2).setCellValue(convertValue(lastYearTaxMap.get(enterpriseName)));
            row.createCell(3).setCellValue(convertValue(theYearBeforeLastTaxMap.get(enterpriseName)));
            row.createCell(4).setCellValue(convertValue(currentYearTaxMap.get(enterpriseName) - lastYearTaxMap.get(enterpriseName)));
            row.createCell(5).setCellValue(convertValue(lastYearTaxMap.get(enterpriseName) - theYearBeforeLastTaxMap.get(enterpriseName)));
            row.createCell(6).setCellValue(convertValue(currentYearTaxMap.get(enterpriseName) - theYearBeforeLastTaxMap.get(enterpriseName)));
            rowIndex += 1;
        }
        sheet.setColumnWidth(0, width);
        sheet.setColumnWidth(1, 4096);
        sheet.setColumnWidth(2, 4096);
        sheet.setColumnWidth(3, 4096);
        sheet.setColumnWidth(4, 4096);
        sheet.setColumnWidth(5, 4096);
        sheet.setColumnWidth(6, 4096);
        OutputStream os = null;
        try {
//            File file = new File("/data/attached/material/税收幅度排名表.xls");
//            if(!file.getParentFile().exists()) {
//                file.mkdirs();
//            }
//            file.createNewFile();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "utf-8") + ".xls");
            os = response.getOutputStream();
            wb.write(os);
            os.flush();
        } catch (Exception e) {
            log.error("ERROR-OCCURRED {}", e.getMessage());
        } finally {
            try {
                wb.close();
                if (os != null) {
                    IOUtils.closeQuietly(os);
                }
            } catch (IOException e) {
                log.error("ERROR-OCCURRED {}", e.getMessage());
            }
        }
    }

    @Override
    public void exportTaxDetailComparison(ReportBaseRequest request, HttpServletResponse response) {
        if (request.getYear() == null) {
            throw new GlobalException(ResultCodeEnum.INVALID_PARAM);
        }
        String queryString = request.getQueryString();
        List<EnterpriseTaxPlus> results = new ArrayList<>();
        int year = request.getYear();
        if (StringUtils.hasLength(queryString)) {
            QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().like(EnterpriseTaxPlus::getEnterpriseName, queryString)
                    .between(EnterpriseTaxPlus::getYear, year - 1, year);
            results = enterpriseTaxMapper.selectList(queryWrapper);
        }

        int width = 15;
        if (!results.isEmpty()) {
            String enterpriseName = results.stream().max(Comparator.comparingInt((EnterpriseTaxPlus p) -> p.getEnterpriseName().length())).get().getEnterpriseName();
            width = enterpriseName.getBytes(StandardCharsets.UTF_8).length * 165;
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow title = sheet.createRow(0);
        HSSFCell cell = title.createCell(0);
        // 表头居中
        HSSFCellStyle titleCellStyle = cell.getCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(titleCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        sheet.setColumnWidth(0, width);
        sheet.setColumnWidth(1, 4096);
        sheet.setColumnWidth(2, 4096);
        sheet.setColumnWidth(3, 4096);
        sheet.setColumnWidth(4, 4096);
        sheet.setColumnWidth(5, 4096);
        sheet.setColumnWidth(6, 4096);
        sheet.setColumnWidth(7, 4096);
        sheet.setColumnWidth(8, 4096);
        sheet.setColumnWidth(9, 4096);
        sheet.setColumnWidth(10, 4096);
        cell.setCellValue(year + "年" + queryString + "税收同比明细");

        HSSFRow row = sheet.createRow(1);
        row.createCell(0).setCellValue("企业名称");
        row.createCell(1).setCellValue("年月");
        row.createCell(2).setCellValue("增值税");
        row.createCell(3).setCellValue("城建税");
        row.createCell(4).setCellValue("企业所得税");
        row.createCell(5).setCellValue("个人所得税");
        row.createCell(6).setCellValue("消费税");
        row.createCell(7).setCellValue("土地增值税");
        row.createCell(8).setCellValue("房产税");
        row.createCell(9).setCellValue("印花税");
        row.createCell(10).setCellValue("税金合计");
        int rowIndex = 2;
        if (!CollectionUtils.isEmpty(results)) {
            for (int i = 0; i < 12; i++) {
                int tmpMonth = i + 1;
                List<EnterpriseTaxPlus> currentMonthResult = results.stream()
                        .filter(enterpriseTax ->
                                enterpriseTax.getMonth() != null
                                        && tmpMonth == enterpriseTax.getMonth())
                        .collect(Collectors.toList());
                if (currentMonthResult.size() > 0) {
                    BigDecimal currentYearAddedTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearAddedTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearCityTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getCityTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getCityTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearCityTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getCityTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getCityTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearIncomeTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getIncomeTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getIncomeTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearIncomeTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getIncomeTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getIncomeTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearPersonTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getPersonTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getPersonTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearPersonTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getPersonTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getPersonTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearExciseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getExcise()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getExcise()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearExciseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getExcise()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getExcise()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearAddedLandTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedLandTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedLandTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearAddedLandTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedLandTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedLandTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearHouseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getHouseTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getHouseTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearHouseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getHouseTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getHouseTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearStampTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getStampTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getStampTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearStampTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getStampTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getStampTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    row = sheet.createRow(rowIndex);
                    row.createCell(0).setCellValue(currentMonthResult.get(0).getEnterpriseName());
                    row.createCell(1).setCellValue(year + "-" + (tmpMonth < 10 ? "0" + tmpMonth : tmpMonth));
                    row.createCell(2).setCellValue(convertValue(currentYearAddedTax.doubleValue()));
                    row.createCell(3).setCellValue(convertValue(currentYearCityTax.doubleValue()));
                    row.createCell(4).setCellValue(convertValue(currentYearIncomeTax.doubleValue()));
                    row.createCell(5).setCellValue(convertValue(currentYearPersonTax.doubleValue()));
                    row.createCell(6).setCellValue(convertValue(currentYearExciseTax.doubleValue()));
                    row.createCell(7).setCellValue(convertValue(currentYearAddedLandTax.doubleValue()));
                    row.createCell(8).setCellValue(convertValue(currentYearHouseTax.doubleValue()));
                    row.createCell(9).setCellValue(convertValue(currentYearStampTax.doubleValue()));
                    row.createCell(10).setCellValue(convertValue(currentYearAddedTax
                            .add(currentYearCityTax
                                    .add(currentYearIncomeTax
                                            .add(currentYearPersonTax
                                                    .add(currentYearExciseTax
                                                            .add(currentYearAddedLandTax
                                                                    .add(currentYearHouseTax
                                                                            .add(currentYearStampTax))))))).doubleValue()));
                    row = sheet.createRow(++rowIndex);
                    row.createCell(0).setCellValue(currentMonthResult.get(0).getEnterpriseName());
                    row.createCell(1).setCellValue(year - 1 + "-" + (tmpMonth < 10 ? "0" + tmpMonth : tmpMonth));
                    row.createCell(2).setCellValue(convertValue(lastYearAddedTax.doubleValue()));
                    row.createCell(3).setCellValue(convertValue(lastYearCityTax.doubleValue()));
                    row.createCell(4).setCellValue(convertValue(lastYearIncomeTax.doubleValue()));
                    row.createCell(5).setCellValue(convertValue(lastYearPersonTax.doubleValue()));
                    row.createCell(6).setCellValue(convertValue(lastYearExciseTax.doubleValue()));
                    row.createCell(7).setCellValue(convertValue(lastYearAddedLandTax.doubleValue()));
                    row.createCell(8).setCellValue(convertValue(lastYearHouseTax.doubleValue()));
                    row.createCell(9).setCellValue(convertValue(lastYearStampTax.doubleValue()));
                    row.createCell(10).setCellValue(convertValue(lastYearAddedTax
                            .add(lastYearCityTax
                                    .add(lastYearIncomeTax
                                            .add(lastYearPersonTax
                                                    .add(lastYearExciseTax
                                                            .add(lastYearAddedLandTax
                                                                    .add(lastYearHouseTax
                                                                            .add(lastYearStampTax))))))).doubleValue()));
                    row = sheet.createRow(++rowIndex);
                    BigDecimal var_1 = ratio(currentYearAddedTax, lastYearAddedTax);
                    BigDecimal var_2 = ratio(currentYearCityTax, lastYearCityTax);
                    BigDecimal var_3 = ratio(currentYearIncomeTax, lastYearIncomeTax);
                    BigDecimal var_4 = ratio(currentYearPersonTax, lastYearPersonTax);
                    BigDecimal var_5 = ratio(currentYearExciseTax, lastYearExciseTax);
                    BigDecimal var_6 = ratio(currentYearAddedLandTax, lastYearAddedLandTax);
                    BigDecimal var_7 = ratio(currentYearHouseTax, lastYearHouseTax);
                    BigDecimal var_8 = ratio(currentYearStampTax, lastYearStampTax);
//                BigDecimal var_1 = currentYearAddedTax.divide(lastYearAddedTax).subtract(new BigDecimal("1"));
//                BigDecimal var_2 = currentYearCityTax.divide(lastYearCityTax).subtract(new BigDecimal("1"));
//                BigDecimal var_3 = currentYearIncomeTax.divide(lastYearIncomeTax).subtract(new BigDecimal("1"));
//                BigDecimal var_4 = currentYearPersonTax.divide(lastYearPersonTax).subtract(new BigDecimal("1"));
//                BigDecimal var_5 = currentYearExciseTax.divide(lastYearExciseTax).subtract(new BigDecimal("1"));
//                BigDecimal var_6 = currentYearAddedLandTax.divide(lastYearAddedLandTax).subtract(new BigDecimal("1"));
//                BigDecimal var_7 = currentYearHouseTax.divide(lastYearHouseTax).subtract(new BigDecimal("1"));
//                BigDecimal var_8 = currentYearStampTax.divide(lastYearStampTax).subtract(new BigDecimal("1"));
//                if (var_1.compareTo(new BigDecimal(0)) != 0 && var_1.compareTo(lastYearAddedTax) == 0) {
//                    var_1 = new BigDecimal(-100);
//                }
//                if (var_1.compareTo(new BigDecimal(0)) != 0 && var_1.compareTo(currentYearAddedTax) == 0) {
//                    var_1 = new BigDecimal(100);
//                }
//                if (var_2.compareTo(new BigDecimal(0)) != 0 && var_2.compareTo(lastYearCityTax) == 0) {
//                    var_2 = new BigDecimal(-100);
//                }
//                if (var_2.compareTo(new BigDecimal(0)) != 0 && var_2.compareTo(currentYearCityTax) == 0) {
//                    var_2 = new BigDecimal(100);
//                }
//                if (var_3.compareTo(new BigDecimal(0)) != 0 && var_3.compareTo(lastYearIncomeTax) == 0) {
//                    var_3 = new BigDecimal(-100);
//                }
//                if (var_3.compareTo(new BigDecimal(0)) != 0 && var_3.compareTo(currentYearIncomeTax) == 0) {
//                    var_3 = new BigDecimal(100);
//                }
//                if (var_4.compareTo(new BigDecimal(0)) != 0 && var_4.compareTo(lastYearPersonTax) == 0) {
//                    var_4 = new BigDecimal(-100);
//                }
//                if (var_4.compareTo(new BigDecimal(0)) != 0 && var_4.compareTo(currentYearPersonTax) == 0) {
//                    var_4 = new BigDecimal(100);
//                }
//                if (var_5.compareTo(new BigDecimal(0)) != 0 && var_5.compareTo(lastYearExciseTax) == 0) {
//                    var_5 = new BigDecimal(-100);
//                }
//                if (var_5.compareTo(new BigDecimal(0)) != 0 && var_5.compareTo(currentYearExciseTax) == 0) {
//                    var_5 = new BigDecimal(100);
//                }
//                if (var_6.compareTo(new BigDecimal(0)) != 0 && var_6.compareTo(lastYearAddedLandTax) == 0) {
//                    var_6 = new BigDecimal(-100);
//                }
//                if (var_6.compareTo(new BigDecimal(0)) != 0 && var_6.compareTo(currentYearAddedLandTax) == 0) {
//                    var_6 = new BigDecimal(100);
//                }
//                if (var_7.compareTo(new BigDecimal(0)) != 0 && var_7.compareTo(lastYearHouseTax) == 0) {
//                    var_7 = new BigDecimal(-100);
//                }
//                if (var_7.compareTo(new BigDecimal(0)) != 0 && var_7.compareTo(currentYearHouseTax) == 0) {
//                    var_7 = new BigDecimal(100);
//                }
//                if (var_8.compareTo(new BigDecimal(0)) != 0 && var_8.compareTo(lastYearStampTax) == 0) {
//                    var_8 = new BigDecimal(-100);
//                }
//                if (var_8.compareTo(new BigDecimal(0)) != 0 && var_8.compareTo(currentYearStampTax) == 0) {
//                    var_8 = new BigDecimal(100);
//                }

                    row.createCell(0).setCellValue(currentMonthResult.get(0).getEnterpriseName());
                    row.createCell(1).setCellValue("增减%");
                    row.createCell(2).setCellValue(var_1.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(3).setCellValue(var_2.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(4).setCellValue(var_3.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(5).setCellValue(var_4.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(6).setCellValue(var_5.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(7).setCellValue(var_6.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(8).setCellValue(var_7.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(9).setCellValue(var_8.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row.createCell(10).setCellValue(var_1.add(var_2.add(var_3.add(var_4.add(var_5.add(var_6.add(var_7.add(var_8))))))).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    row = sheet.createRow(++rowIndex);
                    rowIndex++;
                }
            }
        }
        OutputStream os = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(year + "年企业税收同比明细表", "utf-8") + ".xls");
            os = response.getOutputStream();
            wb.write(os);
            os.flush();
        } catch (Exception e) {
            log.error("ERROR-OCCURRED {}", e.getMessage());
        } finally {
            try {
                wb.close();
                if (os != null) {
                    IOUtils.closeQuietly(os);
                }
            } catch (IOException e) {
                log.error("ERROR-OCCURRED {}", e.getMessage());
            }
        }
    }

    private BigDecimal ratio(BigDecimal currentTax, BigDecimal lastTax) {
        BigDecimal subtract = new BigDecimal("0");
        if (lastTax.compareTo(new BigDecimal(0)) != 0)
            subtract = currentTax.divide(lastTax, 2, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal("1"));
        if (lastTax.compareTo(new BigDecimal(0)) == 0) subtract = currentTax.subtract(new BigDecimal("100"));
        return subtract;
    }

    @Override
    public List<TaxMarginRankingView> taxMarginRanking(ReportBaseRequest request) {
        if (request.getYear() == null || request.getStartMonth() == null || request.getStartMonth() == null) {
            throw new GlobalException(ResultCodeEnum.INVALID_PARAM);
        }

        Integer year = request.getYear();
        Integer startMonth = request.getStartMonth();
        Integer endMonth = request.getEndMonth();
        Integer ranking = request.getRanking();

//        if (Objects.isNull(year)) year = LocalDateTime.now().getYear();
//        if (Objects.isNull(startMonth)) startMonth = LocalDateTime.now().getMonthValue();
//        if (Objects.isNull(endMonth)) endMonth = LocalDateTime.now().getMonthValue();
//
//        if (Objects.isNull(request.getStartMonth()) && startMonth == 1) {
//            year = year - 1;
//            startMonth = 12;
//            endMonth = 12;
//        }

//        String scope = request.getScope(); // 当月 累计
//        if (!StringUtils.hasLength(scope)) scope = "当月";
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getYear, year - 1, year)
                .ge(EnterpriseTaxPlus::getMonth, startMonth)
                .le(EnterpriseTaxPlus::getMonth, endMonth)
                .gt(EnterpriseTaxPlus::getTotalTax, 0);

        List<EnterpriseTaxPlus> results = enterpriseTaxMapper.selectList(queryWrapper);

        //符合条件的税收目标企业群体
        Map<String, List<EnterpriseTaxPlus>> groupResult = Maps.newHashMap();
        results.stream().filter(enterpriseTax ->
                !Objects.isNull(enterpriseTax.getTotalTax()) && StringUtils.hasLength(enterpriseTax.getEnterpriseName())
        ).collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(groupResult::put);
//        //今年相比去年的税收幅度
        List<TaxMarginRankingView> list = new ArrayList<>();
        groupResult.forEach((key, values) -> {
            double currentYearSum = values.stream().filter(enterpriseTax -> Objects.equals(year, enterpriseTax.getYear()))
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            double lastYearSum = values.stream().filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            TaxMarginRankingView view = TaxMarginRankingView.builder()
                    .enterpriseNo(values.get(0).getEnterpriseNo())
                    .enterpriseName(key)
                    .currentYearTax(convertValue(currentYearSum))
                    .currentYearTaxValue(currentYearSum)
                    .lastYearTax(convertValue(lastYearSum))
                    .differential(convertValue(currentYearSum - lastYearSum))
                    .build();
            list.add(view);
        });
        List<TaxMarginRankingView> viewList = list.stream().sorted(
                Comparator.comparing(TaxMarginRankingView::getCurrentYearTaxValue).reversed()
        ).collect(Collectors.toList());
        return viewList.stream().skip(0).limit(ranking).collect(Collectors.toList());
    }

    @Override
    public List<TaxMarginComparisonView> taxMarginComparison(ReportBaseRequest request) {
        if (request.getYear() == null) {
            throw new GlobalException(ResultCodeEnum.INVALID_PARAM);
        }
        int year = request.getYear();
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getYear, year - 2, year);
        List<EnterpriseTaxPlus> results = enterpriseTaxMapper.selectList(queryWrapper);
        //符合条件的税收目标企业群体
        Map<String, List<EnterpriseTaxPlus>> groupResult = Maps.newHashMap();
        //当年税收总额
        Map<String, Double> currentYearTaxMap = Maps.newHashMap();
        //去年税收总额
        Map<String, Double> lastYearTaxMap = Maps.newHashMap();
        //前年税收总额
        Map<String, Double> theYearBeforeLastTaxMap = Maps.newHashMap();
        results.stream().filter(
                enterpriseTax -> !Objects.isNull(enterpriseTax.getTotalTax()) && StringUtils.hasLength(enterpriseTax.getEnterpriseName())
        ).collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(groupResult::put);
        groupResult.entrySet().removeIf(entry -> {
            String enterpriseName = entry.getKey();
            List<EnterpriseTaxPlus> taxList = entry.getValue();
            double theYearBeforeLastSum = taxList.stream().filter(enterpriseTax -> year - 2 == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            double lastYearSum = taxList.stream().filter(enterpriseTax -> year - 1 == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            double currentYearSum = taxList.stream().filter(enterpriseTax -> year == enterpriseTax.getYear())
                    .mapToDouble(EnterpriseTaxPlus::getTotalTax).sum();
            if (lastYearSum - theYearBeforeLastSum <= request.getTaxIndices() * 10000 || currentYearSum - lastYearSum <= request.getTaxIndices() * 10000) {
                return true;
            }
            currentYearTaxMap.put(enterpriseName, currentYearSum);
            lastYearTaxMap.put(enterpriseName, lastYearSum);
            theYearBeforeLastTaxMap.put(enterpriseName, theYearBeforeLastSum);
            return false;
        });
        List<TaxMarginComparisonView> viewList = new ArrayList<>();
        for (String enterpriseName : groupResult.keySet()) {
            TaxMarginComparisonView view = TaxMarginComparisonView.builder()
                    .enterpriseName(enterpriseName)
                    .currentYearTax(convertValue(currentYearTaxMap.get(enterpriseName)))
                    .lastYearTax(convertValue(lastYearTaxMap.get(enterpriseName)))
                    .yearBeforeLastTax(convertValue(theYearBeforeLastTaxMap.get(enterpriseName)))
                    .differential(convertValue(currentYearTaxMap.get(enterpriseName) - lastYearTaxMap.get(enterpriseName)))
                    .lastYearDifferential(convertValue(lastYearTaxMap.get(enterpriseName) - theYearBeforeLastTaxMap.get(enterpriseName)))
                    .yearBeforeLastDifferential(convertValue(currentYearTaxMap.get(enterpriseName) - theYearBeforeLastTaxMap.get(enterpriseName)))
                    .build();
            viewList.add(view);
        }
        return viewList;
    }

    @Override
    public List<TaxDetailComparisonView> taxDetailComparison(ReportBaseRequest request) {
        if (request.getYear() == null) {
            throw new GlobalException(ResultCodeEnum.INVALID_PARAM);
        }
        int year = request.getYear();
        String queryString = request.getQueryString();
        List<TaxDetailComparisonView> viewList = new ArrayList<>();
        if (!StringUtils.hasLength(queryString)) return viewList;
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(EnterpriseTaxPlus::getEnterpriseName, queryString)
                .between(EnterpriseTaxPlus::getYear, year - 1, year);
        List<EnterpriseTaxPlus> results = enterpriseTaxMapper.selectList(queryWrapper);

        if (!CollectionUtils.isEmpty(results)) {
            for (int i = 0; i < 12; i++) {
                int tmpMonth = i + 1;
                List<EnterpriseTaxPlus> currentMonthResult = results.stream()
                        .filter(enterpriseTax ->
                                enterpriseTax.getMonth() != null
                                        && tmpMonth == enterpriseTax.getMonth())
                        .collect(Collectors.toList());
                if (currentMonthResult.size() > 0) {
                    BigDecimal currentYearAddedTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearAddedTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearCityTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getCityTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getCityTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearCityTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getCityTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getCityTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearIncomeTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getIncomeTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getIncomeTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearIncomeTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getIncomeTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getIncomeTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearPersonTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getPersonTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getPersonTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearPersonTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getPersonTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getPersonTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearExciseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getExcise()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getExcise()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearExciseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getExcise()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getExcise()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearAddedLandTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedLandTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedLandTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearAddedLandTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getAddedLandTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getAddedLandTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearHouseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getHouseTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getHouseTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearHouseTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getHouseTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getHouseTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentYearStampTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getStampTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getStampTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lastYearStampTax = currentMonthResult.stream()
                            .filter(enterpriseTax -> year - 1 == enterpriseTax.getYear() && !Objects.isNull(enterpriseTax.getStampTax()))
                            .map(enterpriseTax -> new BigDecimal(enterpriseTax.getStampTax()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    String currentYearTotalTax = currentYearAddedTax
                            .add(currentYearCityTax
                                    .add(currentYearIncomeTax
                                            .add(currentYearPersonTax
                                                    .add(currentYearExciseTax
                                                            .add(currentYearAddedLandTax
                                                                    .add(currentYearHouseTax
                                                                            .add(currentYearStampTax))))))).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                    String lastYearTotalTax = lastYearAddedTax
                            .add(lastYearCityTax
                                    .add(lastYearIncomeTax
                                            .add(lastYearPersonTax
                                                    .add(lastYearExciseTax
                                                            .add(lastYearAddedLandTax
                                                                    .add(lastYearHouseTax
                                                                            .add(lastYearStampTax))))))).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                    TaxDetailComparisonView currentYearView = TaxDetailComparisonView.builder()
                            .date(year + "-" + (tmpMonth < 10 ? "0" + tmpMonth : tmpMonth))
                            .enterpriseName(currentMonthResult.get(0).getEnterpriseName())
                            .addedTax(currentYearAddedTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .cityTax(currentYearCityTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .incomeTax(currentYearIncomeTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .personTax(currentYearPersonTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .excise(currentYearExciseTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .addedLandTax(currentYearAddedLandTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .houseTax(currentYearHouseTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .stampTax(currentYearStampTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .totalTaxTotal(currentYearTotalTax)
                            .build();
                    viewList.add(currentYearView);
//                    LocalDate parse = LocalDate.parse(year - 1 + "-" + (tmpMonth > 9 ? tmpMonth : "0" + tmpMonth) + "-01");
//                    LocalDate settledDate = currentMonthResult.get(0).getSettledDate();
//                    if (Objects.isNull(settledDate)) settledDate = LocalDate.parse("1970-01-01");
//                    if (settledDate.isBefore(parse)) {
                    TaxDetailComparisonView lastYearView = TaxDetailComparisonView.builder()
                            .date(year - 1 + "-" + (tmpMonth < 10 ? "0" + tmpMonth : tmpMonth))
                            .enterpriseName(currentMonthResult.get(0).getEnterpriseName())
                            .addedTax(lastYearAddedTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .cityTax(lastYearCityTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .incomeTax(lastYearIncomeTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .personTax(lastYearPersonTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .excise(lastYearExciseTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .addedLandTax(lastYearAddedLandTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .houseTax(lastYearHouseTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .stampTax(lastYearStampTax.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                            .totalTaxTotal(lastYearTotalTax)
                            .build();
                    viewList.add(lastYearView);
//                    }
                }
            }
        }
        return viewList;
    }

    /**
     * @Description 查询企业综合报表的字段
     * @Title getComprehensiveReportColumns
     * @Param []
     * @Return com.bemore.api.entity.response.EnterpriseComprehensiveReportColumnView
     * @Author Louis
     * @Date 2022/04/21 17:59
     */
    @Override
    public EnterpriseComprehensiveReportColumnView getComprehensiveReportColumns() {
        // 公司工商信息字段
        List<Field> enterpriseFields = Arrays.asList(Enterprise.class.getDeclaredFields());
        List<EnterpriseComprehensiveReportColumn> businessColumns = new ArrayList<>();
        enterpriseFields.forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (Objects.nonNull(excelColumn)) {
                businessColumns.add(
                        EnterpriseComprehensiveReportColumn.builder().field(field.getName()).label(excelColumn.value()).build()
                );
            }
        });
        // 公司人员信息字段
        List<Field> personFields = Arrays.asList(Person.class.getDeclaredFields());
        List<EnterpriseComprehensiveReportColumn> personColumns = new ArrayList<>();
        personFields.forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (Objects.nonNull(excelColumn)) {
                personColumns.add(
                        EnterpriseComprehensiveReportColumn.builder().field(field.getName()).label(excelColumn.value()).build()
                );
            }
        });
        // 税收信息字段
        List<Field> enterpriseTaxPlusFields = Arrays.asList(EnterpriseTaxPlus.class.getDeclaredFields());
        List<EnterpriseComprehensiveReportColumn> enterpriseTaxPlusColumns = new ArrayList<>();
        enterpriseTaxPlusFields.forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (Objects.nonNull(excelColumn)) {
                enterpriseTaxPlusColumns.add(
                        EnterpriseComprehensiveReportColumn.builder().field(field.getName()).label(excelColumn.value()).build()
                );
            }
        });
        // 扶持信息字段
        List<Field> supportFields = Arrays.asList(EnterpriseSupportDetailView.class.getDeclaredFields());
        List<EnterpriseComprehensiveReportColumn> supportColumns = new ArrayList<>();
        supportFields.forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (Objects.nonNull(excelColumn)) {
                supportColumns.add(
                        EnterpriseComprehensiveReportColumn.builder().field(field.getName()).label(excelColumn.value()).build()
                );
            }
        });
        // 区级收入字段
        List<Field> districtIncomeFields = Arrays.asList(DistrictIncome.class.getDeclaredFields());
        List<EnterpriseComprehensiveReportColumn> districtIncomeColumns = new ArrayList<>();
        districtIncomeFields.forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (Objects.nonNull(excelColumn)) {
                districtIncomeColumns.add(
                        EnterpriseComprehensiveReportColumn.builder().field(field.getName()).label(excelColumn.value()).build()
                );
            }
        });
        return EnterpriseComprehensiveReportColumnView.builder()
                .businessColumns(businessColumns)
                .personColumns(personColumns)
                .taxColumns(enterpriseTaxPlusColumns)
                .supportColumns(supportColumns)
                .districtIncomeColumns(districtIncomeColumns)
                .build();
    }

    /**
     * @Description 通过excel查询企业综合报表
     * @Title findComprehensiveReportByExcel
     * @Param [file]
     * @Return com.bemore.api.entity.response.EnterpriseComprehensiveExcelView
     * @Author Louis
     * @Date 2022/04/21 15:58
     */
    @Override
    public EnterpriseComprehensiveExcelView findComprehensiveReportByExcel(@NonNull MultipartFile file) {
        List<String> nameList = FileUtil.getEnterpriseNameByExcel(file);
        EnterpriseComprehensiveExcelView view = new EnterpriseComprehensiveExcelView();
        if (nameList.isEmpty()) return view;
        view.setCount(nameList.size());
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(!nameList.isEmpty(), Enterprise::getName, nameList);
        // 查到的企业名称集合
        List<String> enterpriseList = enterpriseMapper.selectList(queryWrapper)
                .stream()
                .map(Enterprise::getName)
                .collect(Collectors.toList());
        // 不存在的企业
        List<String> failedList = new ArrayList<>();
        // 把不存在的企业加入集合
        for (String name : nameList) {
            if (!enterpriseList.contains(name)) {
                failedList.add(name);
            }
        }
        // 删除不存在的企业，剩余的就是存在的企业
        nameList.removeAll(failedList);
        view.setFailedList(failedList);
        view.setList(nameList);
        return view;
    }

    /**
     * @Description 导出企业综合报表
     * @Title exportComprehensiveReport
     * @Param [param, resp]
     * @Return void
     * @Author Louis
     * @Date 2022/04/21 15:58
     */
    @Override
    public void exportComprehensiveReport(EnterpriseComprehensiveReportParam param, HttpServletResponse resp) {
        /********************************** 参数校验 **************************************/
        if (!StringUtils.hasText(param.getStartDate())) throw new RuntimeException("请选择起始时间");
        if (!StringUtils.hasText(param.getEndDate())) throw new RuntimeException("请选择结束时间");
        List<String> enterpriseNameList = param.getEnterpriseNameList();
        if (CollectionUtils.isEmpty(enterpriseNameList)) throw new RuntimeException("请选择需要导出的企业名称");
        /********************************** 准备数据 **************************************/
        EnterpriseComprehensiveReportColumnView columnView = param.getEnterpriseComprehensiveReportColumnView();
        // 企业信息字段
        List<String> enterpriseFields = columnView.getBusinessColumns().stream().map(EnterpriseComprehensiveReportColumn::getField).collect(Collectors.toList());
        // 企业法人字段
        List<String> personFields = columnView.getPersonColumns().stream().map(EnterpriseComprehensiveReportColumn::getField).collect(Collectors.toList());
        // 税务字段
        List<String> taxFields = columnView.getTaxColumns().stream().map(EnterpriseComprehensiveReportColumn::getField).collect(Collectors.toList());
        // 扶持字段
        List<String> supportFields = columnView.getSupportColumns().stream().map(EnterpriseComprehensiveReportColumn::getField).collect(Collectors.toList());
        // 区级收入字段
        List<String> districtIncomeFields = columnView.getDistrictIncomeColumns().stream().map(EnterpriseComprehensiveReportColumn::getField).collect(Collectors.toList());
        /********************************** 组装数据 **************************************/
        // 标题字段集合，合并所有的字段，默认固定第1列为企业名称
        List<String> titleList = new ArrayList<>();
        titleList.add("企业名称");
        columnView.getBusinessColumns().forEach(column -> titleList.add(column.getLabel()));
        columnView.getPersonColumns().forEach(column -> titleList.add(column.getLabel()));
        columnView.getTaxColumns().forEach(column -> titleList.add(column.getLabel()));
        columnView.getSupportColumns().forEach(column -> titleList.add(column.getLabel()));
        columnView.getDistrictIncomeColumns().forEach(column -> titleList.add(column.getLabel()));
        /********************************** 查询数据 **************************************/
        // 根据企业名称查询企业信息
        List<Enterprise> enterpriseList = enterpriseMapper.selectList(new QueryWrapper<Enterprise>().lambda().in(Enterprise::getName, enterpriseNameList)).stream().collect(Collectors.toList());
        // 将查询到的企业集合按照参数的企业顺序进行排序
        enterpriseList.sort((o1, o2) -> {
            int i1 = enterpriseNameList.indexOf(o1.getName());
            int i2 = enterpriseNameList.indexOf(o2.getName());
            return i1 - i2;
        });
        // 综合字段数据集合
        List<List<Object>> totalColumnDataList = new ArrayList<>();
        // 综合字段样式集合
        List<HorizontalAlignment> totalColumnStyleList = new ArrayList<>();
        // 循环下标
        AtomicInteger index = new AtomicInteger();
        /********************************** 操作数据 **************************************/
        enterpriseList.forEach(enterprise -> {
            // 每一行的字段数据集合
            List<Object> dataList = new ArrayList<>();
            // 添加第1列“企业名称”
            dataList.add(enterpriseNameList.get(index.getAndIncrement()));
            /********************************** 企业工商信息字段 **************************************/
            enterpriseFields.forEach(field -> {
                try {
                    Field declaredField = Enterprise.class.getDeclaredField(field);
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(enterprise);
                    value = Objects.nonNull(value) ? value : "";
                    if (Objects.equals("process", field)) {
                        // 所处流程(1新开，2迁入，3变更，4注销，5正常)
                        Integer process = (Integer) value;
                        switch (process) {
                            case 1:
                                value = "新开";
                                break;
                            case 2:
                                value = "迁入";
                                break;
                            case 3:
                                value = "变更";
                                break;
                            case 4:
                                value = "注销";
                                break;
                            case 5:
                                value = "正常";
                                break;
                            default:
                                value = "";
                                break;
                        }
                    }
                    dataList.add(Objects.nonNull(value) ? value : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            /********************************** 企业人员信息字段 **************************************/
            Person enterpriseMaster = Optional.ofNullable(personDao.findByEnterpriseIdAndIsMaster(enterprise.getId(), 1)).orElse(Person.builder().build());
            personFields.forEach(field -> {
                try {
                    Field declaredField = Person.class.getDeclaredField(field);
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(enterpriseMaster);
                    dataList.add(Objects.nonNull(value) ? value : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            /********************************** 税务字段 **************************************/
            TaxRequest taxRequest = TaxRequest.builder()
                    .startDate(param.getStartDate())
                    .endDate(param.getEndDate())
                    .queryString(enterprise.getName())
                    .build();
            List<EnterpriseTaxPlus> enterpriseTaxPlusList = enterpriseTaxService.getEnterpriseTaxByPage(taxRequest);
            EnterpriseTaxPlus enterpriseTaxPlus = null;
            if (!CollectionUtils.isEmpty(enterpriseTaxPlusList)) {
                // 将同一个公司的数据分组并将数字字段求和，由于只查询了一个公司，所以直接分组后就只有一条数据，直接get(0)
                enterpriseTaxPlus = enterpriseTaxPlusList.stream().collect(Collectors.toMap(EnterpriseTaxPlus::getEnterpriseName, a -> a, (o1, o2) -> {
                    o2.setSales(o1.getSales() + o2.getSales());
                    o2.setBusiness(o1.getBusiness() + o2.getBusiness());
                    o2.setAddedTax(o1.getAddedTax() + o2.getAddedTax());
                    o2.setToAddedTax(o1.getToAddedTax() + o2.getToAddedTax());
                    o2.setBusinessTax(o1.getBusinessTax() + o2.getBusinessTax());
                    o2.setExcise(o1.getExcise() + o2.getExcise());
                    o2.setIncomeTax(o1.getIncomeTax() + o2.getIncomeTax());
                    o2.setPersonTax(o1.getPersonTax() + o2.getPersonTax());
                    o2.setHouseTax(o1.getHouseTax() + o2.getHouseTax());
                    o2.setCarTax(o1.getCarTax() + o2.getCarTax());
                    o2.setStampTax(o1.getStampTax() + o2.getStampTax());
                    o2.setLandTax(o1.getLandTax() + o2.getLandTax());
                    o2.setAddedLandTax(o1.getAddedLandTax() + o2.getAddedLandTax());
                    o2.setCityTax(o1.getCityTax() + o2.getCityTax());
                    o2.setEnvironmentTax(o1.getEnvironmentTax() + o2.getEnvironmentTax());
                    o2.setFarmlandTax(o1.getFarmlandTax() + o2.getFarmlandTax());
                    o2.setCarPurchaseTax(o1.getCarPurchaseTax() + o2.getCarPurchaseTax());
                    o2.setDeedTax(o1.getDeedTax() + o2.getDeedTax());
                    o2.setTotalTax(o1.getTotalTax() + o2.getTotalTax());
                    return o2;
                })).values().stream().findFirst().orElse(EnterpriseTaxPlus.builder().build());
            } else {
                enterpriseTaxPlus = EnterpriseTaxPlus.builder().build();
            }
            // 合计完后的税收对象
            EnterpriseTaxPlus finalEnterpriseTaxPlus = enterpriseTaxPlus;
            taxFields.forEach(field -> {
                try {
                    Field declaredField = EnterpriseTaxPlus.class.getDeclaredField(field);
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(finalEnterpriseTaxPlus);
                    dataList.add(Objects.nonNull(value) ? value : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            /********************************** 扶持字段 **************************************/
            ReportBaseRequest reportBaseRequest = ReportBaseRequest.builder()
                    .startDate(param.getStartDate())
                    .endDate(param.getEndDate())
                    .queryString(enterprise.getName())
                    .build();
            List<EnterpriseSupportDetailView> enterpriseSupportDetailList = this.findEnterpriseSupportDetail(reportBaseRequest);
            EnterpriseSupportDetailView enterpriseSupportDetailView = null;
            if (!CollectionUtils.isEmpty(enterpriseSupportDetailList)) {
                // 将同一个公司的数据分组并将数字字段求和，由于只查询了一个公司，所以直接分组后就只有一条数据，直接get(0)
                enterpriseSupportDetailView = enterpriseSupportDetailList.stream().collect(Collectors.toMap(EnterpriseSupportDetailView::getEnterpriseName, a -> a, (o1, o2) -> {
                    o2.setSupportAmountValue(o1.getSupportAmountValue() + o2.getSupportAmountValue());
                    o2.setMonthAmountValue(o1.getMonthAmountValue() + o2.getMonthAmountValue());
                    o2.setSurplusValue(o1.getSurplusValue() + o2.getSurplusValue());
                    return o2;
                })).values().stream().findFirst().orElse(EnterpriseSupportDetailView.builder().build());
            } else {
                enterpriseSupportDetailView = EnterpriseSupportDetailView.builder().build();
            }
            // 合计完后的扶持信息对象
            EnterpriseSupportDetailView finalEnterpriseSupportDetailView = enterpriseSupportDetailView;
            supportFields.forEach(field -> {
                try {
                    Field declaredField = EnterpriseSupportDetailView.class.getDeclaredField(field);
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(finalEnterpriseSupportDetailView);
                    dataList.add(Objects.nonNull(value) ? value : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            /********************************** 区级收入 **************************************/
            // 构建区级收入对象
            DistrictIncome districtIncome = DistrictIncome.builder()
                    .districtAddedTax(Objects.isNull(finalEnterpriseTaxPlus.getAddedTax()) ? null : finalEnterpriseTaxPlus.getAddedTax() * 0.325)
                    .districtCityTax(Objects.isNull(finalEnterpriseTaxPlus.getCityTax()) ? null : finalEnterpriseTaxPlus.getCityTax() * 0.65)
                    .districtIncomeTax(Objects.isNull(finalEnterpriseTaxPlus.getIncomeTax()) ? null : finalEnterpriseTaxPlus.getIncomeTax() * 0.2)
                    .districtPersonTax(Objects.isNull(finalEnterpriseTaxPlus.getPersonTax()) ? null : finalEnterpriseTaxPlus.getPersonTax() * 0.22)
                    .districtHouseTax(Objects.isNull(finalEnterpriseTaxPlus.getHouseTax()) ? null : finalEnterpriseTaxPlus.getHouseTax() * 0.8)
                    .districtAddedLandTax(Objects.isNull(finalEnterpriseTaxPlus.getAddedLandTax()) ? null : finalEnterpriseTaxPlus.getAddedLandTax() * 0.8)
                    .districtStampTax(Objects.isNull(finalEnterpriseTaxPlus.getStampTax()) ? null : finalEnterpriseTaxPlus.getStampTax() * 1)
                    .build();
            // 计算总数
            districtIncome.setDistrictTotalTax(
                    (Objects.isNull(districtIncome.getDistrictAddedTax()) ? 0 : districtIncome.getDistrictAddedTax())
                            + (Objects.isNull(districtIncome.getDistrictCityTax()) ? 0 : districtIncome.getDistrictCityTax())
                            + (Objects.isNull(districtIncome.getDistrictIncomeTax()) ? 0 : districtIncome.getDistrictIncomeTax())
                            + (Objects.isNull(districtIncome.getDistrictPersonTax()) ? 0 : districtIncome.getDistrictPersonTax())
                            + (Objects.isNull(districtIncome.getDistrictHouseTax()) ? 0 : districtIncome.getDistrictHouseTax())
                            + (Objects.isNull(districtIncome.getDistrictAddedLandTax()) ? 0 : districtIncome.getDistrictAddedLandTax())
                            + (Objects.isNull(districtIncome.getDistrictStampTax()) ? 0 : districtIncome.getDistrictStampTax())
            );
            districtIncomeFields.forEach(field -> {
                try {
                    Field declaredField = DistrictIncome.class.getDeclaredField(field);
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(districtIncome);
                    dataList.add(Objects.nonNull(value) ? value : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // 添加到总数据集合
            totalColumnDataList.add(dataList);
        });
        // 取出一行，判断类型设置字段样式
        List<Object> oneRow = totalColumnDataList.get(0);
        for (int i = 0, len = oneRow.size(); i < len; i++) {
            // 字段类型
            String fieldType = oneRow.get(i).getClass().getName();
            if (Objects.equals("java.lang.Double", fieldType)) {
                // 金额相关的字段全部靠右
                totalColumnStyleList.add(HorizontalAlignment.RIGHT);
            } else if (titleList.get(i).contains("企业名称") || titleList.get(i).contains("地址")) {
                //企业名称和地址字段靠左
                totalColumnStyleList.add(HorizontalAlignment.LEFT);
            } else {
                totalColumnStyleList.add(HorizontalAlignment.CENTER);
            }
        }
        /********************************** 准备文件 **************************************/
        // 文件名
        String fileName = new StringBuffer()
                .append(param.getStartDate())
                .append("月~")
                .append(param.getEndDate())
                .append("月综合报表数据").toString();
        // 创建 Excel
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 总字段数量，+1 是因为前面标题多加了一列”企业名称“
        int totalColumn = 1 + columnView.getBusinessColumns().size()
                + columnView.getPersonColumns().size()
                + columnView.getTaxColumns().size()
                + columnView.getSupportColumns().size()
                + columnView.getDistrictIncomeColumns().size();
        if (totalColumn > 1) {
            // 合并单元格，设置文件标题。注意：-1是因为下标是从0开始的，
            excelWriter.merge(totalColumn - 1, fileName);
            excelWriter.setRowHeight(0, 30);
        }
        // 设置所有列宽
        for (int i = 0; i < totalColumn; i++) {
            excelWriter.setColumnWidth(i, 30);
        }
        // 写入标题行
        excelWriter.writeRow(titleList, false);
        // 写入数据行
        excelWriter.write(totalColumnDataList, false);
        // 获取表格，给每一个单元格设置样式
        Sheet sheet = excelWriter.getSheet();
        // 总行数
        int rowPhysicalNumber = sheet.getPhysicalNumberOfRows();
        // 从第3行开始
        for (int i = 2; i < rowPhysicalNumber; i++) {
            Row row = sheet.getRow(i);
            if (Objects.isNull(row)) {
                continue;
            }
            // 当前行的列数
            int cellPhysicalNumber = row.getLastCellNum();
            // 从第1列开始
            for (int j = 0; j < cellPhysicalNumber; j++) {
                row.getCell(j).getCellStyle().setAlignment(totalColumnStyleList.get(j));
            }
        }
        /********************************** 文件输出 **************************************/
        ServletOutputStream out = null;
        try {
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            resp.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            out = resp.getOutputStream();
            excelWriter.flush(out, true);
            excelWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }


}
