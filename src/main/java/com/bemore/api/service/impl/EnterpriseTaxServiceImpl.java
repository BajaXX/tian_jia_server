package com.bemore.api.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.dao.mapper.*;
import com.bemore.api.entity.*;
import com.bemore.api.entity.request.*;
import com.bemore.api.entity.response.EnterpriseTaxExcelView;
import com.bemore.api.entity.response.TaxRankingView;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.EnterpriseTaxService;
import com.bemore.api.util.DateUtil;
import com.bemore.api.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.bemore.api.constant.CommonConstants.SUPPORT_LEVEL;

@Transactional
@Service
@Slf4j
public class EnterpriseTaxServiceImpl implements EnterpriseTaxService {

    private final EnterpriseTaxMapper enterpriseTaxMapper;
    private final EnterpriseDao enterpriseDao;
    private final SupportAgreementMapper supportAgreementMapper;
    private final SupportMonthLogMapper supportMonthLogMapper;
    private final NewValidAccountMapper newValidAccountMapper;
    private final RollbackLogMapper rollbackLogMapper;

    public EnterpriseTaxServiceImpl(EnterpriseTaxMapper enterpriseTaxMapper,
                                    EnterpriseDao enterpriseDao,
                                    SupportAgreementMapper supportAgreementMapper,
                                    SupportMonthLogMapper supportMonthLogMapper,
                                    NewValidAccountMapper newValidAccountMapper,
                                    RollbackLogMapper rollbackLogMapper) {
        this.enterpriseTaxMapper = enterpriseTaxMapper;
        this.enterpriseDao = enterpriseDao;
        this.supportAgreementMapper = supportAgreementMapper;
        this.supportMonthLogMapper = supportMonthLogMapper;
        this.newValidAccountMapper = newValidAccountMapper;
        this.rollbackLogMapper = rollbackLogMapper;
    }

    @Override
    public void cleanIndustryService() {
        List<Enterprise> enterpriseList = enterpriseDao.findAll();
        enterpriseList.forEach(enterprise -> enterprise.setIndustry(""));
        enterpriseDao.saveAll(enterpriseList);

        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        List<EnterpriseTaxPlus> taxPlusList = enterpriseTaxMapper.selectList(queryWrapper);
        taxPlusList.forEach(tax -> {
            tax.setIndustry("");
            enterpriseTaxMapper.updateById(tax);
        });
    }

    @Override
    public EnterpriseTaxExcelView findEnterpriseTaxByExcel(TaxRequest request) {
        MultipartFile file = request.getFile();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        List<String> nameList = new ArrayList<>();
        if (!Objects.isNull(file)) {
            nameList = FileUtil.getEnterpriseNameByExcel(file);
        }
        EnterpriseTaxExcelView view = new EnterpriseTaxExcelView();

        if (nameList.isEmpty()) return view;

        view.setCount(nameList.size());
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(!nameList.isEmpty(), EnterpriseTaxPlus::getEnterpriseName, nameList)
                .between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .orderByDesc(EnterpriseTaxPlus::getTotalTax);
        List<EnterpriseTaxPlus> taxList = enterpriseTaxMapper.selectList(queryWrapper);

        // 获取查询失败的企业
        for (EnterpriseTaxPlus tax : taxList) {
            nameList.removeIf(name -> Objects.equals(name, tax.getEnterpriseName()));
        }
        List<EnterpriseParam> failedList = nameList.stream().map(name -> {
            EnterpriseParam nameView = new EnterpriseParam();
            nameView.setName(name);
            return nameView;
        }).collect(Collectors.toList());
        view.setFailedList(failedList);
        view.setTaxList(taxList);
        return view;
    }

    @Override
    public void cleanTable() {
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        enterpriseTaxMapper.delete(queryWrapper);
    }

    @Override
    public void exportEnterpriseTax(TaxExportRequest request, HttpServletResponse resp) {
        List<ColumnModel> columns = request.getColumns();
        if (columns.isEmpty()) throw new RuntimeException("导出的列为空");
        TaxRequest taxRequest = new TaxRequest();
        BeanUtils.copyProperties(request, taxRequest);
        List<EnterpriseTaxPlus> enterpriseTaxList = getEnterpriseTaxByPage(taxRequest);

        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        StyleSet styleSet = excelWriter.getStyleSet();
        styleSet.setWrapText();

        for (int i = 0; i < columns.size(); i++) {
            excelWriter.addHeaderAlias(columns.get(i).getLabel(), columns.get(i).getValue()).setColumnWidth(i, 15);
        }
//        excelWriter.addHeaderAlias("enterpriseName","企业名称");
        // 2021年01月~2021年02月企业税收数据
        StringBuffer buffer = new StringBuffer();
        buffer.append(request.getStartDate());
        buffer.append("月~");
        buffer.append(request.getEndDate());
        buffer.append("月企业税收数据");
        excelWriter.setOnlyAlias(true);
        int index = columns.size();
        if (index > 1)
            excelWriter.merge(columns.size() - 1, buffer.toString());
        excelWriter.write(enterpriseTaxList, true);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode(buffer.toString(), "utf-8");
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

    @Override
    public List<EnterpriseTaxPlus> getEnterpriseTaxByPage(TaxRequest request) {
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        if (!StringUtils.hasLength(startDate))
            startDate = DateUtil.getDefaultQueryDateOnTax();

        if (!StringUtils.hasLength(endDate))
            endDate = DateUtil.getDefaultQueryDateOnTax();

        int currentPage = request.getPage();
        IPage<EnterpriseTaxPlus> page = new Page<>(currentPage, request.getSize());
        //  查询结束月数据
        QueryWrapper<EnterpriseTaxPlus> endDateQuery = new QueryWrapper<>();
        endDateQuery.lambda().like(StringUtils.hasLength(request.getQueryString()), EnterpriseTaxPlus::getEnterpriseName, request.getQueryString()).between(EnterpriseTaxPlus::getDate, startDate, endDate).groupBy(EnterpriseTaxPlus::getEnterpriseName);
//                .having("max(date)");
        List<EnterpriseTaxPlus> endDateList = enterpriseTaxMapper.selectList(endDateQuery);
        // 查询时间范围内所有数据
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(StringUtils.hasLength(request.getQueryString()), EnterpriseTaxPlus::getEnterpriseName, request.getQueryString())
                .between(EnterpriseTaxPlus::getDate, startDate, endDate)
                .orderByDesc(EnterpriseTaxPlus::getTotalTax);
//        IPage<EnterpriseTaxPlus> iPage = enterpriseTaxMapper.selectPage(page, queryWrapper);
        List<EnterpriseTaxPlus> taxList = enterpriseTaxMapper.selectList(queryWrapper);
//        Map<String,EnterpriseTaxPlus> endDateList=taxList.stream().collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName,Collectors.collectingAndThen(Collectors.reducing((t1,t2)->Integer.valueOf(t1.getDate().replace("-",""))>Integer.valueOf(t2.getDate().replace("-",""))?t1:t2),Optional::get)));
        // 按企业分组
        Map<String, List<EnterpriseTaxPlus>> map = new HashMap<>();
        taxList.stream().collect(Collectors.groupingBy(EnterpriseTaxPlus::getEnterpriseName, Collectors.toList())).forEach(map::put);
        // 根据结束月顺序分组按月份降序显示
        List<EnterpriseTaxPlus> resultList = new ArrayList<>();
        endDateList.forEach(endData -> {
            map.forEach((key, value) -> {
                if (Objects.equals(endData.getEnterpriseName(), key)) {
                    // 按月份降序
                    value = value.stream().sorted(Comparator.comparing(EnterpriseTaxPlus::getMonth).reversed()).collect(Collectors.toList());
                    resultList.addAll(value);
                }
            });
        });
        return resultList;
    }

    @Override
    public List<EnterpriseTaxParam> preview(MultipartFile[] files) {
        List<EnterpriseTaxParam> resultList = new ArrayList<>();
        if (!Objects.isNull(files)) {
            for (int i = 0; i < files.length; i++) {
                String originalFilename = files[i].getOriginalFilename();
                log.info("当前导入的月份企业税收为：{}", originalFilename);

                StringBuffer importFailed = new StringBuffer();
                importFailed.append("文件名为：");
                importFailed.append(originalFilename);
                importFailed.append("因找不到对应工商信息导致导入失败的企业有:");

                ExcelReader excelReader = null;
                try {
                    excelReader = ExcelUtil.getReader(files[i].getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                excelReader.addHeaderAlias("企业名称", "enterpriseName");
                excelReader.addHeaderAlias("所属年月", "date");
                excelReader.addHeaderAlias("销售额", "sales");
                excelReader.addHeaderAlias("增值税", "addedTax");
                excelReader.addHeaderAlias("营业税", "businessTax");
                excelReader.addHeaderAlias("企业所得税", "incomeTax");
                excelReader.addHeaderAlias("个人所得税", "personTax");
                excelReader.addHeaderAlias("房产税", "houseTax");
                excelReader.addHeaderAlias("印花税", "stampTax");
                excelReader.addHeaderAlias("营改增", "toAddedTax");
                excelReader.addHeaderAlias("城建税", "cityTax");
                excelReader.addHeaderAlias("消费税", "excise");
                excelReader.addHeaderAlias("城镇土地使用税", "landTax");
                excelReader.addHeaderAlias("土地增值税", "addedLandTax");
                excelReader.addHeaderAlias("车船使用税", "carTax");
                excelReader.addHeaderAlias("耕地占用税", "farmlandTax");
                excelReader.addHeaderAlias("车购税", "carPurchaseTax");
                excelReader.addHeaderAlias("契税", "deedTax");
                excelReader.addHeaderAlias("环境保护税", "environmentTax");
                excelReader.addHeaderAlias("税金合计自定义", "totalTax");
                List<EnterpriseTaxParam> paramList = excelReader.readAll(EnterpriseTaxParam.class);
                resultList.addAll(paramList);
            }
        }
        return resultList;
    }

    @Override
    public String findNewestRollbackLog() {
        RollbackLog log = rollbackLogMapper.selectNewestRollbackLog();
        if (Objects.isNull(log)) return "";
        return log.getDate();
    }

    @Override
    public void cleanDataByMonthService(String date) {
        if (!StringUtils.hasLength(date)) return;
        QueryWrapper<EnterpriseTaxPlus> taxWrapper = new QueryWrapper<>();
        taxWrapper.lambda().eq(StringUtils.hasLength(date), EnterpriseTaxPlus::getDate, date);
        enterpriseTaxMapper.delete(taxWrapper);
        QueryWrapper<NewValidAccount> validAccountQueryWrapper = new QueryWrapper<>();
        validAccountQueryWrapper.lambda().eq(StringUtils.hasLength(date), NewValidAccount::getDate, date);
        newValidAccountMapper.delete(validAccountQueryWrapper);
        QueryWrapper<SupportMonthLog> logQueryWrapper = new QueryWrapper<>();
        logQueryWrapper.lambda().eq(StringUtils.hasLength(date), SupportMonthLog::getDate, date);
        supportMonthLogMapper.delete(logQueryWrapper);
        RollbackLog log = new RollbackLog();
        log.setDate(date);
        log.setYear(Integer.parseInt(date.split("-")[0]));
        log.setMonth(Integer.parseInt(date.split("-")[1]));
        rollbackLogMapper.insert(log);
    }

    @Override
    public void importEnterpriseTaxService(MultipartFile files, String date) {
        if (StringUtils.hasLength(date)) {
            QueryWrapper<EnterpriseTaxPlus> taxWrapper = new QueryWrapper<>();
            taxWrapper.lambda().eq(StringUtils.hasLength(date), EnterpriseTaxPlus::getDate, date);
            enterpriseTaxMapper.delete(taxWrapper);
            QueryWrapper<NewValidAccount> validAccountQueryWrapper = new QueryWrapper<>();
            validAccountQueryWrapper.lambda().eq(StringUtils.hasLength(date), NewValidAccount::getDate, date);
            newValidAccountMapper.delete(validAccountQueryWrapper);
            QueryWrapper<SupportMonthLog> logQueryWrapper = new QueryWrapper<>();
            logQueryWrapper.lambda().eq(StringUtils.hasLength(date), SupportMonthLog::getDate, date);
            supportMonthLogMapper.delete(logQueryWrapper);
            QueryWrapper<RollbackLog> rollbackLogQueryWrapper = new QueryWrapper<>();
            rollbackLogQueryWrapper.lambda().eq(StringUtils.hasLength(date), RollbackLog::getDate, date);
            rollbackLogMapper.delete(rollbackLogQueryWrapper);
        }
        importEnterpriseTax(files);
    }

    /**
     * @param files 此方法基于 t_enterprise_tax 表每个企业每月必有一条税收数据，否则计算累计税金会有错误
     */
    @Override
    public void batchImportEnterpriseTaxService(MultipartFile[] files) {
//        List<String> messageArr = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
//            String originalFilename = files[i].getOriginalFilename();
//            log.info("当前导入的月份企业税收为：{}",originalFilename);
            importEnterpriseTax(files[i]);
        }
    }

    @Override
    public String findNewestTaxMonth() {
        EnterpriseTaxPlus enterpriseTaxPlus = enterpriseTaxMapper.selectNewestTaxMonth();
        Integer year = enterpriseTaxPlus.getYear();
        Integer month = enterpriseTaxPlus.getMonth();
        return year + "-" + (month > 9 ? month : "0" + month);
    }

    private void importEnterpriseTax(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.info("当前导入的月份企业税收为：{}", originalFilename);

//        StringBuffer  importFailed = new StringBuffer();
//        importFailed.append("文件名为：");
//        importFailed.append(originalFilename);
//        importFailed.append("找不到对应工商信息按税收数据企业插入的有:");

        ExcelReader excelReader = null;
        try {
            excelReader = ExcelUtil.getReader(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        excelReader.addHeaderAlias("企业名称", "enterpriseName");
        excelReader.addHeaderAlias("所属年月", "date");
        excelReader.addHeaderAlias("销售额", "sales");
        excelReader.addHeaderAlias("增值税", "addedTax");
        excelReader.addHeaderAlias("营业税", "businessTax");
        excelReader.addHeaderAlias("企业所得税", "incomeTax");
        excelReader.addHeaderAlias("个人所得税", "personTax");
        excelReader.addHeaderAlias("房产税", "houseTax");
        excelReader.addHeaderAlias("印花税", "stampTax");
        excelReader.addHeaderAlias("营改增", "toAddedTax");
        excelReader.addHeaderAlias("城建税", "cityTax");
        excelReader.addHeaderAlias("消费税", "excise");
        excelReader.addHeaderAlias("城镇土地使用税", "landTax");
        excelReader.addHeaderAlias("土地增值税", "addedLandTax");
        excelReader.addHeaderAlias("车船使用税", "carTax");
        excelReader.addHeaderAlias("耕地占用税", "farmlandTax");
        excelReader.addHeaderAlias("车购税", "carPurchaseTax");
        excelReader.addHeaderAlias("契税", "deedTax");
        excelReader.addHeaderAlias("环境保护税", "environmentTax");
        excelReader.addHeaderAlias("税金合计自定义", "totalTax");
        List<EnterpriseTaxParam> paramList = excelReader.readAll(EnterpriseTaxParam.class);
        List<String> errorEnterprise=new ArrayList<>();
        for (EnterpriseTaxParam param : paramList) {
            if(Objects.isNull(param.getDate()) || "".equals(param.getDate())){
                errorEnterprise.add(param.getEnterpriseName());
                continue;
            }
            // 日期
            String date = param.getDate().substring(0, 6);
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(4, 6));
            int lastMonth = month - 1;

            String totalTax = param.getTotalTax();
            String paymentDate = "";
            if (StringUtils.hasLength(totalTax) && Double.parseDouble(totalTax) > 0)
                paymentDate = year + "-" + (month < 10 ? "0" + month : month) + "-02";
            log.info("{}xxxx{}", paymentDate, param.getEnterpriseName());
            // 根据企业名查询企业工商信息
            Enterprise enterprise = enterpriseDao.findByName(param.getEnterpriseName());
            if (Objects.isNull(enterprise)) {
//                importFailed.append(param.getEnterpriseName());
//                importFailed.append(",");
                log.info("add new enterprise...{}", param.getEnterpriseName());
                Enterprise add = new Enterprise();
                add.setName(param.getEnterpriseName());
                if (StringUtils.hasLength(param.getTotalTax()) && Double.parseDouble(param.getTotalTax()) > 0 && StringUtils.hasLength(paymentDate))
                    add.setPaymentDate(LocalDate.parse(paymentDate));
                enterprise = enterpriseDao.save(add);
            }
            // 查询当前企业上个月的各项税金，计算累计税金
            QueryWrapper<EnterpriseTaxPlus> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(EnterpriseTaxPlus::getEnterpriseName, param.getEnterpriseName())
                    .eq(EnterpriseTaxPlus::getDate, year + "-" + (lastMonth < 10 ? "0" + lastMonth : lastMonth));
            EnterpriseTaxPlus lastMonthTax = enterpriseTaxMapper.selectOne(wrapper);
            // 组装
            EnterpriseTaxPlus enterpriseTax = new EnterpriseTaxPlus();
            enterpriseTax.setEnterpriseName(param.getEnterpriseName());
            enterpriseTax.setDate(year + "-" + (month < 10 ? "0" + month : month));
            enterpriseTax.setYear(year);
            enterpriseTax.setMonth(month);
            enterpriseTax.setSales(string2double(param.getSales()));
            enterpriseTax.setBusiness(string2double(param.getBusiness()));
            enterpriseTax.setAddedTax(string2double(param.getAddedTax()));
            enterpriseTax.setToAddedTax(string2double(param.getToAddedTax()));
            enterpriseTax.setBusinessTax(string2double(param.getBusinessTax()));
            enterpriseTax.setExcise(string2double(param.getExcise()));
            enterpriseTax.setIncomeTax(string2double(param.getIncomeTax()));
            enterpriseTax.setPersonTax(string2double(param.getPersonTax()));
            enterpriseTax.setHouseTax(string2double(param.getHouseTax()));
            enterpriseTax.setCarTax(string2double(param.getCarTax()));
            enterpriseTax.setStampTax(string2double(param.getStampTax()));
            enterpriseTax.setLandTax(string2double(param.getLandTax()));
            enterpriseTax.setAddedLandTax(string2double(param.getAddedLandTax()));
            enterpriseTax.setCityTax(string2double(param.getCityTax()));
            enterpriseTax.setEnvironmentTax(string2double(param.getEnvironmentTax()));
            enterpriseTax.setFarmlandTax(string2double(param.getFarmlandTax()));
            enterpriseTax.setCarPurchaseTax(string2double(param.getCarPurchaseTax()));
            enterpriseTax.setDeedTax(string2double(param.getDeedTax()));
            enterpriseTax.setTotalTax(string2double(totalTax));
            // 计算累计税金
            setTotalTax(lastMonthTax, enterpriseTax);
            // 组装工商信息
            if (!Objects.isNull(enterprise)) {
                String capital = enterprise.getCapital();
                if (!StringUtils.hasLength(capital)) {
                    capital = "0";
                }
                enterpriseTax.setEnterpriseNo(enterprise.getEnterpriseNo());
//                    enterpriseTax.setIndustry(enterprise.getIndustry());
                enterpriseTax.setGarden(enterprise.getGarden());
                enterpriseTax.setFoundDate(enterprise.getStartTimez());
                enterpriseTax.setEnterpriseType(enterprise.getType());
                enterpriseTax.setProjectType(convertProcess(enterprise.getProcess()));
                enterpriseTax.setReportDate(null);
                enterpriseTax.setThisRegion(null);
                enterpriseTax.setActContactAddress(enterprise.getActContactAddress());
                enterpriseTax.setRegisterAddress(enterprise.getRegisterAddress());
                enterpriseTax.setCapital(Double.parseDouble(capital));
                enterpriseTax.setSource(enterprise.getSource());
                enterpriseTax.setInType(null);
                enterpriseTax.setProcess(convertProcess(enterprise.getProcess()));
                enterpriseTax.setRegisterNum(enterprise.getRegisterNum());
                enterpriseTax.setSettledDate(enterprise.getSettledDate());
                enterpriseTax.setInstitutionalType(enterprise.getInstitutionalType());
                enterpriseTax.setInvestmentType(enterprise.getInvestmentType());
                if (Objects.isNull(enterprise.getPaymentDate()) && StringUtils.hasLength(paymentDate)) {
                    enterpriseTax.setPaymentDate(LocalDate.parse(paymentDate));
                    enterprise.setPaymentDate(LocalDate.parse(paymentDate));
                    enterpriseDao.save(enterprise);
                } else {
                    enterpriseTax.setPaymentDate(enterprise.getPaymentDate());
                }

            }
            try {
                enterpriseTaxMapper.insert(enterpriseTax);
            } catch (DuplicateKeyException e) {
//                throw new WebException(101, "导入数据重复:" + enterpriseTax.getEnterpriseName());
                errorEnterprise.add(enterpriseTax.getEnterpriseName());
                continue;
            }

//            // 计算扶持金
//            LocalDate startDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
//            LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
//            QueryWrapper<SupportAgreement> supportWrapper = new QueryWrapper<>();
//            supportWrapper.lambda().le(SupportAgreement::getStartDate, startDate.toString())
//                    .ge(SupportAgreement::getEndDate, endDate.toString())
//                    .eq(SupportAgreement::getState, "启用");
//            List<SupportAgreement> supportAgreements = supportAgreementMapper.selectList(supportWrapper);
//            // 计算组装扶持明细
//            supportAgreements.forEach(supportAgreement -> {
//                if (Objects.equals(supportAgreement.getEnterpriseName(), enterpriseTax.getEnterpriseName())) {
//                    // 增值税
//                    Double addedTax = Objects.isNull(enterpriseTax.getAddedTax()) ? 0 : enterpriseTax.getAddedTax();
//                    // 企业所得税
//                    Double incomeTax = Objects.isNull(enterpriseTax.getIncomeTax()) ? 0 : enterpriseTax.getIncomeTax();
//                    // 个人所得税
//                    Double personTax = Objects.isNull(enterpriseTax.getPersonTax()) ? 0 : enterpriseTax.getPersonTax();
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
//                    // 查询上月结余
//                    QueryWrapper<SupportMonthLog> lastMonthSupportWrapper = new QueryWrapper<>();
//                    Integer lastMonthSupportYear = enterpriseTax.getYear();
//                    Integer lastMonthSupportMonth = enterpriseTax.getMonth();
//                    if (lastMonthSupportMonth > 1) lastMonthSupportMonth -= 1;
//                    lastMonthSupportWrapper.lambda().eq(SupportMonthLog::getEnterpriseName, supportAgreement.getEnterpriseName())
//                            .eq(SupportMonthLog::getYear, lastMonthSupportYear)
//                            .eq(SupportMonthLog::getMonth, lastMonthSupportMonth);
//                    SupportMonthLog lastMonthSupport = supportMonthLogMapper.selectOne(lastMonthSupportWrapper);
//                    // 上月结余
//                    double lastMonthSurplus = 0;
//                    if (!Objects.isNull(lastMonthSupport)) {
//                        lastMonthSurplus = Double.parseDouble(lastMonthSupport.getSurplus());
//                    }
//
//                    // 当月结余
//                    double surplus = 0;
//                    if (supportAmount < 10000)
//                        surplus = supportAmount;
//                    if (supportAmount > 10000)
//                        surplus = supportAmount % 10000;
//                    // 当月扶持 每月清算，本月结余加上上月结余，如果结果大于等于5000 则扶持一万，当月结余为10000 - 结余金额
//                    double monthAmount = supportAmount - surplus;
//                    surplus = surplus + lastMonthSurplus;
//                    if (surplus >= 5000) {
//                        monthAmount += 10000;
//                        surplus = surplus - 10000;
//                    }
//                    SupportMonthLog supportMonthLog = SupportMonthLog.builder()
//                            .garden("长三角一体化示范区(上海)金融产业园")
//                            .supportId(supportAgreement.getId())
//                            .year(year)
//                            .month(month)
//                            .enterpriseName(enterpriseTax.getEnterpriseName())
//                            .supportAreas(supportAgreement.getSupportAreas())
//                            .supportProject(supportAgreement.getSupportProject())
//                            .supportAmount(convertValue7(supportAmount))
//                            .monthAmount(convertValue7(monthAmount))
//                            .surplus(convertValue7(surplus))
//                            .depositBank(supportAgreement.getDepositBank())
//                            .bankAccount(supportAgreement.getBankAccount())
//                            .date(year + "-" + (month < 10 ? "0" + month : month))
//                            .build();
//                    supportMonthLogMapper.insert(supportMonthLog);
//                }
//            });

            // 计算扶持金
            LocalDate startDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
            LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
            QueryWrapper<SupportAgreement> supportWrapper = new QueryWrapper<>();
            supportWrapper.lambda().le(SupportAgreement::getStartDate, startDate.toString())
                    .ge(SupportAgreement::getEndDate, endDate.toString())
                    .eq(SupportAgreement::getState, "启用");
            List<SupportAgreement> supportAgreements = supportAgreementMapper.selectList(supportWrapper);
            // 计算组装扶持明细
            supportAgreements.forEach(supportAgreement -> {
                if (Objects.equals(supportAgreement.getEnterpriseName(), enterpriseTax.getEnterpriseName())) {
                    // 增值税
                    Double addedTax = Objects.isNull(enterpriseTax.getAddedTax()) ? 0 : enterpriseTax.getAddedTax();
                    // 企业所得税
                    Double incomeTax = Objects.isNull(enterpriseTax.getIncomeTax()) ? 0 : enterpriseTax.getIncomeTax();
                    // 个人所得税
                    Double personTax = Objects.isNull(enterpriseTax.getPersonTax()) ? 0 : enterpriseTax.getPersonTax();
                    // 增税率计算值
                    String addedTaxRateValue = supportAgreement.getAddedTaxRateValue();
                    // 企税率计算值
                    String incomeTaxRateValue = supportAgreement.getIncomeTaxRateValue();
                    // 个税率计算值
                    String personTaxRateValue = supportAgreement.getPersonTaxRateValue();
                    // 扶持金额 增值税*增税率计算值 + 企业所得税*企税率计算值 + 个人所得税*个税率计算值
                    double supportAmount = addedTax * Double.parseDouble(addedTaxRateValue) +
                            incomeTax * Double.parseDouble(incomeTaxRateValue) +
                            personTax * Double.parseDouble(personTaxRateValue);
                    // 查询上月结余
                    QueryWrapper<SupportMonthLog> lastMonthSupportWrapper = new QueryWrapper<>();
                    Integer lastMonthSupportYear = enterpriseTax.getYear();
                    Integer lastMonthSupportMonth = enterpriseTax.getMonth();
                    if (lastMonthSupportMonth > 1) lastMonthSupportMonth -= 1;
                    lastMonthSupportWrapper.lambda().eq(SupportMonthLog::getEnterpriseName, supportAgreement.getEnterpriseName())
                            .eq(SupportMonthLog::getYear, lastMonthSupportYear)
                            .eq(SupportMonthLog::getMonth, lastMonthSupportMonth);
                    SupportMonthLog lastMonthSupport = supportMonthLogMapper.selectOne(lastMonthSupportWrapper);
                    // 上月结余
                    double lastMonthSurplus = 0;
                    if (!Objects.isNull(lastMonthSupport)) {
                        lastMonthSurplus = Double.parseDouble(lastMonthSupport.getSurplus());
                    }

                    // 当月结余
                    double surplus = 0;
                    if (supportAmount < 10000)
                        surplus = supportAmount;
                    if (supportAmount > 10000)
                        surplus = supportAmount % 10000;
                    // 当月扶持 每月清算，本月结余加上上月结余，如果结果大于等于5000 则扶持一万，当月结余为10000 - 结余金额
                    double monthAmount = supportAmount - surplus;
                    surplus = surplus + lastMonthSurplus;
                    if (surplus >= SUPPORT_LEVEL) {
                        monthAmount += 10000;
                        surplus = surplus - 10000;
                    }
                    SupportMonthLog supportMonthLog = SupportMonthLog.builder()
                            .garden("天佳经济园")
                            .supportId(supportAgreement.getId())
                            .year(year)
                            .month(month)
                            .enterpriseName(enterpriseTax.getEnterpriseName())
                            .supportAreas(supportAgreement.getSupportAreas())
                            .supportProject(supportAgreement.getSupportProject())
                            .supportAmount(convertValue7(supportAmount))
                            .monthAmount(convertValue7(monthAmount))
                            .surplus(convertValue7(surplus))
                            .depositBank(supportAgreement.getDepositBank())
                            .bankAccount(supportAgreement.getBankAccount())
                            .date(year + "-" + (month < 10 ? "0" + month : month))
                            .build();
                    supportMonthLogMapper.insert(supportMonthLog);
                }
            });

            // 是否新增有效户
            if (string2double(totalTax) >= 1000) {
                QueryWrapper<NewValidAccount> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(NewValidAccount::getEnterpriseName, enterpriseTax.getEnterpriseName());
                NewValidAccount validAccount = newValidAccountMapper.selectOne(queryWrapper);
                if (Objects.isNull(validAccount)) {
                    NewValidAccount newValidAccount = new NewValidAccount();
                    newValidAccount.setEnterpriseId(enterprise.getId());
                    newValidAccount.setEnterpriseName(enterprise.getName());
                    newValidAccount.setDate(year + "-" + (month < 10 ? "0" + month : month));
                    newValidAccount.setYear(year);
                    newValidAccount.setMonth(month);
                    newValidAccountMapper.insert(newValidAccount);
                }
            }
        }
        if(errorEnterprise.size()>0){
            throw new WebException(101,errorEnterprise.toString());
        }
//        messageArr.add(importFailed.toString());
    }

    private void setTotalTax(EnterpriseTaxPlus lastMonthTax, EnterpriseTaxPlus currentMonthTax) {
        // 上月税收数据为null，则认为当前月为1月
        if (Objects.isNull(lastMonthTax)) {
            currentMonthTax.setSalesTotal(currentMonthTax.getSales());
            currentMonthTax.setBusinessTotal(currentMonthTax.getBusiness());
            currentMonthTax.setAddedTaxTotal(currentMonthTax.getToAddedTax());
            currentMonthTax.setToAddedTaxTotal(currentMonthTax.getToAddedTax());
            currentMonthTax.setBusinessTaxTotal(currentMonthTax.getBusinessTax());
            currentMonthTax.setExciseTotal(currentMonthTax.getExcise());
            currentMonthTax.setIncomeTaxTotal(currentMonthTax.getIncomeTax());
            currentMonthTax.setPersonTaxTotal(currentMonthTax.getPersonTax());
            currentMonthTax.setHouseTaxTotal(currentMonthTax.getHouseTax());
            currentMonthTax.setCarTaxTotal(currentMonthTax.getCarTax());
            currentMonthTax.setStampTaxTotal(currentMonthTax.getStampTax());
            currentMonthTax.setLandTaxTotal(currentMonthTax.getLandTax());
            currentMonthTax.setAddedLandTaxTotal(currentMonthTax.getAddedLandTax());
            currentMonthTax.setCityTaxTotal(currentMonthTax.getCityTax());
            currentMonthTax.setEnvironmentTaxTotal(currentMonthTax.getEnvironmentTax());
            currentMonthTax.setFarmlandTaxTotal(currentMonthTax.getFarmlandTax());
            currentMonthTax.setCarPurchaseTaxTotal(currentMonthTax.getCarPurchaseTax());
            currentMonthTax.setDeedTaxTotal(currentMonthTax.getDeedTax());
            currentMonthTax.setTotalTaxTotal(currentMonthTax.getTotalTax());
        } else {
            currentMonthTax.setSalesTotal(currentMonthTax.getSales() + lastMonthTax.getSalesTotal());
            currentMonthTax.setBusinessTotal(currentMonthTax.getBusiness() + lastMonthTax.getBusinessTotal());
            currentMonthTax.setAddedTaxTotal(currentMonthTax.getToAddedTax() + lastMonthTax.getAddedTaxTotal());
            currentMonthTax.setToAddedTaxTotal(currentMonthTax.getToAddedTax() + lastMonthTax.getToAddedTaxTotal());
            currentMonthTax.setBusinessTaxTotal(currentMonthTax.getBusinessTax() + lastMonthTax.getBusinessTaxTotal());
            currentMonthTax.setExciseTotal(currentMonthTax.getExcise() + lastMonthTax.getExciseTotal());
            currentMonthTax.setIncomeTaxTotal(currentMonthTax.getIncomeTax() + lastMonthTax.getIncomeTaxTotal());
            currentMonthTax.setPersonTaxTotal(currentMonthTax.getPersonTax() + lastMonthTax.getPersonTaxTotal());
            currentMonthTax.setHouseTaxTotal(currentMonthTax.getHouseTax() + lastMonthTax.getHouseTaxTotal());
            currentMonthTax.setCarTaxTotal(currentMonthTax.getCarTax() + lastMonthTax.getCarTaxTotal());
            currentMonthTax.setStampTaxTotal(currentMonthTax.getStampTax() + lastMonthTax.getStampTaxTotal());
            currentMonthTax.setLandTaxTotal(currentMonthTax.getLandTax() + lastMonthTax.getLandTaxTotal());
            currentMonthTax.setAddedLandTaxTotal(currentMonthTax.getAddedLandTax() + lastMonthTax.getAddedLandTaxTotal());
            currentMonthTax.setCityTaxTotal(currentMonthTax.getCityTax() + lastMonthTax.getCityTaxTotal());
            currentMonthTax.setEnvironmentTaxTotal(currentMonthTax.getEnvironmentTax() + lastMonthTax.getEnvironmentTaxTotal());
            currentMonthTax.setFarmlandTaxTotal(currentMonthTax.getFarmlandTax() + lastMonthTax.getFarmlandTaxTotal());
            currentMonthTax.setCarPurchaseTaxTotal(currentMonthTax.getCarPurchaseTax() + lastMonthTax.getCarPurchaseTaxTotal());
            currentMonthTax.setDeedTaxTotal(currentMonthTax.getDeedTax() + lastMonthTax.getDeedTaxTotal());
            currentMonthTax.setTotalTaxTotal(currentMonthTax.getTotalTax() + lastMonthTax.getTotalTaxTotal());
        }

    }

    private String convertProcess(int process) {
        String str = "";
        if (!Objects.isNull(process)) {
            if (process == 1) {
                str = "新开";
            } else if (process == 2) {
                str = "迁入";
            } else if (process == 3) {
                str = "变更";
            } else if (process == 4) {
                str = "注销";
            } else if (process == 5) {
                str = "正常";
            }
        }
        return str;
    }

    private Double string2double(String str) {
        double value = 0;
        if (StringUtils.hasLength(str)) {
            value = Double.parseDouble(str);
        }
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private String convertValue(double value) {
        if (Objects.isNull(value)) return "0";
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    // 保留7位小数
    private String convertValue7(double value) {
        if (Objects.isNull(value)) return "0";
        return new BigDecimal(value).setScale(7, RoundingMode.HALF_UP).toPlainString();
    }

    private String double2String(double d, String s) {
        BigDecimal bDecimal = new BigDecimal(d);
        if (!StringUtils.isEmpty(s)) {
            bDecimal.add(new BigDecimal(s));
        }
        return bDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String double2String(double d) {
        BigDecimal bDecimal = new BigDecimal(d);
        return bDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
