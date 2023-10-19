package com.bemore.api.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.sax.Excel03SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.dao.mapper.EnterpriseMapper;
import com.bemore.api.dao.mapper.MemberMapper;
import com.bemore.api.dao.mapper.PersonMapper;
import com.bemore.api.dto.*;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.Member;
import com.bemore.api.entity.Person;
import com.bemore.api.entity.request.EnterpriseParam;
import com.bemore.api.entity.request.ReportBaseRequest;
import com.bemore.api.entity.response.HomeStatView;
import com.bemore.api.entity.response.NewEnterpriseTaxView;
import com.bemore.api.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    private final EnterpriseDao enterpriseDao;
    private final EnterpriseMapper enterpriseMapper;
    private final PersonMapper personMapper;
    private final MemberMapper memberMapper;


    public BusinessServiceImpl(EnterpriseDao enterpriseDao,EnterpriseMapper enterpriseMapper,PersonMapper personMapper,MemberMapper memberMapper) {
        this.enterpriseDao = enterpriseDao;
        this.enterpriseMapper=enterpriseMapper;
        this.personMapper=personMapper;
        this.memberMapper=memberMapper;
    }

    @Override
    public void reportEnterpriseService(ReportBaseRequest request, HttpServletResponse response) {
        List<Enterprise> all = enterpriseDao.findAll();

        List<Enterprise> rows = CollUtil.newArrayList(all);
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.addHeaderAlias("process","所处流程");
        excelWriter.addHeaderAlias("garden","园区名字");
        excelWriter.addHeaderAlias("registerNum","三合一码");
        excelWriter.addHeaderAlias("enterpriseNo","企业编号");
        excelWriter.addHeaderAlias("name","企业名称");
        excelWriter.addHeaderAlias("startDate","成立日期");
        excelWriter.addHeaderAlias("source","招商来源");
        excelWriter.addHeaderAlias("registerAddress","注册地址");
        excelWriter.addHeaderAlias("paymentDate","启缴日期");
        excelWriter.setOnlyAlias(true);
        excelWriter.write(rows,true);
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode("企业表","utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition","attachment;filename="+ fileName + ".xlsx");
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
    public void supplementary(MultipartFile file) {
        try {
            ExcelReader excelReader = ExcelUtil.getReader(file.getInputStream());
            excelReader.addHeaderAlias("企业","name");
            excelReader.addHeaderAlias("注册地址","registerAddress");
//            excelReader.addHeaderAlias("机构类型","institutionalType");
//            excelReader.addHeaderAlias("投资类型","investmentType");
//            excelReader.addHeaderAlias("入驻日期","settledDate");
            excelReader.addHeaderAlias("招商来源","source");
            excelReader.addHeaderAlias("跟踪员","follower");
            List<EnterpriseParam> paramList = excelReader.readAll(EnterpriseParam.class);
            paramList.forEach(param -> {
                log.info("param...{}",param);
//                String settledDateStr = param.getSettledDate();
//                String[] date = settledDateStr.split(" ");
//                String[] split = date[0].split("-");
//                int month = Integer.parseInt(split[1]);
//                int day = Integer.parseInt(split[2]);
//                String settledDate = split[0] + "-" + (month > 9 ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day);
//                log.info("settledDate...{}",settledDate);
                Enterprise byName = enterpriseDao.findByName(param.getName());
                if (!Objects.isNull(byName)) {
//                    byName.setSettledDate(LocalDate.parse(settledDate));
                    byName.setRegisterAddress(param.getRegisterAddress());
//                    byName.setInstitutionalType(param.getInstitutionalType());
//                    byName.setInvestmentType(param.getInvestmentType());
                    byName.setSource(param.getSource());
                    byName.setFollower(param.getFollower());
                    byName.setActContactAddress(param.getRegisterAddress());
                    enterpriseDao.save(byName);
                } else {
                    Enterprise enterprise = new Enterprise();
                    enterprise.setName(param.getName());
                    enterprise.setFollower(param.getFollower());
                    enterprise.setSource(param.getSource());
                    enterprise.setRegisterAddress(param.getRegisterAddress());
                    enterprise.setActContactAddress(param.getRegisterAddress());
                    enterpriseDao.save(enterprise);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HomeStatView getHomeStat() {
//        long count = enterpriseDao.count();
        long count = enterpriseDao.countAllByProcess(5);
        int newE = enterpriseDao.countAllByProcess(1);
        int into = enterpriseDao.countAllByProcess(2);
        int change = enterpriseDao.countAllByProcess(3);
        int writeOff = enterpriseDao.countAllByProcess(4);
        HomeStatView statView = new HomeStatView();
        statView.setCount(count);
        statView.setNewE(newE);
        statView.setInto(into);
        statView.setChange(change);
        statView.setWriteOff(writeOff);
        return statView;
    }

    @Override
    public void importEnterpriseBatch(MultipartFile file) {
        try {
            ExcelReader excelReader = ExcelUtil.getReader(file.getInputStream());
            excelReader.addHeaderAlias("所属园区","garden");
            excelReader.addHeaderAlias("企业编号","enterpriseNo");
            excelReader.addHeaderAlias("企业名称","name");
            excelReader.addHeaderAlias("营业执照编号","paperNo");
            excelReader.addHeaderAlias("成立日期","startDate");
            excelReader.addHeaderAlias("企业类型","type");
            excelReader.addHeaderAlias("从事行业","belongIndustry");
//            excelReader.addHeaderAlias("行业小类","industry");
            excelReader.addHeaderAlias("基金备案号","introducer");
            excelReader.addHeaderAlias("招商来源","source");
            excelReader.addHeaderAlias("注册资本币种","currency");
//            excelReader.addHeaderAlias("招商来源","follower");
            excelReader.addHeaderAlias("注册资本","capital");
            excelReader.addHeaderAlias("实收资本","realCapital");
            excelReader.addHeaderAlias("注册地址","registerAddress");
            excelReader.addHeaderAlias("注册地址邮编","zipcode");
            excelReader.addHeaderAlias("经营期限结束","endDate");
            excelReader.addHeaderAlias("一般经营项目","business");
            excelReader.addHeaderAlias("实际经营电话","contactPhone");
            excelReader.addHeaderAlias("实际经营地址","actContactAddress");
//            excelReader.addHeaderAlias("实际经营本区","actContactAddress");
            List<Enterprise> enterpriseList = excelReader.readAll(Enterprise.class);
            enterpriseList.forEach( enterprise -> log.info("enterprise...{}",enterprise));
            enterpriseDao.saveAll(enterpriseList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EnterprisePageDto getEnterprises(EnterpriseQueryDto queryDto) {
        if (queryDto.getBasic()==null){
            queryDto.setBasic(new BasicInfoDto());
        }
        if (queryDto.getBusiness()==null){
            queryDto.setBusiness(new BusinessInfoDto());
        }
        if (queryDto.getTax()==null){
            queryDto.setTax(new TaxInfoDto());
        }
        queryDto.setOffset((queryDto.getPage()-1)*queryDto.getLimit());

        Integer total=enterpriseMapper.getEnterpriseCount(queryDto);
        List<Enterprise> list= enterpriseMapper.getEnterprise(queryDto);

        EnterprisePageDto enterprisePageDto=new EnterprisePageDto();
        enterprisePageDto.setEnterprises(list);
        enterprisePageDto.setTotalNumber(total);
        return enterprisePageDto;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePerson(PersonDto personDto) {
        personDto.setId(null);
        Person person = new Person();
        BeanUtils.copyProperties(personDto,person);

        QueryWrapper<Person> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("idcard",person.getIdcard());
        Person personFromDb = personMapper.selectOne(queryWrapper);
        if (personFromDb!=null){
            person.setId(personFromDb.getId());
            personMapper.updateById(person);
        }else {
            personMapper.insert(person);
        }


        if (Objects.equals(1,personDto.getIsStock())
        || Objects.equals(1,personDto.getIsSupervisor())
        || Objects.equals(1,personDto.getIsDirector())){
            Member member = new Member();
            BeanUtils.copyProperties(personDto,member);
            QueryWrapper<Member> wrapper = new QueryWrapper<>();
            wrapper.eq("idcard",member.getIdcard());
            Member memberFromDb = memberMapper.selectOne(wrapper);
            if (memberFromDb!=null){
                member.setId(memberFromDb.getId());
                memberMapper.updateById(member);
            }else {
                memberMapper.insert(member);
            }

        }
    }
}
