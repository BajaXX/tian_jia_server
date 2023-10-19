package com.bemore.api.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.constant.CommonConstants;
import com.bemore.api.dao.*;
import com.bemore.api.dao.mapper.EnterpriseMapper;
import com.bemore.api.dao.mapper.MemberLogMapper;
import com.bemore.api.dao.mapper.MemberMapper;
import com.bemore.api.dao.mapper.PersonLogMapper;
import com.bemore.api.dto.EnterprisePageDto;
import com.bemore.api.dto.EnterpriseQueryDto;
import com.bemore.api.dto.NewEnterpriseDto;
import com.bemore.api.dto.PersonDto;
import com.bemore.api.entity.*;
import com.bemore.api.entity.request.ReportBaseRequest;
import com.bemore.api.entity.response.HomeStatView;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.BusinessService;
import com.bemore.api.service.EnterpriseAddrService;
import com.bemore.api.service.PersonManageService;
import com.google.gson.Gson;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Table;
import com.spire.doc.collections.ParagraphCollection;
import com.spire.doc.documents.Paragraph;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import com.bemore.api.util.ConvertUtil;
import com.bemore.api.util.GsonUtil;
import com.bemore.api.util.Util;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private EnterpriseLogDao enterpriseLogDao;

    @Autowired
    private PersonLogDao personLogDao;

    @Autowired
    private MemberLogDao memberLogDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectLogDao projectLogDao;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private EnterpriseAddrService enterpriseAddrService;

    @Autowired
    private PersonManageService personManageService;

    @Autowired
    private PersonLogMapper personLogMapper;

    @Autowired
    private MemberLogMapper memberLogMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private TransferLogDao transferLogDao;

    @GetMapping("/reportEnterprise")
    @ApiOperation(value = "导出所有企业信息")
    public void reportEnterprise(ReportBaseRequest request, HttpServletResponse response) {
        businessService.reportEnterpriseService(request, response);
    }

    @GetMapping("/cleanPaymentDate")
    @ApiOperation(value = "清空启缴日期")
    public void cleanPaymentDate() {
        List<Enterprise> enterpriseList = enterpriseMapper.selectList(new QueryWrapper<Enterprise>());
        enterpriseList.forEach(enterprise -> {
            enterprise.setPaymentDate(null);
            enterpriseMapper.updateById(enterprise);
        });
    }

    @GetMapping("/cleanAll")
    @ApiOperation(value = "清空企业列表")
    public void cleanTable() {
        enterpriseMapper.delete(null);
    }

    @PostMapping("/supplementary")
    @ApiOperation(value = "根据excel补充注册地址 入驻日期，机构类型和投资类型的信息")
    public void supplementary(@RequestParam("file") MultipartFile file) {
        businessService.supplementary(file);
    }

    @GetMapping("/homeStat")
    @ApiOperation(value = "首页企业统计")
    public String getHomeStat() {
        return GsonUtil.build(businessService.getHomeStat());
    }

    @PostMapping("/importEnterpriseBatch")
    @ApiOperation(value = "批量导入企业")
    public void importEnterpriseBatch(@RequestParam("file") MultipartFile file) {
        businessService.importEnterpriseBatch(file);
    }

    // 完成注销（已注销）
    @PostMapping("/finishCancelEnterprise")
    public String finishCancelEnterprise(@RequestParam String id) {
        // 更新当前表状态为已注销
        Enterprise enterprise = enterpriseDao.findById(id).get();
        enterprise.setProcess(0);
        enterpriseDao.save(enterprise);
        return GsonUtil.build();
    }

    // 终止注销
    @PostMapping("/terminationCancelEnterprise")
    public String terminationCancel(@RequestParam String id) {
        // 更新当前表状态为正常
        Enterprise enterprise = enterpriseDao.findById(id).get();
        enterprise.setProcess(5);
        enterpriseDao.save(enterprise);
        return GsonUtil.build();
    }


    // 注销企业（注销中）
    @PostMapping("/cancelEnterprise")
    public String cancelEnterprise(@RequestParam String id) {
        // 更新当前表状态为注销
        Enterprise enterprise = enterpriseDao.findById(id).get();
        enterprise.setProcess(4);
        enterpriseDao.save(enterprise);
        return GsonUtil.build();
    }

    // 新开企业
    @PostMapping("/newEnterprise")
    public String saveEnterprise(@RequestBody NewEnterpriseDto newEnterprise) {

        QueryWrapper<Enterprise> wrapper = new QueryWrapper<>();
        wrapper.eq("name", newEnterprise.getName());
        Enterprise one = enterpriseMapper.selectOne(wrapper);
        if (one != null) {
            throw new WebException(500, "当前企业已入驻，不能再次办理新开");
        }
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(newEnterprise, enterprise);
        if (StringUtils.contains(newEnterprise.getType(), "合伙")) {
            enterprise.setIsPartner(CommonConstants.IS_PARTNER.YES.getCode());
        }
        enterprise.setProcess(1);
        enterprise = enterpriseDao.save(enterprise);
        return GsonUtil.build(enterprise.getId());
    }

    @GetMapping("/getAllCompanyType")
    public String getAllCompanyType() {
        return GsonUtil.build(CommonConstants.ALL_ENTERPRISE_TYPE);
    }

    @PostMapping("/finishNewEnterprise")
    public String finishNewEnterprise(@RequestParam String id) {
        // 更新当前表状态为正常
        Enterprise enterprise = enterpriseDao.findById(id).get();
        enterprise.setProcess(5);
        enterpriseDao.save(enterprise);
        return GsonUtil.build();
    }

    @PostMapping("/cancelNewEnterprise")
    public String cancelNewEnterprise(@RequestParam String id) {
        // 删除数据
        enterpriseDao.deleteById(id);
        personDao.deleteByEnterprise(id);
        memberDao.deleteByEnterprise(id);
        projectDao.deleteByEnterpriseId(id);
        return GsonUtil.build();
    }

    // 迁入企业
    @PostMapping("/importEnterprise")
    public String importEnterprise(@RequestBody Enterprise enterprise) {
        Enterprise enter = enterpriseDao.findByName(enterprise.getName());
        if (!Objects.isNull(enter)) {
            throw new WebException(100, "迁入企业已存在，请勿重复操作。");
        }

        enterprise = enterpriseDao.save(enterprise);
        EnterpriseLog log = ConvertUtil.convert(enterprise);
        log.setEnterpriseId(enterprise.getId());
        log.setValid("0");
        log.setCreateTime(Util.getDateValue());
        enterpriseLogDao.save(log);
        return GsonUtil.build(enterprise.getId());
    }

    @PostMapping("/importEnterpriseInfo")
    public String importEnterpriseInfo(@RequestParam String id) {
        Enterprise enterprise = enterpriseDao.findById(id).get();
        EnterpriseLog log = ConvertUtil.convert(enterprise);
        log.setValid("0");
        log.setCreateTime(Util.getDateValue());
        enterpriseLogDao.save(log);
        return GsonUtil.build();
    }

    @PostMapping("/finishImport")
    public String finishImport(@RequestParam String id) {
        // 履历生效
        enterpriseLogDao.validLog(id);
        // 复制人员股东信息
        List<Person> persons = personDao.findByEnterpriseId(id);
        for (Person person : persons) {
            PersonLog log = ConvertUtil.convert(person);
            log.setValid("1");
            log.setCreateTime(Util.getDateValue());
            personLogDao.save(log);
        }
        List<Member> members = memberDao.findByEnterpriseId(id);
        for (Member member : members) {
            MemberLog log = ConvertUtil.convert(member);
            log.setValid("1");
            log.setCreateTime(Util.getDateValue());
            memberLogDao.save(log);
        }
        Project project = projectDao.findByEnterpriseId(id);
        if (project != null) {
            ProjectLog projectLog = new ProjectLog();
            BeanUtils.copyProperties(project, projectLog);
            projectLog.setValid("1");
            projectLog.setCreateTime(Util.getDateValue());
            projectLogDao.save(projectLog);
        }
        // 更新当前表状态为正常
        Enterprise enterprise = enterpriseDao.findById(id).get();
        enterprise.setProcess(5);
        enterpriseDao.save(enterprise);
        return GsonUtil.build();
    }

    @PostMapping("/cancelImport")
    public String cancelImport(@RequestParam String id) {
        // 删除履历
        enterpriseLogDao.deleteByEnterprise(id);
        // 删除数据
        enterpriseDao.deleteById(id);
        personDao.deleteByEnterprise(id);
        memberDao.deleteByEnterprise(id);
        projectDao.deleteByEnterpriseId(id);
        return GsonUtil.build();
    }

    // 变更企业
    @PostMapping("/updateEnterprise")
    public String updateEnterprise(@RequestParam String id) {
        Enterprise enterprise = enterpriseDao.findById(id).get();
        // 复制企业人员股东信息留档以便恢复
        List<Person> persons = personDao.findByEnterpriseId(id);
        for (Person person : persons) {
            PersonLog log = new PersonLog();
            BeanUtils.copyProperties(person, log);
            log.setValid("0");
            log.setCreateTime(Util.getDateValue());
            personLogDao.save(log);
        }
        List<Member> members = memberDao.findByEnterpriseId(id);
        for (Member member : members) {
            MemberLog log = new MemberLog();
            BeanUtils.copyProperties(member, log);
            log.setValid("0");
            log.setCreateTime(Util.getDateValue());
            memberLogDao.save(log);
            member.setOldPutAmount(member.getPutAmount());
            memberDao.save(member);
        }
//        Project project = projectDao.findByEnterpriseId(id);
//        if (project != null) {
//            ProjectLog projectLog = new ProjectLog();
//            BeanUtils.copyProperties(project, projectLog);
//            projectLog.setValid("0");
//            projectLog.setCreateTime(Util.getDateValue());
//            projectLogDao.save(projectLog);
//        }
//        EnterpriseLog log = ConvertUtil.convert(enterprise);
        EnterpriseLog log = new EnterpriseLog();
        BeanUtils.copyProperties(enterprise, log);
        log.setValid("0");
        log.setEnterpriseId(enterprise.getId());
        log.setCreateTime(Util.getDateValue());
        enterpriseLogDao.save(log);
        // 更新变更状态
        enterprise.setProcess(3);
        enterpriseDao.save(enterprise);
        return GsonUtil.build();
    }

    @PostMapping("/finishUpdateEnterprise")
    public String finishUpdateEnterprise(@RequestParam String id) {
        // 履历生效
        enterpriseLogDao.validLog(id);
        personLogDao.validLog(id);
        memberLogDao.validLog(id);
        transferLogDao.deleteByEnterpriseId(id);
//        projectLogDao.validLog(id);
        // 更新当前表状态为正常
        Enterprise enterprise = enterpriseDao.findById(id).get();
        enterprise.setProcess(5);
        enterpriseDao.save(enterprise);
        return GsonUtil.build();
    }

    @PostMapping("/cancelUpdateEnterprise")
    public String cancelUpdateEnterprise(@RequestParam String id) {
        List<EnterpriseLog> logs = enterpriseLogDao.findByEnterpriseIdAndValid(id, "0");
        if (logs.isEmpty()) throw new WebException(102, "数据不完全,请联系管理员");
        EnterpriseLog log = logs.get(0);
        // 删除变更数据
        try {
//            enterpriseDao.deleteById(id);
            personDao.deleteByEnterprise(id);
            memberDao.deleteByEnterprise(id);
            projectDao.deleteByEnterpriseId(id);


        } catch (EmptyResultDataAccessException erdae) {
            System.out.println(erdae);
        }
        // 恢复数据

//        Enterprise enterprise = ConvertUtil.convert(log);
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(log, enterprise);
        enterprise.setId(log.getEnterpriseId());
        enterpriseDao.save(enterprise);
        List<PersonLog> persons = personLogDao.findByEnterpriseIdAndValid(id, "0");
        for (PersonLog personLog : persons) {
            Person person = new Person();
            BeanUtils.copyProperties(personLog, person);
            personDao.save(person);
        }
        List<MemberLog> members = memberLogDao.findByEnterpriseIdAndValid(id, "0");
        for (MemberLog memberLog : members) {
            Member member = new Member();
            BeanUtils.copyProperties(memberLog, member);
            memberDao.save(member);
        }
//        List<ProjectLog> projectLogs = projectLogDao.findByEnterpriseIdAndValid(id, "0");
//        for (ProjectLog projectLog : projectLogs) {
//            Project project = new Project();
//            BeanUtils.copyProperties(projectLog, project);
//            projectDao.save(project);
//        }
        // 删除履历数据
        enterpriseLogDao.deleteByEnterprise(id);
        personLogDao.deleteByEnterprise(id);
        memberLogDao.deleteByEnterprise(id);
        //        projectLogDao.deleteByEnterprise(id);
        transferLogDao.deleteByEnterpriseId(id);
        return GsonUtil.build();
    }

    // 日常更新企业信息
    @PostMapping("/saveEnterprise")
    public String saveEnterprise(@RequestBody Enterprise enterprise) {
        if (StringUtils.contains(enterprise.getType(), "合伙")) {
            enterprise.setIsPartner(CommonConstants.IS_PARTNER.YES.getCode());
        }
        try {
            enterpriseDao.save(enterprise);
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                throw new WebException(100, "企业名称重复，请检查！");
            } else {
                e.printStackTrace();
            }
        }
        return GsonUtil.build();
    }

    @PostMapping("/saveEnterpriseLog")
    public String saveEnterpriseLog(@RequestBody EnterpriseLog enterprise) {
        if (StringUtils.contains(enterprise.getType(), "合伙")) {
            enterprise.setIsPartner(CommonConstants.IS_PARTNER.YES.getCode());
        }
        enterpriseLogDao.save(enterprise);
        return GsonUtil.build();
    }

    @PostMapping("/fetchEnterprises")
    public String fetchEnterprises(Enterprise enterprise) {

        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        if (enterprise.getProcess() != null) {
            queryWrapper.eq("process", enterprise.getProcess());
        }
        if (StringUtils.isNotEmpty(enterprise.getName())) {
            queryWrapper.like("name", enterprise.getName() + "%");
        }
        return GsonUtil.build(enterpriseMapper.selectList(queryWrapper));
    }

    @PostMapping("/getEnterprises")
    public String getEnterprises(@RequestBody EnterpriseQueryDto enterpriseQueryDto) {
        EnterprisePageDto result = businessService.getEnterprises(enterpriseQueryDto);
        return GsonUtil.build(result);
    }

    @PostMapping("/fetchEnterprise")
    public String fetchEnterprise(@RequestParam String id) {
        Enterprise enterprise = enterpriseDao.findById(id).orElse(null);
        return GsonUtil.build(enterprise);
    }

    @PostMapping("/fetchEnterpriseLog")
    public String fetchEnterpriseLog(@RequestParam String id) {
//        List<EnterpriseLog> enterprises = enterpriseLogDao.findAll(new Specification<EnterpriseLog>() {
//            @Override
//            public Predicate toPredicate(Root<EnterpriseLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                List<Predicate> list = new ArrayList<Predicate>();
//
//                list.add(cb.equal(root.<String>get("enterpriseId"), id));
//                list.add(cb.equal(root.<String>get("valid"), "0"));
//
//                Predicate[] p = new Predicate[list.size()];
//                return cb.and(list.toArray(p));
//            }
//        });
//        if (enterprises != null && enterprises.size() > 0) {
//            return GsonUtil.build(enterprises.get(0));
//        }

        EnterpriseLog enterpriseLog = enterpriseLogDao.findByEnterpriseId(id);
        if (Objects.isNull(enterpriseLog)) {
            return GsonUtil.build(1, "数据异常", null);

        } else {
            return GsonUtil.build(enterpriseLog);
        }
    }

    @PostMapping("/deleteEnterprise")
    public String deleteEnterprise(@RequestParam String id) {
        personDao.deleteByEnterprise(id);
        memberDao.deleteByEnterprise(id);
        enterpriseDao.deleteById(id);
        return GsonUtil.build();
    }

    @PostMapping("/saveMember")
    public String saveMember(@RequestBody Member member) {
        TPerson tPerson = new TPerson();
        BeanUtils.copyProperties(member, tPerson);
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("idcard", member.getIdcard());

        QueryWrapper<TPerson> wrapper = new QueryWrapper<>();
        wrapper.eq("idcard", tPerson.getIdcard());
        TPerson one = personManageService.getOne(wrapper);
        if (one == null) {
            personManageService.save(tPerson);
        } else {
            tPerson.setId(one.getId());
            personManageService.updateById(tPerson);
        }
        Member memberFromDb = memberMapper.selectOne(queryWrapper);
        if (memberFromDb != null) {
            member.setId(memberFromDb.getId());
            memberMapper.updateById(member);
            return GsonUtil.build();
        }
        memberDao.save(member);
        return GsonUtil.build();
    }

    @PostMapping("/saveTransfer")
    public String saveTransfer(@RequestBody TransferLog transferLog) {
        if (Objects.isNull(transferLog.getEnterpriseId())) throw new WebException(101, "数据错误");
        Member member = memberDao.getOne(transferLog.getOldStock());
        if (Objects.isNull(member)) throw new WebException(102, "未找到原股东信息");
        member.setPutAmount(String.valueOf(Double.valueOf(member.getPutAmount()) - transferLog.getAmount()));
        transferLog.setOldStockName(member.getName());
        transferLog.setOldStockIdcard(member.getIdcard());
        memberDao.save(member);
        Member newMember = new Member();
        if (transferLog.getNewStock().equals("0")) {
            newMember.setName(transferLog.getNewStockName());
            newMember.setPutAmount(String.valueOf(transferLog.getAmount()));
            newMember.setEnterpriseId(transferLog.getEnterpriseId());

        } else {
            newMember = memberDao.getOne(transferLog.getNewStock());
            if (Objects.isNull(newMember)) throw new WebException(102, "未找到新股东信息");
            newMember.setPutAmount(String.valueOf(Double.valueOf(newMember.getPutAmount()) + transferLog.getAmount()));
        }
        newMember.setPutType(transferLog.getPutType());
        newMember.setIsStock(1);
        memberDao.save(newMember);
        transferLog.setNewStock(newMember.getId());
        transferLog.setNewStockName(newMember.getName());
        transferLog.setNewStockIdcard(newMember.getIdcard());
        transferLog.setTransType(1);
        transferLogDao.save(transferLog);

        return GsonUtil.build(transferLog);
    }

    @PostMapping("/deleteTransfer")
    public String deleteTransfer(@RequestBody TransferLog transferLog) {
        if (Objects.isNull(transferLog.getEnterpriseId())) throw new WebException(101, "数据错误");
        if (Objects.isNull(transferLog.getId())) throw new WebException(101, "数据错误");
        Member member = memberDao.getOne(transferLog.getOldStock());
        if (Objects.isNull(member)) throw new WebException(102, "未找到原股东信息");
        member.setPutAmount(String.valueOf(Double.valueOf(member.getPutAmount()) + transferLog.getAmount()));
        memberDao.save(member);
        Member newMember = memberDao.getOne(transferLog.getNewStock());
        if (Objects.isNull(newMember)) throw new WebException(102, "未找到新股东信息");
        if (Double.valueOf(newMember.getPutAmount()) - transferLog.getAmount() == 0) {
            memberDao.delete(newMember);
        } else {
            newMember.setPutAmount(Double.valueOf(newMember.getPutAmount()) - transferLog.getAmount() + "");
            memberDao.save(newMember);
        }

        transferLogDao.deleteById(transferLog.getId());

        return GsonUtil.build("ok");
    }

    @PostMapping("/incrAndDecr")
    public String incrAndDecr(@RequestBody TransferLog transferLog) {
        if (Objects.isNull(transferLog.getEnterpriseId())) throw new WebException(101, "数据错误");
        Member member = new Member();
        if (transferLog.getOldStock().equals("0")) {
            member.setName(transferLog.getOldStockName());
            member.setPutAmount(String.valueOf(transferLog.getAmount()));
            member.setEnterpriseId(transferLog.getEnterpriseId());
            member.setIsStock(1);
            member.setPutType("货币");
        } else {
            member = memberDao.getOne(transferLog.getOldStock());
            if (Objects.isNull(member)) throw new WebException(102, "未找到原股东信息");
            member.setPutAmount(Double.valueOf(member.getPutAmount()) + transferLog.getAmount() + "");

        }
        memberDao.save(member);
        Enterprise enterprise = enterpriseDao.getOne(transferLog.getEnterpriseId());
        if (Objects.isNull(enterprise)) throw new WebException(102, "未找到公司信息");
        transferLog.setOldStock(member.getId());
        transferLog.setOldStockName(member.getName());
        transferLog.setOldStockIdcard(member.getIdcard());
        transferLog.setTransType(2);
        transferLogDao.save(transferLog);
        enterprise.setCapital(Double.valueOf(enterprise.getCapital()) + transferLog.getAmount() + "");
        enterpriseDao.save(enterprise);


        return GsonUtil.build("ok");
    }

    @PostMapping("/deleteIncrAndDecr")
    public String deleteIncrAndDecr(@RequestBody TransferLog transferLog) {
        if (Objects.isNull(transferLog.getEnterpriseId())) throw new WebException(101, "数据错误");
        if (Objects.isNull(transferLog.getId())) throw new WebException(101, "数据错误");
        Member member = memberDao.getOne(transferLog.getOldStock());
        if (Objects.isNull(member)) throw new WebException(102, "未找到股东信息");
        Enterprise enterprise = enterpriseDao.getOne(transferLog.getEnterpriseId());

        enterprise.setCapital(Double.valueOf(enterprise.getCapital()) - transferLog.getAmount() + "");
        enterpriseDao.save(enterprise);


        memberDao.save(member);
        if (Double.valueOf(member.getPutAmount()).compareTo(transferLog.getAmount()) == 0) {
            memberDao.delete(member);
        } else {
            member.setPutAmount(Double.valueOf(member.getPutAmount()) - transferLog.getAmount() + "");
            memberDao.save(member);
        }

        transferLogDao.deleteById(transferLog.getId());

        return GsonUtil.build("ok");
    }

    @PostMapping("/getIncrAndDecr")
    public String getIncrAndDecr(@RequestParam final String enterpriseId) {

        List<TransferLog> list = transferLogDao.findTransferLogsByEnterpriseIdAndTransType(enterpriseId, 2);

        return GsonUtil.build(list);
    }

    @PostMapping("/getTransferList")
    public String getTransferList(@RequestParam final String enterpriseId) {

        List<TransferLog> list = transferLogDao.findTransferLogsByEnterpriseIdAndTransType(enterpriseId, 1);

        return GsonUtil.build(list);
    }

    @PostMapping("/deleteMember")
    public String deleteMember(@RequestParam String id) {
        memberDao.deleteById(id);
        return GsonUtil.build();
    }

    @PostMapping("/fetchMember")
    public String fetchMember(@RequestParam String id) {
        Member member = memberDao.findById(id).get();
        return GsonUtil.build(member);
    }

    @PostMapping("/fetchMembers")
    public String fetchMembers(@RequestParam final String enterprise) {
        List<Member> members = memberDao.findAll(new Specification<Member>() {
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                list.add(cb.equal(root.<String>get("enterpriseId"), enterprise));

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        });
        return GsonUtil.build(members);
    }

    @PostMapping("/fetchMembersLog")
    public String fetchMembersLog(@RequestParam final String enterprise) {
        List<MemberLog> members = memberLogDao.findByEnterpriseIdAndValid(enterprise, "0");
        return GsonUtil.build(members);
    }

    @PostMapping("/fetchPersonsLog")
    public String fetchPersonsLog(@RequestParam final String enterprise) {
        List<PersonLog> personLogs = personLogDao.findByEnterpriseIdAndValid(enterprise, "0");
        return GsonUtil.build(personLogs);
    }

    @PostMapping("/savePerson")
    public String savePerson(@RequestBody PersonDto personDto) {
        TPerson tPerson = new TPerson();
        BeanUtils.copyProperties(personDto, tPerson);
        QueryWrapper<TPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("idcard", tPerson.getIdcard());
        TPerson one = personManageService.getOne(queryWrapper);
        if (one == null) {
            personManageService.save(tPerson);
        } else {
            tPerson.setId(one.getId());
            personManageService.updateById(tPerson);
        }
        businessService.savePerson(personDto);
        return GsonUtil.build();
    }

    @PostMapping("/deletePerson")
    public String deletePerson(@RequestParam String id) {
        personDao.deleteById(id);
        return GsonUtil.build();
    }

    @PostMapping("/fetchPerson")
    public String fetchPerson(@RequestParam String id) {
        Person person = personDao.findById(id).get();
        return GsonUtil.build(person);
    }

    @PostMapping("/fetchPersons")
    public String fetchPersons(@RequestParam final String enterprise) {
        List<Person> persons = personDao.findAll(new Specification<Person>() {
            @Override
            public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                list.add(cb.equal(root.<String>get("enterpriseId"), enterprise));

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        });
        return GsonUtil.build(persons);
    }

    @GetMapping("/fetchPersonLogByIdCard")
    public String fetchPersonByIdCard(Person person) {
        QueryWrapper<PersonLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("idcard", person.getIdcard()).eq("enterprise_id", person.getEnterpriseId()).eq("valid", 0).orderByDesc("create_time").last("limit 1");
        PersonLog person1 = personLogMapper.selectOne(queryWrapper);
        return GsonUtil.build(person1);
    }

    @GetMapping("/fetchMemberLogByIdCard")
    public String fetchMemberLogByIdCard(Member member) {
        QueryWrapper<MemberLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("idcard", member.getIdcard()).eq("enterprise_id", member.getEnterpriseId()).eq("valid", 0).orderByDesc("create_time").last("limit 1");
        MemberLog person1 = memberLogMapper.selectOne(queryWrapper);
        return GsonUtil.build(person1);
    }

    /**
     * @return
     * @author : jackie.yao
     * @date: 2021/3/5 3:16 PM
     * @description save project material
     */
    @PostMapping("projectMaterial")
    public String saveProjectMaterial(@RequestBody Project project) {
        Project existedProject = projectDao.findByEnterpriseId(project.getEnterpriseId());
        if (existedProject != null) {
            project.setId(existedProject.getId());
        }
        projectDao.save(project);
        return GsonUtil.build();
    }

    /**
     * @return
     * @author : jackie.yao
     * @date: 2021/3/5 4:23 PM
     * @description fetch project material via unique id
     */
    @GetMapping("/fetchProjectMaterial")
    public String fetchProjectMaterial(@RequestParam String id) {
        Project project = projectDao.findById(id).isPresent() ? projectDao.findById(id).get() : new Project();
        return GsonUtil.build(project);
    }

    /**
     * @return
     * @author : jackie.yao
     * @date: 2021/3/5 4:14 PM
     * @description fetch relative project material in batch via enterpriseId
     */
    @GetMapping("/fetchProjectMaterials")
    public String fetchProjectMaterials(@RequestParam final String enterpriseId) {
        List<Project> projects = projectDao.findAll(new Specification<Project>() {
            @Override
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = Lists.newArrayList();

                list.add(cb.equal(root.<String>get("enterpriseId"), enterpriseId));

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        });
        return GsonUtil.build(projects);
    }


    @PostMapping(value = "/updateInfoTable")
    public String updateInfoTable(@RequestParam MultipartFile file, @RequestParam String enterpriseId) {

        Document doc = new Document();

        try {
            doc.loadFromStream(file.getInputStream(), FileFormat.Auto);


            Enterprise enterprise = new Enterprise();

            Table table1 = doc.getSections().get(0).getTables().get(0);
            String enterpriseName = table1.getRows().get(1).getCells().get(2).getParagraphs().get(0).getText();
            String capital = table1.getRows().get(4).getCells().get(2).getParagraphs().get(0).getText();

            enterprise.setName(enterpriseName.trim());
            enterprise.setCapital(capital.replaceAll("[^0-9]", ""));

            int i = 6;
            while (true) {
                String next = table1.getRows().get(i).getCells().get(1).getParagraphs().get(0).getText();
                if ("法定代表人".equals(next)) {
                    break;
                }


                String pname = table1.getRows().get(i).getCells().get(2).getParagraphs().get(0).getText();
                String pcard = table1.getRows().get(i).getCells().get(3).getParagraphs().get(0).getText();
                String pamount = table1.getRows().get(i).getCells().get(4).getParagraphs().get(0).getText();
                String prate = table1.getRows().get(i).getCells().get(5).getParagraphs().get(0).getText();


                if (!Objects.isNull(pname) && !"".equals(pname)) {
                    Member member = new Member();
                    member.setName(pname);
                    member.setIdcard(pcard);
                    member.setPutAmount(pamount);
                    member.setPutRate(prate);
                    member.setEnterpriseId(enterpriseId);
                    member.setIsStock(1);
                    memberDao.save(member);
                }
                i++;
            }

            Person master = new Person();
            if ("法定代表人".equals(table1.getRows().get(i).getCells().get(1).getParagraphs().get(0).getText())) {
                master.setName(table1.getRows().get(i).getCells().get(2).getParagraphs().get(0).getText());
                master.setIdcard(table1.getRows().get(i).getCells().get(4).getParagraphs().get(0).getText());
                master.setMobile(table1.getRows().get(i).getCells().get(6).getParagraphs().get(0).getText());
                master.setIsMaster(1);
                master.setEnterpriseId(enterpriseId);
                personDao.save(master);
            }
            i += 2;


            Member jianshi = new Member();
            if ("监  事".equals(table1.getRows().get(i).getCells().get(1).getParagraphs().get(0).getText())) {
                jianshi.setName(table1.getRows().get(i).getCells().get(2).getParagraphs().get(0).getText());
                jianshi.setIdcard(table1.getRows().get(i).getCells().get(4).getParagraphs().get(0).getText());
                jianshi.setMobile(table1.getRows().get(i).getCells().get(6).getParagraphs().get(0).getText());
                jianshi.setIsSupervisor(1);
                jianshi.setEnterpriseId(enterpriseId);
                memberDao.save(jianshi);
            }
            i += 2;
            Person finance = new Person();
            if ("财务负责人".equals(table1.getRows().get(i).getCells().get(1).getParagraphs().get(0).getText())) {
                finance.setName(table1.getRows().get(i).getCells().get(2).getParagraphs().get(0).getText());
                finance.setIdcard(table1.getRows().get(i).getCells().get(4).getParagraphs().get(0).getText());
                finance.setMobile(table1.getRows().get(i).getCells().get(6).getParagraphs().get(0).getText());
                finance.setIsFinance(1);
                finance.setEnterpriseId(enterpriseId);
                personDao.save(finance);
            }

            i += 2;
            Person contact = new Person();
            if ("企业联系人".equals(table1.getRows().get(i).getCells().get(1).getParagraphs().get(0).getText())) {
                contact.setName(table1.getRows().get(i).getCells().get(2).getParagraphs().get(0).getText());
                contact.setIdcard(table1.getRows().get(i).getCells().get(4).getParagraphs().get(0).getText());
                contact.setMobile(table1.getRows().get(i).getCells().get(6).getParagraphs().get(0).getText());
                contact.setIsContact(1);
                contact.setEnterpriseId(enterpriseId);
                personDao.save(contact);
            }

            i += 2;
            if ("实际经营地址".equals(table1.getRows().get(i).getCells().get(1).getParagraphs().get(0).getText())) {
                enterprise.setActContactAddress(table1.getRows().get(i).getCells().get(2).getParagraphs().get(0).getText());
            }

            i += 2;
            enterprise.setRegisterAddress(table1.getRows().get(i).getCells().get(1).getParagraphs().get(0).getText());


            i += 2;
            String business = "";
            ParagraphCollection paragraphCollection = table1.getRows().get(i).getCells().get(1).getParagraphs();
            if (paragraphCollection.getCount() > 0) {
                for (int j = 0; j < paragraphCollection.getCount(); j++) {
                    business += paragraphCollection.get(j).getText();
                }
            }

            enterprise.setBusiness(business);


            log.info("识别结果：{}", enterprise);
            return GsonUtil.build(enterprise);
        } catch (IOException e) {
            e.printStackTrace();
            return GsonUtil.build("error");
        }
    }
}
