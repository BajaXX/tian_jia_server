package com.bemore.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.dao.mapper.EnterpriseMapper;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.service.EnterpriseService;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName EnterpriseServiceImpl
 * @Description 企业信息服务层
 * @Author Louis
 * @Date 2022/04/24 23:29
 */
@Service
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper, Enterprise> implements EnterpriseService {

    @Autowired private EnterpriseMapper enterpriseMapper;

    @Override
    public void updateByExcel(@NonNull MultipartFile excelFile) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(excelFile.getInputStream());
        // 获取第一个工作表，下标为0
        XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
        // 总行数
        int rowPhysicalNumber = sheet.getPhysicalNumberOfRows();
        Assert.isTrue(rowPhysicalNumber > 0, "没有找到数据");
        // 结果集
        List<Enterprise> newEnterpriseList = new ArrayList<>();
        // 从第3行开始，下标减1
        for (int i = 2; i < rowPhysicalNumber; i++) {
            XSSFRow row = sheet.getRow(i);
            if (Objects.isNull(row)) {
                continue;
            }
            Enterprise enterprise = new Enterprise();
            // B：第2列：企业名称
            XSSFCell nameCell = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(nameCell)) {
                nameCell.setCellType(CellType.STRING);
                enterprise.setName(nameCell.getStringCellValue());
            } else {
                // 没有企业名称直接跳过
                continue;
            }
            // C：第3列：基金管理规模或基金规模（亿元）
            XSSFCell fundManagementScaleCell = row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(fundManagementScaleCell)) {
                fundManagementScaleCell.setCellType(CellType.STRING);
                enterprise.setFundManagementScale(fundManagementScaleCell.getStringCellValue());
            }
            // D：第4列：机构类型
            XSSFCell institutionalTypeCell = row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(institutionalTypeCell)) {
                institutionalTypeCell.setCellType(CellType.STRING);
                enterprise.setInstitutionalType(institutionalTypeCell.getStringCellValue());
            }
            // E：第5列：投资类型
            XSSFCell investmentTypeCell = row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(investmentTypeCell)) {
                investmentTypeCell.setCellType(CellType.STRING);
                enterprise.setInvestmentType(investmentTypeCell.getStringCellValue());
            }
            // I：第9列：迁入/新设
            XSSFCell moveTypeCell = row.getCell(8, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(moveTypeCell)) {
                moveTypeCell.setCellType(CellType.STRING);
                enterprise.setMoveType(moveTypeCell.getStringCellValue());
            }
            // K：第11列：是否自主招商
            XSSFCell beIndependentInvestmentPromotionCell = row.getCell(10, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(beIndependentInvestmentPromotionCell)) {
                beIndependentInvestmentPromotionCell.setCellType(CellType.STRING);
                enterprise.setBeIndependentInvestmentPromotion(beIndependentInvestmentPromotionCell.getStringCellValue());
            }
            // L：第12列：入驻日期
            XSSFCell settledDateCell = row.getCell(11, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(settledDateCell)) {
                int beginDateEpochDay = (int) settledDateCell.getNumericCellValue();
                // 计算机的纪元时间是1970-01-01，Excel是1900-01-01，所以中间相差25569天
                String settledDate = beginDateEpochDay < 25569 ? null : LocalDate.ofEpochDay(beginDateEpochDay - 25569).toString();
                enterprise.setSettledDate(settledDate);
            }
            // O：第15列：指定联系人
            XSSFCell designatedContactCell = row.getCell(14, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(designatedContactCell)) {
                designatedContactCell.setCellType(CellType.STRING);
                enterprise.setDesignatedContact(designatedContactCell.getStringCellValue());
            }
            // P：第16列：联系电话
            XSSFCell designatedContactPhoneCell = row.getCell(15, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Objects.nonNull(designatedContactPhoneCell)) {
                designatedContactPhoneCell.setCellType(CellType.STRING);
                enterprise.setDesignatedContactPhone(designatedContactPhoneCell.getStringCellValue());
            }
            newEnterpriseList.add(enterprise);
        }
        // 查询出全部的企业
        List<Enterprise> enterpriseList = enterpriseMapper.selectList(null);
        enterpriseList.forEach(enterprise -> {
            // 根据企业名称匹配出对应的一个企业对象
            Enterprise matchedEnterprise = newEnterpriseList.stream().filter(newEnterprise -> Objects.equals(enterprise.getName(), newEnterprise.getName())).findFirst().orElse(null);
            if (Objects.nonNull(matchedEnterprise)) {
                enterprise.setFundManagementScale(matchedEnterprise.getFundManagementScale());
                enterprise.setInstitutionalType(matchedEnterprise.getInstitutionalType());
                enterprise.setInvestmentType(matchedEnterprise.getInvestmentType());
                enterprise.setMoveType(matchedEnterprise.getMoveType());
                enterprise.setBeIndependentInvestmentPromotion(matchedEnterprise.getBeIndependentInvestmentPromotion());
                enterprise.setSettledDate(matchedEnterprise.getSettledDate());
                enterprise.setDesignatedContact(matchedEnterprise.getDesignatedContact());
                enterprise.setDesignatedContactPhone(matchedEnterprise.getDesignatedContactPhone());
            }
        });
        // 批量更细保存
        this.updateBatchById(enterpriseList);
    }

}
