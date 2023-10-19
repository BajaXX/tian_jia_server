package com.bemore.api.service;

import com.bemore.api.dto.*;
import com.bemore.api.entity.Enterprise;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@ActiveProfiles("local")
@RunWith(SpringRunner.class)
public class BusinessServiceTest {

    @Autowired
    private BusinessService businessService;
    @Test
    public void getEnterprises() {

//        EnterpriseQueryDto queryDto = new EnterpriseQueryDto();
//        BasicInfoDto basicInfoDto = new BasicInfoDto();
//        basicInfoDto.setName("上海琏安管理咨询合伙企业（有限合伙）");
//        BusinessInfoDto businessInfoDto = new BusinessInfoDto();
//        TaxInfoDto taxInfoDto = new TaxInfoDto();
//        queryDto.setBasic(basicInfoDto);
//        queryDto.setBusiness(businessInfoDto);
//        queryDto.setTax(taxInfoDto);
//        EnterprisePageDto enterprises = businessService.getEnterprises(queryDto);
//        system.ou
    }
}
