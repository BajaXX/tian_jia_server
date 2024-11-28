package com.bemore.api.controller;

import com.bemore.api.dao.*;
import com.bemore.api.dto.SupportMonthDto;
import com.bemore.api.dto.req.SupportAgreementReq;
import com.bemore.api.entity.*;
import com.bemore.api.entity.request.SettledQueryParam;
import com.bemore.api.entity.request.SupportAgreementParam;
import com.bemore.api.entity.request.TaxExportRequest;
import com.bemore.api.entity.response.PlatformSupportDataView;
import com.bemore.api.entity.response.PlatformsContractView;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.SupportAgreementService;
import com.bemore.api.util.GsonUtil;
import io.swagger.annotations.ApiOperation;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.bemore.api.entity.SupportFixData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/contract")
public class SupportContractController {

    @Value("${company.fixData}")
    private String fixDataPath;

    @Autowired
    private PlatformsDao platformsDao;

    @Autowired
    private PlatformsContractDao platformsContractDao;

    @Autowired
    private ContractsDao contractsDao;

    @Autowired
    private ContractRulesDao contractRulesDao;
    @Autowired
    private SupportContractDao supportContractDao;

    @Autowired
    private SupportAgreementService supportAgreementService;

    @Autowired
    private EnterpriseSupportLogDao enterpriseSupportLogDao;

    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private PlatformsSupportLogDao platformsSupportLogDao;

    @PostMapping("/modifySupportContract")
    @ApiOperation(value = "修改企业扶持协议")
    public String modifySupportAgreement(@RequestBody SupportAgreementParam param) {
        return GsonUtil.build("success");
    }

    // @PostMapping("/addSupportContract")
    // @ApiOperation(value = "添加企业扶持协议")
    // public String addSupportAgreement(@RequestBody SupportAgreementParam param) {
    // return GsonUtil.build("success");
    // }

    /////////////////////////// 新接口////////////////////////////////////
    @PostMapping("/addPlatform")
    @ApiOperation(value = "添加平台")
    public String addPlatform(@RequestBody Platforms param) {
        Platforms platforms = platformsDao.findPlatformsByName(param.getPlatformName());
        if (!Objects.isNull(platforms)) {
            throw new WebException(103, "平台已存在");
        }
        platforms = new Platforms();
        platforms.setPlatformName(param.getPlatformName());
        platforms.setCreateTime((Long) System.currentTimeMillis() / 1000);
        platforms.setIsBase(0);
        platformsDao.save(platforms);

        return GsonUtil.build("success");
    }

    @PostMapping("/getPlatforms")
    @ApiOperation(value = "获取所有平台")
    public String getPlatforms(@RequestBody(required = false) SettledQueryParam param) {
        Sort sort = Sort.by("createTime");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("platformName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnorePaths("createTime").withIgnorePaths("isBase");
        Platforms platforms = new Platforms();
        if (!Objects.isNull(param) && !Objects.isNull(param.getQueryString())) {
            platforms.setPlatformName(param.getQueryString());
        }
        Example<Platforms> example = Example.of(platforms, exampleMatcher);
        List<Platforms> platformsList = platformsDao.findAll(example, sort);

        return GsonUtil.build(platformsList);
    }

    @PostMapping("/getContractList")
    @ApiOperation(value = "获取所有协议")
    public String getContractList(@RequestBody(required = false) Contracts param) {

        List<Contracts> contractsList = new ArrayList<Contracts>();

        if (Objects.isNull(param)) {
            contractsList = contractsDao.findAll();
        } else {
            contractsList = contractsDao.findAllByContractNameLike("%" + param.getContractName() + "%");
        }
        return GsonUtil.build(contractsList);
        //
        // List<Contracts> contractsList = contractsDao.findAll();
        //
        // return GsonUtil.build(contractsList);
    }

    @PostMapping("/getContractRules")
    @ApiOperation(value = "获取所有协议")
    public String getContractRules(@RequestBody ContractRules contractRules) {
        List<ContractRules> contractRulesList = contractRulesDao
                .findAllByContractIdOrderByTaxStart(contractRules.getContractId());

        return GsonUtil.build(contractRulesList);
    }

    @PostMapping("/addContract")
    @ApiOperation(value = "新增协议")
    public String addContract(@RequestBody Contracts contracts) {
        contracts.setStatus(1);
        contractsDao.save(contracts);
        return GsonUtil.build("success");
    }

    @PostMapping("/setPlatform")
    @ApiOperation(value = "修改基础平台")
    public String setPlatform(@RequestBody Platforms param) {
        platformsDao.resetBasePlatform();
        platformsDao.setBasePlatform(param.getId());

        return GsonUtil.build("success");
    }

    @PostMapping("/delPlatform")
    @ApiOperation(value = "获取所有平台")
    public String delPlatform(@RequestBody Platforms param) {
        platformsDao.deleteById(param.getId());

        return GsonUtil.build("success");
    }

    @PostMapping("/addPlatformContract")
    @ApiOperation(value = "添加平台协议")
    public String addPlatformContract(@RequestBody PlatformsContract param) {
        // //查询协议期是否冲突
        int count = platformsContractDao.getContractCountByTax(param.getPlatformId(), param.getIsFund(),
                param.getAgreementStart(), param.getAgreementEnd());
        if (count > 0) {
            throw new WebException(104, "该平台有未结束的协议，请先作废原协议再新增协议");
        }
        param.setCstatus(1);
        platformsContractDao.saveAndFlush(param);
        return GsonUtil.build("success");
    }

    @PostMapping("/addContractRule")
    @ApiOperation(value = "添加平台协议")
    public String addContractRule(@RequestBody ContractRules param) {
        // 查询协议期是否冲突
        int count = contractRulesDao.getContractCountByTax(param.getContractId(), param.getTaxStart(),
                param.getTaxEnd());
        if (count > 0) {
            throw new WebException(104, "平台协议阶梯不可以有交集");
        }
        contractRulesDao.saveAndFlush(param);
        return GsonUtil.build("success");
    }

    @PostMapping("/cancelContract")
    @ApiOperation(value = "作废平台协议")
    public String cancelContract(@RequestBody PlatformsContract param) {
        // 查询
        PlatformsContract platformsContract = platformsContractDao.getOne(param.getId());
        if (Objects.isNull(platformsContract) || platformsContract.getCstatus() != 1) {
            throw new WebException(105, "未找到对应的平台协议，请刷新页面再试。");
        }
        // todo 需要计算之前的数据
        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyyyMMdd");
        platformsContractDao.updateCstatus(param.getId(), 2, Integer.valueOf(date));
        return GsonUtil.build("success");
    }

    @PostMapping("/cancelSupportContract")
    @ApiOperation(value = "作废平台协议")
    public String cancelSupportContract(@RequestBody SupportContract param) {
        // 查询
        SupportContract supportContract = supportContractDao.getOne(param.getId());
        if (Objects.isNull(supportContract) || supportContract.getStatus() != 1) {
            throw new WebException(105, "未找到对应的企业扶持协议，请刷新页面再试。");
        }
        // todo 需要计算之前的数据
        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyyyMMdd");
        supportContractDao.updateStatus(param.getId(), 2, Integer.valueOf(date));
        return GsonUtil.build("success");
    }

    @PostMapping("/getPlatformContract")
    @ApiOperation(value = "获取平台协议")
    public String getPlatformContract(@RequestBody(required = false) PlatformsContract param) {
        List<PlatformsContract> platformsContractList = new ArrayList<>();
        if (Objects.isNull(param) || Objects.isNull(param.getPlatformId())) {
            Sort sort = Sort.by("agreementStart");
            platformsContractList = platformsContractDao.findAll(sort);
        } else {
            platformsContractList = platformsContractDao
                    .findAllPlatformsContractByPlatformIdOrderByCstatusAscAgreementStart(param.getPlatformId());
        }

        return GsonUtil.build(platformsContractList);
    }

    @PostMapping("/getSupportContractList")
    @ApiOperation(value = "获取平台协议")
    public String getSupportContractList(@RequestBody(required = false) SupportContract param) {
        List<SupportContract> supportContractList = new ArrayList<>();

        if (Objects.isNull(param)) {
            Sort sort = Sort.by("startDate");
            supportContractList = supportContractDao.findAll(sort);
        } else {
            supportContractList = supportContractDao
                    .findAllByEnterpriseNameLikeOrderByStatusAscStartDateAsc("%" + param.getEnterpriseName() + "%");
        }
        return GsonUtil.build(supportContractList);
    }

    @PostMapping("/getSupportContractEnterpriseList")
    @ApiOperation(value = "获取平台协议")
    public String getSupportContractEnterpriseList(@RequestBody(required = false) SupportContract param) {
        List<String> supportContractList = new ArrayList<String>();

        if (Objects.isNull(param)) {
            supportContractList = supportContractDao.getSupportEnterpriseList();
        } else {
            supportContractList = supportContractDao.getSupportEnterpriseByName("%" + param.getEnterpriseName() + "%");
        }
        return GsonUtil.build(supportContractList);
    }

    @PostMapping("/addSupportContract")
    @ApiOperation(value = "添加平台协议")
    public String addSupportContract(@RequestBody SupportContract param) {
        // //查询协议期是否冲突
        int count = supportContractDao.getContractCountByDate(param.getEnterpriseName(), param.getStartDate(),
                param.getEndDate());
        if (count > 0) {
            throw new WebException(104, "该企业有未结束的协议，请先作废原协议再新增协议");
        }

        Enterprise enterprise = enterpriseDao.findByName(param.getEnterpriseName());
        if (enterprise == null) {
            throw new WebException(105, "未找到对应的企业");
        }

        param.setStatus(1);
        param.setEnterpriseNo(enterprise.getEnterpriseNo());
        supportContractDao.saveAndFlush(param);
        return GsonUtil.build("success");
    }

    @PostMapping("/computeContract")
    @ApiOperation(value = "计算扶持协议")
    public String computeContract(@RequestBody SupportAgreementReq supportAgreementReq) {

        supportAgreementService.computeContract(supportAgreementReq);
        return GsonUtil.build("success");
    }

    @PostMapping("/recomputeContract")
    @ApiOperation(value = "重新计算扶持协议")
    public String recomputeContract(@RequestBody SupportAgreementReq supportAgreementReq) {

        supportAgreementService.recomputeContract(supportAgreementReq);
        return GsonUtil.build("success");
    }

    @PostMapping("/computePlatform")
    @ApiOperation(value = "重新计算扶持协议")
    public String computePlatform(@RequestBody SupportAgreementReq supportAgreementReq) {

        supportAgreementService.computePlatform(supportAgreementReq);
        return GsonUtil.build("success");
    }

    @PostMapping("/getEnterpriseSupportList")
    @ApiOperation(value = "获取企业扶持列表")
    public String getEnterpriseSupportList(@RequestBody SupportAgreementReq supportAgreementReq) {
        String enterpriseName = supportAgreementReq.getEnterpriseName();
        int startDate = Integer.valueOf(supportAgreementReq.getStartDate());
        int endDate = Integer.valueOf(supportAgreementReq.getEndDate());

        // 1. 读取修正数据文件
        ObjectMapper mapper = new ObjectMapper();
        List<SupportFixData> fixDataList = new ArrayList<>();
        try {
            File file = new File(fixDataPath);
            if (file.exists() && file.length() > 0) {
                fixDataList = mapper.readValue(file, new TypeReference<List<SupportFixData>>() {
                });
            }
        } catch (IOException e) {
            log.error("读取扶持修正数据文件失败", e);
        }

        // 2. 获取企业支持列表数据
        List<EnterpriseSupportLog> list;
        if (Objects.nonNull(enterpriseName) && !"".equals(enterpriseName)) {
            list = enterpriseSupportLogDao
                    .findByEnterpriseNameLikeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(
                            "%" + supportAgreementReq.getEnterpriseName() + "%", startDate, endDate);
        } else {
            list = enterpriseSupportLogDao.findByDateGreaterThanEqualAndDateLessThanEqualOrderByDate(startDate,
                    endDate);
        }

        // 3. 为每条记录设置fixValue值
        for (EnterpriseSupportLog log : list) {
            String month = String.valueOf(log.getDate()); // 202301
            Optional<SupportFixData> fixData = fixDataList.stream().filter(
                    data -> data.getCompanyName().equals(log.getEnterpriseName()) && data.getFixMonth().equals(month)) // 直接比较完整的日期格式
                    .findFirst();

            // 如果找到修正数据，设置fixValue
            log.setFixValue(fixData.map(SupportFixData::getFixAmount).orElse(null));
        }

        return GsonUtil.build(list);
    }

    @PostMapping(value = "/uploadSupportFiles")
    public String uploadSupportFiles(@RequestParam MultipartFile file) {
        System.out.println(file.getName());
        // enterpriseTaxService.importEnterpriseTaxService(file,date);
        return GsonUtil.build("success");
    }

    @PostMapping("/getPlatformSupportList")
    @ApiOperation(value = "获取平台扶持列表")
    public String getPlatformSupportList(@RequestBody SupportAgreementReq supportAgreementReq) {

        String enterpriseName = supportAgreementReq.getEnterpriseName();
        int startDate = Integer.valueOf(supportAgreementReq.getStartDate());
        int endDate = Integer.valueOf(supportAgreementReq.getEndDate());

        List<TPlatformsSupportLog> list = new ArrayList<>();
        if (Objects.nonNull(enterpriseName) && !"".equals(enterpriseName)) {
            list = platformsSupportLogDao.findAllByPlatformIdAndDateBetweenOrderByDate(
                    supportAgreementReq.getEnterpriseName(), startDate, endDate);
        } else {
            list = platformsSupportLogDao.findAllByDateBetweenOrderByDate(startDate, endDate);
        }

        return GsonUtil.build(list);
    }

    @PostMapping("/getSupportMonth")
    @ApiOperation(value = "获取扶持月度汇总")
    public String getSupportMonth(@RequestBody TaxExportRequest body) {

        int startDate = Integer.valueOf(body.getStartDate());

        List<Object[]> list = enterpriseSupportLogDao.getSupportMonthByDate(startDate);
        List<Object[]> platformlist = platformsSupportLogDao.getSupportMonthByDate(startDate);

        List<SupportMonthDto> dtos = new ArrayList<>();

        list.addAll(platformlist);

        for (Object[] result : list) {
            String supportAreas = (String) result[0];
            String supportProject = (String) result[1];
            double totalAmount = (double) result[2];

            SupportMonthDto dto = new SupportMonthDto();
            dto.setSupportAreas(supportAreas);
            dto.setSupportProject(supportProject);
            dto.setTotalAmount(totalAmount);

            dtos.add(dto);
        }

        return GsonUtil.build(dtos);
    }
}
