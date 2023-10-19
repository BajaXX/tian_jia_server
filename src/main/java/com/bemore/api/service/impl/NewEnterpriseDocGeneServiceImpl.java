package com.bemore.api.service.impl;

import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.dao.mapper.TEnterpriseMemberMapper;
import com.bemore.api.dto.EnterpriseMasterInfoDto;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.service.NewEnterpriseDocGeneService;
import com.bemore.api.util.DocUtil;
import com.bemore.api.util.StrUtils;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Service
@Slf4j
public class NewEnterpriseDocGeneServiceImpl implements NewEnterpriseDocGeneService {

    @Autowired
    EnterpriseDao enterpriseDao;

    @Autowired
    TEnterpriseMemberMapper memberMapper;


    @Override
    public void generate1401Doc(String enterpriseId, String fileName, HttpServletResponse response) {
        Enterprise enterprise = enterpriseDao.getOne(enterpriseId);
        String enterpriseName;
        String registerAddress;
        // 经营范围
        String business;
        if (enterprise == null) {
            enterpriseName="";
            registerAddress="";
            business="";
        }else {
            enterpriseName = enterprise.getName();
            registerAddress = enterprise.getRegisterAddress();
            // 经营范围
            business = enterprise.getBusiness();
        }

        EnterpriseMasterInfoDto masterInfo = memberMapper.getMasterInfo(enterpriseId);
        String name;
        String address;
        String putType;
        String putAmount;
        if (masterInfo==null){
            name="";
            address="";
            putType="";
            putAmount="";
        }else {
            name = masterInfo.getName();
            address = masterInfo.getAddress();
            putType = masterInfo.getPutType();
            putAmount = masterInfo.getPutAmount();
        }


        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseName",StrUtils.blackIfNull(enterpriseName));
        params.put("enterpriseAddress",StrUtils.blackIfNull(registerAddress));
        params.put("business",StrUtils.blackIfNull(business));
        params.put("name",StrUtils.blackIfNull(name));
        params.put("address",StrUtils.blackIfNull(address));
        params.put("putAmount",StrUtils.blackIfNull(putAmount));


        Document document = DocUtil.replace("template/"+fileName+".doc", params);
        DocUtil.DocWriteResponse(fileName,response,document,FileFormat.Doc);
    }

    @Override
    public void generate1409Doc(String enterpriseId, String fileName, HttpServletResponse response) {
        Enterprise enterprise = enterpriseDao.getOne(enterpriseId);
        String enterpriseName ;
        String registerAddress ;
        String business;
        if (enterprise == null) {
            enterpriseName="";
            registerAddress="";
            business="";
        }else {
            enterpriseName = enterprise.getName();
            registerAddress = enterprise.getRegisterAddress();
            // 经营范围
            business = enterprise.getBusiness();
        }
        EnterpriseMasterInfoDto masterInfo = memberMapper.getMasterInfo(enterpriseId);
        String name ;
        String address ;
        String putType ;
        String putAmount;

        if (masterInfo==null){
            name="";
            address="";
            putType="";
            putAmount="";
        }else {
            name = masterInfo.getName();
            address = masterInfo.getAddress();
            putType = masterInfo.getPutType();
            putAmount = masterInfo.getPutAmount();
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseName",StrUtils.blackIfNull(enterpriseName));
        params.put("enterpriseAddress",StrUtils.blackIfNull(registerAddress));
        params.put("business",StrUtils.blackIfNull(business));
        params.put("nameAndAddress",StrUtils.blackIfNull(name));
        params.put("putAmount",StrUtils.blackIfNull(putAmount));


        Document document = DocUtil.replace("template/"+fileName+".doc", params);
        DocUtil.DocWriteResponse(fileName,response,document,FileFormat.Doc);
    }

    @Override
    public void generate1501Doc(String enterpriseId, String fileName, HttpServletResponse response) {
        Enterprise enterprise = enterpriseDao.getOne(enterpriseId);
        String enterpriseName ;
        String registerAddress ;
        //注册资本
        String capital  ;
        String contactPhone ;
        String actContactAddress ;
        //法人
        String industry ;
        //法人电话
        String introducer;

        String postCode ;
        String remake ;
        if (enterprise == null) {
            enterpriseName = "";
            registerAddress = "";
            //注册资本
            capital = "";
            contactPhone = "";
            actContactAddress = "";
            //法人
            industry = "";
            //法人电话
            introducer = "";

            postCode = "";
            remake = "";
        }else {
            enterpriseName = enterprise.getName();
            registerAddress = enterprise.getRegisterAddress();
            //注册资本
            capital = enterprise.getCapital();
            contactPhone = enterprise.getContactPhone();
            actContactAddress = enterprise.getActContactAddress();
            //法人
            industry = enterprise.getIndustry();
            //法人电话
            introducer = enterprise.getIntroducer();

            postCode = enterprise.getZipcode();
            remake = enterprise.getRemake();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseName",StrUtils.blackIfNull(enterpriseName));
        params.put("capital",StrUtils.blackIfNull(capital));
        params.put("contactPhone",StrUtils.blackIfNull(contactPhone));
        params.put("actContactAddress",StringUtils.isEmpty(actContactAddress)? StrUtils.blackIfNull(actContactAddress):StrUtils.blackIfNull(registerAddress));
        params.put("master",StrUtils.blackIfNull(industry));
        params.put("masterPhone",StrUtils.blackIfNull(introducer));
        params.put("remarks",StrUtils.blackIfNull(remake));
        params.put("postCode",StrUtils.blackIfNull(postCode));
        Document document = DocUtil.replace("template/"+fileName+".doc", params);
        DocUtil.DocWriteResponse(fileName,response,document,FileFormat.Doc);

    }

    @Override
    public void generate1010Doc(String enterpriseId, String fileName, HttpServletResponse response) {
        HashMap<String, String> params = new HashMap<>();
        params.put("principal",StrUtils.blackIfNull(""));
        params.put("byThePrincipal",StrUtils.blackIfNull(""));
        params.put("principalThings",StrUtils.blackIfNull(""));
        params.put("otherThing",StrUtils.blackIfNull(""));
        params.put("startDate",StrUtils.blackIfNull(""));
        params.put("endDate",StrUtils.blackIfNull(""));
        params.put("phone",StrUtils.blackIfNull(""));
        params.put("mobilePhone",StrUtils.blackIfNull(""));
        Document document = DocUtil.replace("template/"+fileName+".doc", params);
        String idCardPath="";
        String idCardBackPath="";
        if (!StringUtils.isEmpty(idCardPath)){
            document = DocUtil.insertImg(document, idCardPath);
        }
        if (!StringUtils.isEmpty(idCardBackPath)){
            document = DocUtil.insertImg(document, idCardBackPath);
        }
        DocUtil.DocWriteResponse(fileName,response,document,FileFormat.Doc);
    }
}
