package com.bemore.api.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bemore.api.annotation.ExcelColumn;
import com.bemore.api.dao.mapper.EnterpriseMapper;
import com.bemore.api.dao.mapper.EnterpriseSettledMapper;
import com.bemore.api.dao.mapper.EnterpriseTaxMapper;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.EnterpriseSettled;
import com.bemore.api.entity.EnterpriseTaxPlus;
import com.bemore.api.entity.request.SettledQueryParam;
import com.bemore.api.entity.response.EnterpriseSettledTaxView;
import com.bemore.api.entity.response.EnterpriseSettledView;
import com.bemore.api.entity.response.YearTax;
import com.bemore.api.service.EnterpriseSettledService;
import com.bemore.api.util.DateUtil;
import com.bemore.api.util.Util;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.bemore.api.util.GsonUtil.build;

@Service
public class EnterpriseSettledServiceImpl implements EnterpriseSettledService {

    private final EnterpriseSettledMapper settledMapper;
    private final EnterpriseMapper enterpriseMapper;
    private final EnterpriseTaxMapper enterpriseTaxMapper;

    public EnterpriseSettledServiceImpl(EnterpriseSettledMapper settledMapper, EnterpriseMapper enterpriseMapper, EnterpriseTaxMapper enterpriseTaxMapper) {
        this.settledMapper = settledMapper;
        this.enterpriseMapper = enterpriseMapper;
        this.enterpriseTaxMapper = enterpriseTaxMapper;
    }

    @Override
    public void deleteOneById(String id) {
        settledMapper.deleteById(id);
    }

    @Override
    public EnterpriseSettledView findOneById(String id) {
        EnterpriseSettled enterpriseSettled = settledMapper.selectById(id);
        EnterpriseSettledView view = new EnterpriseSettledView();
        BeanUtils.copyProperties(enterpriseSettled,view);
        return view;
    }

    @Override
    public IPage<EnterpriseSettled> findEnterpriseSettledPageOnPage(SettledQueryParam param) {
        String queryString = param.getQueryString();
        IPage<EnterpriseSettled> page = new Page<>(param.getPage(),param.getSize());
        QueryWrapper<EnterpriseSettled> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(StringUtils.hasLength(queryString),EnterpriseSettled::getEnterpriseName,queryString)
                .orderByDesc(EnterpriseSettled::getCreateTime);
        IPage<EnterpriseSettled> page1 = settledMapper.selectPage(page, queryWrapper);
        return page1;
    }

    @Override
    public void updateInfo(EnterpriseSettledView settled) {
        String id = settled.getId();
        EnterpriseSettled result = settledMapper.selectById(id);
        if (Objects.isNull(result)) throw new RuntimeException("update error: not found");
        BeanUtils.copyProperties(settled,result);
        result.setId(id);
        settledMapper.updateById(result);
    }

    @Override
    public void saveInfo(EnterpriseSettled settled) {
        settledMapper.insert(settled);
    }

    /**
     * @Description 查询入驻企业清单
     * @Title findEnterpriseSettledTaxList
     * @Param []
     * @Return java.util.List<com.bemore.api.entity.response.EnterpriseSettledTaxView>
     * @Author Louis
     * @Date 2022/04/25 20:03
     */
    @Override
    public List<EnterpriseSettledTaxView> findEnterpriseSettledTaxList() {
        // 查询出所有已入驻的企业，所处流程: 5 正常
        List<Enterprise> enterpriseList = enterpriseMapper.selectList(new QueryWrapper<Enterprise>().lambda().eq(Enterprise::getProcess, 5));
        // 当前年份
        int currentYear = DateUtil.currentYear();
        // 历史年份集合,从2019开始到去年
        List<Integer> historyYears = new ArrayList<>();
        for (int year = 2019; year < currentYear; year++) {
            historyYears.add(year);
        }
        // 当前月份
        int currentMonth = DateUtil.currentMonth();
        List<Integer> currentYearMonths = new ArrayList<>();
        for (int month = 1; month < currentMonth; month++) {
            currentYearMonths.add(month);
        }
        // 结果集
        List<EnterpriseSettledTaxView> enterpriseSettledTaxViewList = new ArrayList<>();
        // 循环查询数据
        AtomicInteger serialNumber = new AtomicInteger(1);
        enterpriseList.forEach(enterprise -> {
            EnterpriseSettledTaxView enterpriseSettledTaxView = new EnterpriseSettledTaxView();
            // 复制相同字段的值
            BeanUtils.copyProperties(enterprise, enterpriseSettledTaxView);
            // 序号
            enterpriseSettledTaxView.setSerialNumber(serialNumber.getAndIncrement());
            // 备注置空，留位置用户自己输入
            enterpriseSettledTaxView.setRemake(null);
            enterpriseSettledTaxView.setEnterpriseName(enterprise.getName());
            // 初始化集合参数
            enterpriseSettledTaxView.setHistoryYearTaxList(new ArrayList<YearTax>());
            enterpriseSettledTaxView.setCurrentYearTaxList(new ArrayList<YearTax>());
            historyYears.forEach(year -> {
                YearTax yearTax = null;
                List<EnterpriseTaxPlus> enterpriseTaxPlusList = enterpriseTaxMapper.selectList(new QueryWrapper<EnterpriseTaxPlus>().lambda().eq(EnterpriseTaxPlus::getEnterpriseName, enterprise.getName()).eq(EnterpriseTaxPlus::getYear, year));
                if (CollectionUtils.isEmpty(enterpriseTaxPlusList)) {
                    yearTax = YearTax.builder()
                            .date(year.toString() + "年税收（万元）")
                            .value("/")
                            .build();
                } else {
                    // 根据月份倒序排序
                    enterpriseTaxPlusList.sort(Comparator.comparing(EnterpriseTaxPlus::getMonth).reversed());
                    // 取出第一条的总数，就是全年税收，转换为万元
                    yearTax = YearTax.builder()
                            .date(year.toString() + "年税收（万元）")
                            .value(String.valueOf(Util.roundDouble(enterpriseTaxPlusList.get(0).getTotalTaxTotal() / 10000, 6)))
                            .build();
                }
                enterpriseSettledTaxView.getHistoryYearTaxList().add(yearTax);
            });
            currentYearMonths.forEach(month -> {
                YearTax yearMonthTax = null;
                // 当前年的月的数据
                List<EnterpriseTaxPlus> enterpriseTaxPlusList = enterpriseTaxMapper.selectList(new QueryWrapper<EnterpriseTaxPlus>().lambda().eq(EnterpriseTaxPlus::getEnterpriseName, enterprise.getName()).eq(EnterpriseTaxPlus::getYear, currentYear).eq(EnterpriseTaxPlus::getMonth, month));
                if (CollectionUtils.isEmpty(enterpriseTaxPlusList)) {
                    yearMonthTax = YearTax.builder()
                            .date(currentYear + "年" + month + "月税收（万元）")
                            .value("/")
                            .build();
                } else {
                    // 月份只有1条数据，取出第一条的总数，转换为万元
                    yearMonthTax = YearTax.builder()
                            .date(currentYear + "年" + month + "月税收（万元）")
                            .value(String.valueOf(Util.roundDouble(enterpriseTaxPlusList.get(0).getTotalTax() / 10000, 6)))
                            .build();                }
                enterpriseSettledTaxView.getCurrentYearTaxList().add(yearMonthTax);
            });
            enterpriseSettledTaxViewList.add(enterpriseSettledTaxView);
        });
        return enterpriseSettledTaxViewList;
    }

    /**
     * @Description 导出入驻企业清单
     * @Title exportEnterpriseSettledTaxList
     * @Param [response]
     * @Return void
     * @Author Louis
     * @Date 2022/04/25 20:04
     */
    @Override
    public void exportEnterpriseSettledTaxList(HttpServletResponse response) {
        /********************************** 查询数据 **************************************/
        List<EnterpriseSettledTaxView> enterpriseSettledTaxList = this.findEnterpriseSettledTaxList();
        if (CollectionUtils.isEmpty(enterpriseSettledTaxList)) {
            throw new RuntimeException("暂无数据");
        }
        /********************************** 处理字段 **************************************/
        // 字段
        List<Field> enterpriseFields = Arrays.asList(EnterpriseSettledTaxView.class.getDeclaredFields());
        // 所有标题
        List<String> titleList = new ArrayList<>();
        enterpriseFields.forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (Objects.nonNull(excelColumn)) {
                titleList.add(excelColumn.value());
            }
        });
        /********************************** 处理数据 **************************************/
        // 把税收字段动态拼接进去
        List<String> historyYearTaxColumn = enterpriseSettledTaxList.get(0).getHistoryYearTaxList().stream().map(YearTax::getDate).collect(Collectors.toList());
        List<String> currentYearTaxColumn = enterpriseSettledTaxList.get(0).getCurrentYearTaxList().stream().map(YearTax::getDate).collect(Collectors.toList());
        titleList.addAll(historyYearTaxColumn);
        titleList.addAll(currentYearTaxColumn);
        // 所有字段数据集合
        List<List<Object>> totalDataList = new ArrayList<>();
        enterpriseSettledTaxList.forEach(enterprise -> {
            // 每一行的字段数据集合
            List<Object> dataList = new ArrayList<>();
            dataList.add(enterprise.getSerialNumber());
            dataList.add(enterprise.getEnterpriseName());
            dataList.add(enterprise.getFundManagementScale());
            dataList.add(enterprise.getInstitutionalType());
            dataList.add(enterprise.getInvestmentType());
            dataList.add(enterprise.getMoveType());
            dataList.add(enterprise.getBeIndependentInvestmentPromotion());
            dataList.add(enterprise.getSettledDate());
            dataList.add(enterprise.getRemake());
            dataList.add(enterprise.getSource());
            dataList.add(enterprise.getDesignatedContact());
            dataList.add(enterprise.getDesignatedContactPhone());
            List<String> historyYearTaxList = enterprise.getHistoryYearTaxList().stream().map(YearTax::getValue).collect(Collectors.toList());
            List<String> currentYearTaxList = enterprise.getCurrentYearTaxList().stream().map(YearTax::getValue).collect(Collectors.toList());
            dataList.addAll(historyYearTaxList);
            dataList.addAll(currentYearTaxList);
            // 添加到总数据集合
            totalDataList.add(dataList);
        });
        /********************************** 准备文件 **************************************/
        // 文件名
        String fileName = "长三角金融产业园入驻企业清单";
        // 创建 Excel
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 总字段数量
        int totalColumn = titleList.size();
        if (totalColumn > 1) {
            // 合并单元格，设置文件标题。注意：这里的 -1 是因为下标是从0开始的
            excelWriter.merge(totalColumn - 1, fileName);
            excelWriter.setRowHeight(0, 30);
        }
        // 设置所有列宽
        for (int i = 0; i < totalColumn; i++) {
            excelWriter.setColumnWidth(i, 30);
        }
        // 写入标题行
        excelWriter.writeRow(titleList, false);
        // 写入数据行
        excelWriter.write(totalDataList, false);
        // 获取表格，给每一个单元格设置样式
        Sheet sheet = excelWriter.getSheet();
        // 总行数
        int rowPhysicalNumber = sheet.getPhysicalNumberOfRows();
        // 从第3行开始
        for (int i = 2; i < rowPhysicalNumber; i++) {
            Row row = sheet.getRow(i);
            if (Objects.isNull(row)) {
                continue;
            }
            // 当前行的列数
            int cellPhysicalNumber = row.getLastCellNum();
            // 从第1列开始
            for (int j = 0; j < cellPhysicalNumber; j++) {
                row.getCell(j).getCellStyle().setAlignment(HorizontalAlignment.GENERAL);
            }
        }
        /********************************** 文件输出 **************************************/
        ServletOutputStream out = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileName,"utf-8") + ".xlsx");
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

}
