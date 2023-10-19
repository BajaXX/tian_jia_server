package com.bemore.api.service.tax.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.bemore.api.dao.mapper.AddEnterpriseDao;
import com.bemore.api.dto.AddEnterpriseDetailExportDto;
import com.bemore.api.service.tax.AddEnterpriseService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddEnterpriseServiceImpl implements AddEnterpriseService {

    private final AddEnterpriseDao addEnterpriseDao;

    public AddEnterpriseServiceImpl(AddEnterpriseDao addEnterpriseDao) {
        this.addEnterpriseDao = addEnterpriseDao;
    }

    @Override
    public void exportAddEnterpriseDetail(Integer year, Integer month, HttpServletResponse response) throws IOException {
        String fileName = URLEncoder.encode("新增纳税企业明细表","utf-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");

        List<AddEnterpriseDetailExportDto> detail = addEnterpriseDao.getExportDetail(year,month);
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.addHeaderAlias("no","序号");
        excelWriter.addHeaderAlias("enterpriseNo","企业编号");
        excelWriter.addHeaderAlias("registerNum","组织机构代码");
        excelWriter.addHeaderAlias("name","单位名称");
        excelWriter.addHeaderAlias("source","招商来源");
        excelWriter.addHeaderAlias("registerAddress","单位地址");
        excelWriter.addHeaderAlias("process","状态");
        excelWriter.addHeaderAlias("startDate","成立时间");
        excelWriter.addHeaderAlias("buildDate","建档时间");
        excelWriter.addHeaderAlias("seizeDate","启缴时间");
        excelWriter.addHeaderAlias("curMonthTax","当月纳税额");
        excelWriter.addHeaderAlias("totalTax","累计纳税额");
        excelWriter.merge(12,"新增纳税企业明细表");
        excelWriter.writeCellValue(0,1,"经济小区名称：长三角");
        excelWriter.writeCellValue(1,1,String.format("%d年%d月份",year,month));
        excelWriter.write(detail,true);
        excelWriter.writeCellValue(0,4,"合计");


        List<BigDecimal> totalTaxList = detail.stream().filter(item -> item.getTotalTax() != null).map(AddEnterpriseDetailExportDto::getTotalTax).collect(Collectors.toList());
        double totalTax = totalTaxList.stream().mapToDouble(item -> item.doubleValue()).sum();
        List<BigDecimal> curMonthTaxList = detail.stream().filter(item -> item.getCurMonthTax() != null).map(AddEnterpriseDetailExportDto::getCurMonthTax).collect(Collectors.toList());
        double curMonthTax = curMonthTaxList.stream().mapToDouble(item -> item.doubleValue()).sum();
        excelWriter.writeCellValue(10,4,totalTax);
        excelWriter.writeCellValue(11,4,curMonthTax);
        excelWriter.flush(response.getOutputStream());
        excelWriter.close();
    }

    @Override
    public List<AddEnterpriseDetailExportDto> getAddDetail(Integer year, Integer month) {
        return addEnterpriseDao.getExportDetail(year,month);
    }
}
