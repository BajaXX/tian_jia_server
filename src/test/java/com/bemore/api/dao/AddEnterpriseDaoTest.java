package com.bemore.api.dao;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.dao.mapper.AddEnterpriseDao;
import com.bemore.api.dao.mapper.EnterpriseAddrMapper;
import com.bemore.api.dto.AddEnterpriseDetailExportDto;
import com.bemore.api.entity.EnterpriseAddr;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("local")
@RunWith(SpringRunner.class)
public class AddEnterpriseDaoTest {

    @Autowired
    AddEnterpriseDao addEnterpriseDao;

    @Autowired
    EnterpriseAddrMapper enterpriseAddrMapper;

    @Test
    public void getExportDetail() {
        List<AddEnterpriseDetailExportDto> detail = addEnterpriseDao.getExportDetail(2021, 1);
        detail.forEach(System.out::println);
    }

    @Test
    public void importAddrs(){
        ExcelReader reader = ExcelUtil.getReader("C:\\Users\\raytine\\Desktop\\【总表】长三角金融产业园企业跟踪分工-总(1)-20220310170001(1).xls");
        reader.addHeaderAlias("公司名称","enterpriseName");
        reader.addHeaderAlias("注册地址","addr");
        List<EnterpriseAddr> data = reader.readAll(EnterpriseAddr.class);

        for (EnterpriseAddr datum : data) {
            QueryWrapper<EnterpriseAddr> wrapper = new QueryWrapper<>();
            wrapper.eq("addr",datum.getAddr().trim());
            if (enterpriseAddrMapper.selectCount(wrapper)>0){
                continue;
            }
            datum.setCreateTime(new Timestamp(System.currentTimeMillis()));
            enterpriseAddrMapper.insert(datum);
        }
    }
}