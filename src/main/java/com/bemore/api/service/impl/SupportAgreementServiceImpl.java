package com.bemore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bemore.api.dao.*;
import com.bemore.api.dao.mapper.EnterpriseTaxMapper;
import com.bemore.api.dao.mapper.SupportAgreementMapper;
import com.bemore.api.dao.mapper.SupportMonthLogMapper;
import com.bemore.api.dto.req.SupportAgreementReq;
import com.bemore.api.entity.*;
import com.bemore.api.entity.request.SettledQueryParam;
import com.bemore.api.entity.request.SupportAgreementParam;
import com.bemore.api.entity.response.SupportAgreementView;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.SupportAgreementService;
import com.bemore.api.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bemore.api.constant.CommonConstants.SUPPORT_LEVEL;

@Slf4j
@Service
public class SupportAgreementServiceImpl implements SupportAgreementService {

    private final SupportAgreementMapper supportAgreementMapper;
    private final EnterpriseTaxMapper enterpriseTaxMapper;
    private final SupportMonthLogMapper supportMonthLogMapper;

    @Autowired
    private PlatformsDao platformsDao;

    @Autowired
    private PlatformsContractDao platformsContractDao;

    @Autowired
    private SupportContractDao supportContractDao;

    @Autowired
    private ContractRulesDao contractRulesDao;

    @Autowired
    private EnterpriseTaxPlusDao enterpriseTaxPlusDao;

    @Autowired
    private EnterpriseSupportLogDao enterpriseSupportLogDao;


    public SupportAgreementServiceImpl(SupportAgreementMapper supportAgreementMapper, EnterpriseTaxMapper enterpriseTaxMapper, SupportMonthLogMapper supportMonthLogMapper) {
        this.supportAgreementMapper = supportAgreementMapper;
        this.enterpriseTaxMapper = enterpriseTaxMapper;
        this.supportMonthLogMapper = supportMonthLogMapper;
    }

    @Override
    public void recomputeContract(SupportAgreementReq supportAgreementReq) {
        String enterpriseName = supportAgreementReq.getEnterpriseName();

        String endDate = supportAgreementReq.getEndDate();
        int startYear = Integer.parseInt(endDate.substring(0, 4));
        if (!Objects.isNull(enterpriseName) && !"".equals(enterpriseName) && Objects.nonNull(supportAgreementReq.getContractId())) {
            enterpriseSupportLogDao.deleteEnterpriseSupportLogsByEnterpriseNameAndYearAndSupportId(enterpriseName, startYear, supportAgreementReq.getContractId());
        } else {
            enterpriseSupportLogDao.deleteEnterpriseSupportLogsByYear(startYear);
        }
        computeContract(supportAgreementReq);

    }

    @Override
    public void computePlatform(SupportAgreementReq supportAgreementReq) {
        String platformId = supportAgreementReq.getEnterpriseName();

        String endDate = supportAgreementReq.getEndDate();
        int startYear = Integer.parseInt(endDate.substring(0, 4));
        int endMonth = Integer.parseInt(endDate.substring(4, 6));
        int sdate = startYear * 100 + 1;
        int edate = startYear * 100 + endMonth;


        if (!Objects.isNull(platformId) && !"".equals(platformId)) {

            for (int curDate = sdate; curDate <= edate; curDate++) {

                //获取本年度已经扶持金额
                List<EnterpriseSupportLog> yearEnterpriseSupportLogList = enterpriseSupportLogDao.findByPlatformIdAndYearAndDateLessThanEqual(platformId, startYear, curDate);
                double sumSupportYear = yearEnterpriseSupportLogList.stream().collect(Collectors.summarizingDouble(EnterpriseSupportLog::getSupportAmount)).getSum();

                List<EnterpriseSupportLog> enterpriseSupportLogs = enterpriseSupportLogDao.findByPlatformIdAndDate(platformId, curDate);
                if (enterpriseSupportLogs.isEmpty()) throw new WebException(101, curDate + "月该平台下没有招商企业协议");

                //本月平台扶持金总额
                double sumSupport = enterpriseSupportLogs.stream().collect(Collectors.summarizingDouble(EnterpriseSupportLog::getPlatformSupportAmount)).getSum();

                //获取上月扶持数据
                List<EnterpriseSupportLog> lastEnterpriseSupportLogList = enterpriseSupportLogDao.findByPlatformIdAndDate(platformId, curDate - 1);


                //上月平台结余扶持金总额
                double sumLastSurplus = 0;

                if (!lastEnterpriseSupportLogList.isEmpty()) {
                    sumLastSurplus = lastEnterpriseSupportLogList.stream().collect(Collectors.summarizingDouble(EnterpriseSupportLog::getPlatformSurplus)).getSum();
                }

                // 当月结余
                double platformSurplus = 0;
//                sumSupport += sumLastSurplus;
                double curMonthSumSupport = sumSupport + sumLastSurplus;
                double platform_month_amount = 0;
                if (sumSupportYear + sumSupport < 10000) {
                    if (curMonthSumSupport < 5000 && curMonthSumSupport >= 4000) {
                        platformSurplus = curMonthSumSupport - 4000;
                        platform_month_amount = 4000;
                    } else if (curMonthSumSupport < 4000) {
                        platformSurplus = curMonthSumSupport;
                    } else if (curMonthSumSupport >= 5000) {
                        platformSurplus = 10000 - curMonthSumSupport;
                        platform_month_amount = 10000;
                    }
                } else {
                    platformSurplus = curMonthSumSupport % 10000;
                    platform_month_amount = curMonthSumSupport - platformSurplus;
                    if (platformSurplus >= 5000) {
                        platform_month_amount += 10000;
                        platformSurplus = platformSurplus - 10000;
                    }
                }
//                double platformSurplus = 0;
//                if (sumSupport < 10000) platformSurplus = sumSupport;
//                if (sumSupport > 10000) platformSurplus = sumSupport % 10000;
//
//
//                // 当月扶持 每月清算，本月结余加上上月结余，如果结果大于等于5000 则扶持一万，当月结余为10000 - 结余金额
//                double platform_month_amount = sumSupport - platformSurplus;
//                platformSurplus = platformSurplus + sumLastSurplus;
//                if (platformSurplus >= SUPPORT_LEVEL) {
//                    platform_month_amount += 10000;
//                    platformSurplus = platformSurplus - 10000;
//                }


                for (EnterpriseSupportLog log : enterpriseSupportLogs) {
                    log.setPlatformMonthAmount(log.getPlatformSupportAmount() / sumSupport * platform_month_amount);
                    log.setPlatformSurplus(log.getPlatformSupportAmount() / sumSupport * platformSurplus);

                    enterpriseSupportLogDao.save(log);
                }
            }

        } else {
            throw new WebException(101, "必须选择招商平台");
        }

    }

    @Override
    public void computeContract(SupportAgreementReq supportAgreementReq) {
        String enterpriseName = supportAgreementReq.getEnterpriseName();
//        String startDate = supportAgreementReq.getStartDate();
        String endDate = supportAgreementReq.getEndDate();
        int startYear = Integer.parseInt(endDate.substring(0, 4));
//        int startMonth = Integer.parseInt(startDate.substring(5, 7));
        int endMonth = Integer.parseInt(endDate.substring(4, 6));
        int sdate = startYear * 100 + 1;
        int edate = startYear * 100 + endMonth;

        String sdate_str = new StringBuffer(String.valueOf(sdate)).insert(4, "-").toString();
        String edate_str = new StringBuffer(String.valueOf(edate)).insert(4, "-").toString();


        //获取扶持协议对应的企业
        List<String> enterpriseList = new ArrayList<>();

        if (Objects.isNull(enterpriseName) || "".equals(enterpriseName)) {
            enterpriseList = supportContractDao.getSupportEnterpriseList();
        } else {
            enterpriseList.add(enterpriseName);
        }

        for (String enterprise : enterpriseList) {

            //获取当前企业所有税务数据

            List<EnterpriseTaxPlus> enterpriseTaxList = enterpriseTaxPlusDao.findByEnterpriseNameAndDateBetween(enterprise, sdate_str, edate_str);
            if (enterpriseTaxList.isEmpty()) continue;


            for (int curDate = sdate; curDate <= edate; curDate++) {
                //获取本年度已经扶持金额
                List<EnterpriseSupportLog> yearEnterpriseSupportLogList = enterpriseSupportLogDao.findByEnterpriseNameAndYearAndDateLessThanEqual(enterprise, startYear, curDate);
                double sumSupportYear = yearEnterpriseSupportLogList.stream().collect(Collectors.summarizingDouble(EnterpriseSupportLog::getSupportAmount)).getSum();
                double sumMonthAmount = yearEnterpriseSupportLogList.stream().collect(Collectors.summarizingDouble(EnterpriseSupportLog::getMonthAmount)).getSum();

                //当前处理月份
                String curDateStr = new StringBuffer(String.valueOf(curDate)).insert(4, "-").toString();
                String lastDateStr = new StringBuffer(String.valueOf(curDate - 1)).insert(4, "-").toString();


                //获取本月的税务数据
                EnterpriseTaxPlus enterpriseTaxPlus = enterpriseTaxList.stream().filter(t -> t.getDate().equals(curDateStr)).findFirst().orElse(null);
                if (Objects.isNull(enterpriseTaxPlus)) continue;

                int finalCurDate = curDate;
                List<EnterpriseTaxPlus> allTaxCurDate = enterpriseTaxList.stream()
                        .filter(t -> Integer.valueOf(t.getDate().replace("-", "")) <= finalCurDate)
                        .collect(Collectors.toList());

                //获取三方协议
                SupportContract supportContract = null;

                if (Objects.nonNull(supportAgreementReq.getContractId()) && !"".equals(supportAgreementReq.getContractId())) {
                    //获取扶持协议对象
                    supportContract = supportContractDao.getOne(supportAgreementReq.getContractId());
                    if (Objects.isNull(supportContract)) throw new WebException(102, enterprise + "指定的协议未找到，无法计算");

                } else {

                    //1 获取当前企业有效的扶持协议
                    List<SupportContract> supportContractList = supportContractDao.findByEnterpriseNameAndDate(enterprise, curDate);

                    //没数据退出当前循环
                    if (Objects.isNull(supportContractList) || supportContractList.isEmpty()) continue;

                    if (supportContractList.size() > 1)
                        throw new WebException(101, enterprise + "存在两份有效协议，无法计算，请检查数据。");
                    supportContract = supportContractList.get(0);
                }


                //已有当月扶持数据，跳过继续
                if (!enterpriseSupportLogDao.findByEnterpriseNameAndSupportContractIdAndDate(enterprise, supportContract.getContractId(), curDate).isEmpty())
                    continue;


                //获取上月扶持数据
                List<EnterpriseSupportLog> lastEnterpriseSupportLogList = enterpriseSupportLogDao.findByEnterpriseNameAndSupportContractIdAndDate(enterprise, supportContract.getContractId(), curDate - 1);
                if (lastEnterpriseSupportLogList.size() > 1) {
                    throw new WebException(101, enterprise + "上月存在多个补贴数据，数据有误，请联系管理员。");
                }
                EnterpriseSupportLog lastEnterpriseSupportLog = lastEnterpriseSupportLogList.isEmpty() || Objects.isNull(lastEnterpriseSupportLogList.get(0)) ? null : lastEnterpriseSupportLogList.get(0);

                //2 计算
                //获取企业类型
                int isFund = supportContract.getIsFund();
                //获取园区扶持政策
                List<ContractRules> contractRules = contractRulesDao.findAllByContractIdOrderByTaxStart(supportContract.getContractId());
                if (contractRules.isEmpty()) throw new WebException(101, supportContract.getContractName() + "扶持条款找不到");

                double ttt = enterpriseTaxPlus.getTotalTaxTotal() < 0 ? 0 : enterpriseTaxPlus.getTotalTaxTotal();


                //获取园区政策
                ContractRules contractRule = contractRules.stream().filter(t -> t.getTaxStart() * 10000 <= ttt && t.getTaxEnd() * 10000 > ttt).findFirst().orElse(null);
                if (Objects.isNull(contractRule)) throw new WebException(103, enterprise + "园区政策未找到");


                Double addedTaxRateValue_base = contractRule.getAddedTaxRate() / 100 * contractRule.getGroupRate() / 100 * 0.325;
                Double incomeTaxRateValue_base = contractRule.getIncomeTaxRate() * contractRule.getGroupRate() * 0.2 / 10000;
                Double personTaxRateValue_base = contractRule.getPersonTaxRate() * contractRule.getGroupRate() * 0.22 / 10000;

                EnterpriseSupportLog supportLog = new EnterpriseSupportLog();

                if (enterprise.equals("上海积怡尼经贸有限公司")) {
                    System.out.println(enterprise);
                }

                // 增值税
//                Double addedTax = Objects.isNull(enterpriseTaxPlus.getAddedTax()) ? 0 : enterpriseTaxPlus.getAddedTax();
                Double addedTax = allTaxCurDate.stream().collect(Collectors.summarizingDouble(EnterpriseTaxPlus::getAddedTax)).getSum();

                // 企业所得税
//                Double incomeTax = Objects.isNull(enterpriseTaxPlus.getIncomeTax()) ? 0 : enterpriseTaxPlus.getIncomeTax();
                Double incomeTax = allTaxCurDate.stream().collect(Collectors.summarizingDouble(EnterpriseTaxPlus::getIncomeTax)).getSum();
                // 个人所得税
//                Double personTax = Objects.isNull(enterpriseTaxPlus.getPersonTax()) ? 0 : enterpriseTaxPlus.getPersonTax();
                Double personTax = allTaxCurDate.stream().collect(Collectors.summarizingDouble(EnterpriseTaxPlus::getPersonTax)).getSum();


//                if (enterpriseTaxPlus.getYear() <= 2023) {
//                    addedTax = Util.formatDouble(addedTax / 10000, 2);
//                    incomeTax = Util.formatDouble(incomeTax / 10000, 2);
//                    personTax = Util.formatDouble(personTax / 10000, 2);
//                }

                //截止当月，全年累计税收基数
                double supportAmount_base = addedTax * addedTaxRateValue_base + incomeTax * incomeTaxRateValue_base + personTax * personTaxRateValue_base;

//                if (enterpriseTaxPlus.getYear() <= 2023) {
//                    supportAmount_base = Util.formatDouble(supportAmount_base, 2);
//                }

//                supportAmount_base = supportAmount_base - sumMonthAmount;
                log.info("本月扶持金额...{}", supportAmount_base);
                // 查询上月结余

                // 上月结余
//                double lastMonthSurplus = 0;
//                if (!Objects.isNull(lastEnterpriseSupportLog)) {
//                    lastMonthSurplus = lastEnterpriseSupportLog.getSurplus();
//                }
//                log.info("上月结余扶持金...{}", lastMonthSurplus);

//                double curMonthSupport = supportAmount_base + lastMonthSurplus;


                //当月参与扶持的税收
                double curMonthSupport = supportAmount_base;

                if (enterpriseTaxPlus.getYear() <= 2023) {
                    curMonthSupport = Util.formatDouble(curMonthSupport /10000,2)*10000;
                }
                // 当月结余
                double surplus = 0;
                //当月累计扶持金额
                double monthAmount = 0;
                //4月份及之前的数据按照该方式计算进位
                if (curDate <= 202302) {
                    //园区直招的计算方式
                    if (supportContract.getPlatformName().isEmpty()) {
                        if (curMonthSupport < 10000 && curMonthSupport >= 4000) {
                            surplus = curMonthSupport % 1000;
                            monthAmount = curMonthSupport - surplus;
//                            if (surplus > 0) {
//                                monthAmount += 1000;
//                                surplus = surplus - 1000;
//                            }
                        } else if (curMonthSupport < 4000) {
                            surplus = curMonthSupport;
                        } else if (curMonthSupport >= 10000) {
                            surplus = curMonthSupport % 10000;
                            monthAmount = curMonthSupport - surplus;
//                            if (surplus > 0) {
//                                monthAmount += 10000;
//                                surplus = surplus - 10000;
//                            }
                        }
                    } else {
                        //有平台的计算方式
                        surplus = curMonthSupport % 10000;
                        monthAmount = curMonthSupport - surplus;
//                        if (surplus > 0) {
//                            monthAmount += 10000;
//                            surplus = surplus - 10000;
//                        }
                    }
                } else {
                    //新的计算规则

                    if (supportAmount_base < 10000) {
                        if (curMonthSupport < 5000 && curMonthSupport >= 4000) {
                            surplus = curMonthSupport - 4000;
                            monthAmount = 4000;
                        } else if (curMonthSupport < 4000) {
                            surplus = curMonthSupport;
                        } else if (curMonthSupport >= 5000) {
                            surplus = 10000 - curMonthSupport;
                            monthAmount = 10000;
                        }
                    } else {
                        surplus = curMonthSupport % 10000;
                        monthAmount = curMonthSupport - surplus;
                        if (surplus >= 5000) {
                            monthAmount += 10000;
                            surplus = surplus - 10000;
                        }
                    }
                }

                monthAmount = monthAmount - sumMonthAmount;

//                if (enterpriseTaxPlus.getYear() <= 2023) {
//                    monthAmount = Util.formatDouble(monthAmount /10000,2);
//                    surplus = Util.formatDouble(surplus /10000,2);
//                }


                supportLog.setGarden("天佳经济园");
                supportLog.setSupportId(supportContract.getId());
                supportLog.setBankAccount(supportContract.getBankAccount());
                supportLog.setDepositBank(supportContract.getDepositBank());
                supportLog.setDate(curDate);
                supportLog.setEnterpriseName(supportContract.getEnterpriseName());
                supportLog.setMonthAmount(monthAmount);
                supportLog.setSupportAmount(supportAmount_base);
                supportLog.setSurplus(surplus);
                supportLog.setYear(startYear);
                supportLog.setStatus(1);
                supportLog.setPlatformId(supportContract.getPlatformId());
                supportLog.setPlatformName(supportContract.getPlatformName());
                supportLog.setSupportContractId(supportContract.getContractId());


                //计算平台扶持金
                if (Objects.nonNull(supportContract.getPlatformId()) && !"".equals(supportContract.getPlatformId())) {
                    //处理平台的扶持金额

                    //获取平台有效政策


                    //获取平台扶持政策
                    List<PlatformsContract> platformsContractList = platformsContractDao.findPlatformsContractsByPlatformIdAndIsFundAndAgreementStartLessThanEqualAndAgreementEndGreaterThanEqual(supportContract.getPlatformId(), isFund, curDate, curDate);
                    if (platformsContractList.isEmpty()) throw new WebException(101, enterprise + "，没有对应的平台协议");
                    if (platformsContractList.size() > 1)
                        throw new WebException(101, enterprise + "，有多个有效的平台协议，数据错误，请联系管理员");

                    PlatformsContract platformsContract = platformsContractList.get(0);

                    supportLog.setPlatformContractId(platformsContract.getId());

                    List<ContractRules> rules = contractRulesDao.findAllByContractIdOrderByTaxStart(platformsContract.getContractId());
                    if (rules.isEmpty()) throw new WebException(101, platformsContract.getContractName() + "扶持条款找不到");

                    //获取本月平台所有企业税收
                    Double allTax = enterpriseTaxPlusDao.sumTaxByPlatformIdAndDate(supportContract.getPlatformId(), curDateStr, supportContract.getIsFund());
                    Double lastSumTax = enterpriseTaxPlusDao.sumTaxByPlatformIdAndDate(supportContract.getPlatformId(), lastDateStr, supportContract.getIsFund());
                    allTax = Objects.isNull(allTax) ? 0 : allTax;
                    lastSumTax = Objects.isNull(lastSumTax) ? 0 : lastSumTax;

                    //获取上月匹配平台政策
                    Double finalLastSumTax = lastSumTax;
                    ContractRules rule_last = rules.stream().filter(t -> t.getTaxStart() * 10000 <= finalLastSumTax && t.getTaxEnd() * 10000 > finalLastSumTax).findFirst().orElse(null);
                    if (Objects.isNull(rule_last))
                        throw new WebException(103, enterprise + ",平台政策未找到,月份:" + lastDateStr);


                    //获取本月匹配平台政策
                    Double finalAllTax = allTax;
                    ContractRules rule = rules.stream().filter(t -> t.getTaxStart() * 10000 <= finalAllTax && t.getTaxEnd() * 10000 > finalAllTax).findFirst().orElse(null);
                    if (Objects.isNull(rule)) throw new WebException(103, enterprise + ",平台政策未找到,月份:" + curDateStr);

                    supportLog.setContractId(platformsContract.getContractId());

                    if (rule_last.getId().equals(rule.getId())) {


                        Double addedTaxRateValue_platform = rule.getAddedTaxRate() * rule.getGroupRate() * 0.325 / 10000;
                        Double incomeTaxRateValue_platform = rule.getIncomeTaxRate() * rule.getGroupRate() * 0.2 / 10000;
                        Double personTaxRateValue_platform = rule.getPersonTaxRate() * rule.getGroupRate() * 0.22 / 10000;


                        log.info("企业名称：{}", enterprise);
                        log.info("月份：{}", curDateStr);
                        log.info("匹配增值税比率：{}", rule.getAddedTaxRate());
                        log.info("匹配所得税比率：{}", rule.getIncomeTaxRate());
                        log.info("匹配个人所得税比率：{}", rule.getPersonTaxRate());
                        log.info("集团率：{}", rule.getGroupRate());
                        log.info("增值税计算值：{}", addedTaxRateValue_platform);
                        log.info("所得税计算值：{}", incomeTaxRateValue_platform);
                        log.info("个人所得税计算值：{}", personTaxRateValue_platform);


                        double supportAmount_platform = enterpriseTaxPlus.getAddedTax() * addedTaxRateValue_platform + enterpriseTaxPlus.getIncomeTax() * incomeTaxRateValue_platform + enterpriseTaxPlus.getPersonTax() * personTaxRateValue_platform - supportAmount_base;


                        log.info("企业增值税...{}", enterpriseTaxPlus.getAddedTax());
                        log.info("所得税...{}", enterpriseTaxPlus.getIncomeTax());
                        log.info("个人所得税...{}", enterpriseTaxPlus.getPersonTax());
                        log.info("平台当月应获得金额...{}", supportAmount_platform);


                        supportLog.setPlatformSupportAmount(supportAmount_platform);

                    } else {

                        Double addedTaxRateValue_platform = rule.getAddedTaxRate() * rule.getGroupRate() * 0.325 / 10000;
                        Double incomeTaxRateValue_platform = rule.getIncomeTaxRate() * rule.getGroupRate() * 0.2 / 10000;
                        Double personTaxRateValue_platform = rule.getPersonTaxRate() * rule.getGroupRate() * 0.22 / 10000;


                        //截止上月已支付的扶持金
                        List<EnterpriseSupportLog> supportLogs = enterpriseSupportLogDao.findByEnterpriseNameAndSupportContractIdAndYearAndDateLessThan(supportContract.getEnterpriseName(), supportContract.getContractId(), startYear, curDate);
                        double last_total_support_platform = supportLogs.stream().collect(Collectors.summarizingDouble(EnterpriseSupportLog::getPlatformSupportAmount)).getSum();
                        double last_total_support_enterprise = supportLogs.stream().collect(Collectors.summarizingDouble(EnterpriseSupportLog::getSupportAmount)).getSum();


                        List<double[]> total_tax_list = enterpriseTaxPlusDao.sumAllTaxByEnterpriseAndDateAndYear(enterprise, curDateStr, startYear);

                        double[] total_tax = total_tax_list.get(0);

                        //按照新的阶梯金额计算截止本月的扶持金
                        double total_support = total_tax[0] * addedTaxRateValue_platform + total_tax[1] * incomeTaxRateValue_platform + total_tax[2] * personTaxRateValue_platform;

                        double supportAmount_platform = total_support - last_total_support_enterprise - last_total_support_platform - supportLog.getSupportAmount();


                        supportLog.setPlatformSupportAmount(supportAmount_platform);
                    }

                }


                enterpriseSupportLogDao.save(supportLog);
                log.info("supportLog...{}", supportLog);

            }
        }


    }

    @Override
    public SupportAgreementView findSupportAgreement(String id) {
        SupportAgreement supportAgreement = supportAgreementMapper.selectById(id);
        SupportAgreementView view = new SupportAgreementView();
        BeanUtils.copyProperties(supportAgreement, view);
        view.setStartDate(supportAgreement.getStartDate().toString());
        view.setEndDate(supportAgreement.getEndDate().toString());
        return view;
    }

    @Override
    public Page<SupportAgreementView> findAgreementListByPage(SettledQueryParam param) {
        String queryString = param.getQueryString();

        Page<SupportAgreement> page = new Page<>(param.getPage(), param.getSize());
        QueryWrapper<SupportAgreement> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(StringUtils.hasLength(queryString), SupportAgreement::getEnterpriseName, queryString).orderByDesc(SupportAgreement::getCreateTime);
        Page<SupportAgreement> iPage1 = supportAgreementMapper.selectPage(page, queryWrapper);
        List<SupportAgreement> records = iPage1.getRecords();
        List<SupportAgreementView> collect = records.stream().map(record -> {
            SupportAgreementView view = new SupportAgreementView();
            BeanUtils.copyProperties(record, view);
            view.setStartDate(record.getStartDate().toString());
            view.setEndDate(record.getEndDate().toString());
            view.setCreateTime(record.getCreateTime().toString());
            if (!Objects.isNull(record.getPlatformId())) {
                Platforms platforms = platformsDao.getOne(record.getPlatformId());
                view.setPlatformName(platforms.getIsBase() == 1 ? "【园区平台】" + platforms.getPlatformName() : platforms.getPlatformName());
            }
            return view;
        }).collect(Collectors.toList());
        Page responsePage = new Page();
        responsePage.setRecords(collect);
        responsePage.setTotal(iPage1.getTotal());
        return responsePage;
    }

    @Override
    public void deleteOneSupportAgreement(String id) {
        supportAgreementMapper.deleteById(id);
    }

    @Override
    public void updateSupportAgreement(SupportAgreementParam param) {
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        String id = param.getId();
        SupportAgreement supportAgreement = supportAgreementMapper.selectById(id);
        if (Objects.isNull(supportAgreement)) throw new RuntimeException("update error: not found");
        BeanUtils.copyProperties(param, supportAgreement);
        supportAgreement.setStartDate(LocalDate.parse(startDate));
        supportAgreement.setEndDate(LocalDate.parse(endDate));
        supportAgreement.setId(id);
        supportAgreementMapper.updateById(supportAgreement);

        // 更新扶持金月记录
        updateSupportMonthLog(startDate.substring(0, 7), endDate.substring(0, 7), supportAgreement);
    }

    private void updateSupportMonthLog(String startDate, String endDate, SupportAgreement supportAgreement) {
        // 查询扶持金月记录
        QueryWrapper<SupportMonthLog> supportMonthLogQueryWrapper = new QueryWrapper<>();
        supportMonthLogQueryWrapper.lambda().eq(SupportMonthLog::getEnterpriseName, supportAgreement.getEnterpriseName());
        List<SupportMonthLog> supportMonthLogs = supportMonthLogMapper.selectList(supportMonthLogQueryWrapper);
        // 按条件查询企业税收数据
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getDate, startDate, endDate).eq(EnterpriseTaxPlus::getEnterpriseName, supportAgreement.getEnterpriseName());
        List<EnterpriseTaxPlus> taxList = enterpriseTaxMapper.selectList(queryWrapper);
        supportMonthLogs.forEach(supportMonthLog -> {
            for (EnterpriseTaxPlus enterpriseTax : taxList) {
                if (Objects.equals(supportMonthLog.getDate(), enterpriseTax.getDate())) {
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
                    double supportAmount = addedTax * Double.parseDouble(addedTaxRateValue) + incomeTax * Double.parseDouble(incomeTaxRateValue) + personTax * Double.parseDouble(personTaxRateValue);
                    // 查询上月结余
                    QueryWrapper<SupportMonthLog> lastMonthSupportWrapper = new QueryWrapper<>();
                    Integer lastMonthSupportYear = enterpriseTax.getYear();
                    Integer lastMonthSupportMonth = enterpriseTax.getMonth();
                    if (lastMonthSupportMonth > 1) lastMonthSupportMonth -= 1;
                    lastMonthSupportWrapper.lambda().eq(SupportMonthLog::getEnterpriseName, supportAgreement.getEnterpriseName()).eq(SupportMonthLog::getYear, lastMonthSupportYear).eq(SupportMonthLog::getMonth, lastMonthSupportMonth);
                    SupportMonthLog lastMonthSupport = supportMonthLogMapper.selectOne(lastMonthSupportWrapper);
                    // 上月结余
                    double lastMonthSurplus = 0;
                    if (!Objects.isNull(lastMonthSupport)) {
                        lastMonthSurplus = Double.parseDouble(lastMonthSupport.getSurplus());
                    }

                    // 当月结余
                    double surplus = 0;
                    if (supportAmount < 10000) surplus = supportAmount;
                    if (supportAmount > 10000) surplus = supportAmount % 10000;
                    // 当月扶持 每月清算，本月结余加上上月结余，如果结果大于等于5000 则扶持一万，当月结余为10000 - 结余金额
                    double monthAmount = supportAmount - surplus;
                    surplus = surplus + lastMonthSurplus;
                    if (surplus >= SUPPORT_LEVEL) {
                        monthAmount += 10000;
                        surplus = surplus - 10000;
                    }
                    supportMonthLog.setSupportAmount(convertValue7(supportAmount));
                    supportMonthLog.setMonthAmount(convertValue7(monthAmount));
                    supportMonthLog.setSurplus(convertValue7(surplus));
                    supportMonthLogMapper.updateById(supportMonthLog);
                    break;
                }
            }
        });
    }

    @Override
    public void saveSupportAgreement(SupportAgreementParam param) {
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        SupportAgreement supportAgreement = new SupportAgreement();
        BeanUtils.copyProperties(param, supportAgreement);
        supportAgreement.setStartDate(LocalDate.parse(startDate));
        supportAgreement.setEndDate(LocalDate.parse(endDate));
        supportAgreementMapper.insert(supportAgreement);

        // 创建扶持金月记录
        addSupportMonthLog(startDate.substring(0, 7), endDate.substring(0, 7), supportAgreement);
    }

    private void addSupportMonthLog(String startDate, String endDate, SupportAgreement supportAgreement) {
        // 按条件查询企业税收数据
        QueryWrapper<EnterpriseTaxPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(EnterpriseTaxPlus::getDate, startDate, endDate).eq(EnterpriseTaxPlus::getEnterpriseName, supportAgreement.getEnterpriseName());
        List<EnterpriseTaxPlus> taxList = enterpriseTaxMapper.selectList(queryWrapper);
        // 计算组装扶持明细
        taxList.forEach(enterpriseTax -> {
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
                double supportAmount = addedTax * Double.parseDouble(addedTaxRateValue) + incomeTax * Double.parseDouble(incomeTaxRateValue) + personTax * Double.parseDouble(personTaxRateValue);
                // 查询上月结余
                QueryWrapper<SupportMonthLog> supportWrapper = new QueryWrapper<>();
                Integer year = enterpriseTax.getYear();
                Integer month = enterpriseTax.getMonth();
                if (month > 1) month -= 1;
                supportWrapper.lambda().eq(SupportMonthLog::getEnterpriseName, supportAgreement.getEnterpriseName()).eq(SupportMonthLog::getYear, year).eq(SupportMonthLog::getMonth, month);
                SupportMonthLog lastMonthSupport = supportMonthLogMapper.selectOne(supportWrapper);
                // 上月结余
                double lastMonthSurplus = 0;
                if (!Objects.isNull(lastMonthSupport)) {
                    lastMonthSurplus = Double.parseDouble(lastMonthSupport.getSurplus());
                }

                // 当月结余
                double surplus = 0;
                if (supportAmount < 10000) surplus = supportAmount;
                if (supportAmount > 10000) surplus = supportAmount % 10000;
                // 当月扶持 每月清算，本月结余加上上月结余，如果结果大于等于5000 则扶持一万，当月结余为10000 - 结余金额
                double monthAmount = supportAmount - surplus;
                surplus = surplus + lastMonthSurplus;
                if (surplus >= SUPPORT_LEVEL) {
                    monthAmount += 10000;
                    surplus = surplus - 10000;
                }

                SupportMonthLog supportMonthLog = SupportMonthLog.builder().garden("天佳经济园").supportId(supportAgreement.getId()).year(enterpriseTax.getYear()).month(enterpriseTax.getMonth()).enterpriseName(enterpriseTax.getEnterpriseName()).supportAreas(supportAgreement.getSupportAreas()).supportProject(supportAgreement.getSupportProject()).supportAmount(convertValue7(supportAmount)).monthAmount(convertValue7(monthAmount)).surplus(convertValue7(surplus)).depositBank(supportAgreement.getDepositBank()).bankAccount(supportAgreement.getBankAccount()).date(enterpriseTax.getDate()).build();
                supportMonthLogMapper.insert(supportMonthLog);
            }
        });
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
}
