package com.bemore.api.service.tax.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bemore.api.dao.mapper.EnterpriseTaxMapper;
import com.bemore.api.entity.EnterpriseTaxPlus;
import com.bemore.api.entity.response.chart.*;
import com.bemore.api.service.tax.TaxChartService;
import com.bemore.api.util.Util;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName TaxChartServiceImpl
 * @Description 税收图表服务层
 * @Author Louis
 * @Date 2022/04/28 13:45
 */
@Service
public class TaxChartServiceImpl implements TaxChartService {

    @Autowired private EnterpriseTaxMapper enterpriseTaxMapper;

    /**
     * @Description 查询税收数据比例饼图
     * @Title getTaxDataPie
     * @Param [enterpriseName, months]
     * @Return java.util.List<com.bemore.api.entity.response.chart.TaxDataPie>
     * @Author Louis
     * @Date 2022/04/28 13:53
     */
    @Override
    public List<TaxDataPie> getTaxDataPie(@NonNull String enterpriseName, int months) {
        // 税收集合
        List<EnterpriseTaxPlus> enterpriseTaxPlusList = enterpriseTaxMapper.selectList(new LambdaQueryWrapper<EnterpriseTaxPlus>().eq(EnterpriseTaxPlus::getEnterpriseName, enterpriseName.trim()).orderByDesc(EnterpriseTaxPlus::getDate).last(months > 0, "LIMIT " + months));
        // 将同一个公司的数据分组并将数字字段求和，由于只查询了一个公司，所以直接分组后就只有一条数据，直接get(0)
        EnterpriseTaxPlus enterpriseTaxPlus = enterpriseTaxPlusList.stream().collect(Collectors.toMap(EnterpriseTaxPlus::getEnterpriseName, a -> a, (o1, o2) -> {
            o2.setSales(o1.getSales() + o2.getSales());
            o2.setBusiness(o1.getBusiness() + o2.getBusiness());
            o2.setAddedTax(o1.getAddedTax() + o2.getAddedTax());
            o2.setToAddedTax(o1.getToAddedTax() + o2.getToAddedTax());
            o2.setBusinessTax(o1.getBusinessTax() + o2.getBusinessTax());
            o2.setExcise(o1.getExcise() + o2.getExcise());
            o2.setIncomeTax(o1.getIncomeTax() + o2.getIncomeTax());
            o2.setPersonTax(o1.getPersonTax() + o2.getPersonTax());
            o2.setHouseTax(o1.getHouseTax() + o2.getHouseTax());
            o2.setCarTax(o1.getCarTax() + o2.getCarTax());
            o2.setStampTax(o1.getStampTax() + o2.getStampTax());
            o2.setLandTax(o1.getLandTax() + o2.getLandTax());
            o2.setAddedLandTax(o1.getAddedLandTax() + o2.getAddedLandTax());
            o2.setCityTax(o1.getCityTax() + o2.getCityTax());
            o2.setEnvironmentTax(o1.getEnvironmentTax() + o2.getEnvironmentTax());
            o2.setFarmlandTax(o1.getFarmlandTax() + o2.getFarmlandTax());
            o2.setCarPurchaseTax(o1.getCarPurchaseTax() + o2.getCarPurchaseTax());
            o2.setDeedTax(o1.getDeedTax() + o2.getDeedTax());
            o2.setTotalTax(o1.getTotalTax() + o2.getTotalTax());
            return o2;
        })).values().stream().findFirst().orElse(EnterpriseTaxPlus.builder().build());
        // 不能使用TotalTax作为总数，这只代表税，不包括销售
        Double total = enterpriseTaxPlus.getSales() + enterpriseTaxPlus.getTotalTax();
        if (total == 0) {
            return null;
        }
        // 结果集
        List<TaxDataPie> taxDataPies = Arrays.asList(
                TaxDataPie.builder().name("销售额").value(Util.roundDouble(enterpriseTaxPlus.getSales() / 10000, 6)).percentage(Util.roundDouble(enterpriseTaxPlus.getSales() / total * 100, 2) + "%").build(),
                TaxDataPie.builder().name("增值税").value(Util.roundDouble(enterpriseTaxPlus.getAddedTax() / 10000, 6)).percentage(Util.roundDouble(enterpriseTaxPlus.getAddedTax() / total * 100, 2) + "%").build(),
                TaxDataPie.builder().name("企业所得税").value(Util.roundDouble(enterpriseTaxPlus.getIncomeTax() / 10000, 6)).percentage(Util.roundDouble(enterpriseTaxPlus.getIncomeTax() / total * 100, 2) + "%").build(),
                TaxDataPie.builder().name("个人所得税").value(Util.roundDouble(enterpriseTaxPlus.getPersonTax() / 10000, 6)).percentage(Util.roundDouble(enterpriseTaxPlus.getPersonTax() / total * 100, 2) + "%").build(),
                TaxDataPie.builder().name("城建税").value(Util.roundDouble(enterpriseTaxPlus.getCityTax() / 10000, 6)).percentage(Util.roundDouble(enterpriseTaxPlus.getCityTax() / total * 100, 2) + "%").build(),
                TaxDataPie.builder().name("其他").value(0D).percentage("0.0%").build()
        );
        // 其他
        Double otherValue = (total / 10000)
                            - taxDataPies.get(0).getValue()
                            - taxDataPies.get(1).getValue()
                            - taxDataPies.get(2).getValue()
                            - taxDataPies.get(3).getValue()
                            - taxDataPies.get(4).getValue();
        taxDataPies.get(taxDataPies.size() - 1).setValue(Util.roundDouble(otherValue, 6));
        taxDataPies.get(taxDataPies.size() - 1).setPercentage(Util.roundDouble(otherValue / (total / 10000) * 100, 2) + "%");
        return taxDataPies;
    }

    /**
     * @Description 查询税收统计数据
     * @Title getTaxStats
     * @Param [enterpriseName]
     * @Return com.bemore.api.entity.response.chart.TaxStats
     * @Author Louis
     * @Date 2022/04/28 16:07
     */
    @Override
    public TaxStats getTaxStats(@NonNull String enterpriseName) {
        // 税收集合
        List<EnterpriseTaxPlus> enterpriseTaxPlusList = enterpriseTaxMapper.selectList(new LambdaQueryWrapper<EnterpriseTaxPlus>().eq(EnterpriseTaxPlus::getEnterpriseName, enterpriseName.trim()).orderByDesc(EnterpriseTaxPlus::getDate).last("LIMIT 1"));
        // 当月对象
        EnterpriseTaxPlus enterpriseTaxPlus = CollectionUtils.isEmpty(enterpriseTaxPlusList)
                ? EnterpriseTaxPlus.builder().build()
                : enterpriseTaxPlusList.get(0);
        // 企业当月税收排名集合
        List<EnterpriseTaxPlus> enterpriseTaxPlusCurrentMonthRankingList = enterpriseTaxMapper.selectList(new LambdaQueryWrapper<EnterpriseTaxPlus>()
                .select(EnterpriseTaxPlus::getId, EnterpriseTaxPlus::getEnterpriseName, EnterpriseTaxPlus::getTotalTax)
                .eq(EnterpriseTaxPlus::getYear, enterpriseTaxPlus.getYear())
                .eq(EnterpriseTaxPlus::getMonth, enterpriseTaxPlus.getMonth())
                .orderByDesc(EnterpriseTaxPlus::getTotalTax));
        // 企业当年税收排名集合
        List<EnterpriseTaxPlus> enterpriseTaxPlusCurrentYearRankingList = enterpriseTaxMapper.selectList(new LambdaQueryWrapper<EnterpriseTaxPlus>()
                .select(EnterpriseTaxPlus::getId, EnterpriseTaxPlus::getEnterpriseName, EnterpriseTaxPlus::getTotalTaxTotal)
                .eq(EnterpriseTaxPlus::getYear, enterpriseTaxPlus.getYear())
                .eq(EnterpriseTaxPlus::getMonth, enterpriseTaxPlus.getMonth())
                .orderByDesc(EnterpriseTaxPlus::getTotalTaxTotal));
        // 当月排名，当年排名
        int currentMonthRanking = 1, currentYearRanking = 1;
        for (EnterpriseTaxPlus tax : enterpriseTaxPlusCurrentMonthRankingList) {
            if (!Objects.equals(tax.getEnterpriseName(), enterpriseTaxPlus.getEnterpriseName())) {
                currentMonthRanking++;
            } else {
                break;
            }
        }
        for (EnterpriseTaxPlus tax : enterpriseTaxPlusCurrentYearRankingList) {
            if (!Objects.equals(tax.getEnterpriseName(), enterpriseTaxPlus.getEnterpriseName())) {
                currentYearRanking++;
            } else {
                break;
            }
        }
        return TaxStats.builder()
                .enterpriseName(enterpriseTaxPlus.getEnterpriseName())
                .year(enterpriseTaxPlus.getYear())
                .month(enterpriseTaxPlus.getMonth())
                .currentMonthTotalTax(enterpriseTaxPlus.getTotalTax())
                .currentYearTotalTax(enterpriseTaxPlus.getTotalTaxTotal())
                .currentMonthRanking(currentMonthRanking)
                .currentYearRanking(currentYearRanking)
                .build();
    }

    /**
     * @Description 查询税收数据趋势
     * @Title getTaxTrend
     * @Param [enterpriseName, months]
     * @Return com.bemore.api.entity.response.chart.TaxTrend
     * @Author Louis
     * @Date 2022/04/28 17:19
     */
    @Override
    public TaxTrend getTaxTrend(@NonNull String enterpriseName, int months) {
        // 税收集合
        List<EnterpriseTaxPlus> enterpriseTaxPlusList = enterpriseTaxMapper.selectList(new LambdaQueryWrapper<EnterpriseTaxPlus>().eq(EnterpriseTaxPlus::getEnterpriseName, enterpriseName.trim()).orderByDesc(EnterpriseTaxPlus::getDate).last(months > 0, "LIMIT " + months));
        // 将倒序转为正序
        Collections.reverse(enterpriseTaxPlusList);
        // 按时间从早到晚排序
        List<String> timeSortDate = enterpriseTaxPlusList.stream().map(tax -> tax.getYear() + "年" + tax.getMonth() + "月").collect(Collectors.toList());
        List<Double> timeSortValue = enterpriseTaxPlusList.stream().map(EnterpriseTaxPlus::getTotalTax).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        // 按数据从大到小排序
        List<EnterpriseTaxPlus> valueSortedEnterpriseTaxPlusList = enterpriseTaxPlusList.stream().sorted(Comparator.comparing(EnterpriseTaxPlus::getTotalTax).reversed()).collect(Collectors.toList());
        List<String> valueSortDate = valueSortedEnterpriseTaxPlusList.stream().map(tax -> tax.getYear() + "年" + tax.getMonth() + "月").collect(Collectors.toList());
        List<Double> valueSortValue = valueSortedEnterpriseTaxPlusList.stream().map(EnterpriseTaxPlus::getTotalTax).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        return TaxTrend.builder()
                .timeSortDate(timeSortDate)
                .timeSortValue(timeSortValue)
                .valueSortDate(valueSortDate)
                .valueSortValue(valueSortValue)
                .build();
    }

    /**
     * @Description 查询税收数据折线图
     * @Title getTaxLine
     * @Param [enterpriseName, months]
     * @Return com.bemore.api.entity.response.chart.TaxChart
     * @Author Louis
     * @Date 2022/04/29 10:23
     */
    @Override
    public TaxChart getTaxLine(@NonNull String enterpriseName, int months) {
        // 税收集合
        List<EnterpriseTaxPlus> enterpriseTaxPlusList = enterpriseTaxMapper.selectList(new LambdaQueryWrapper<EnterpriseTaxPlus>().eq(EnterpriseTaxPlus::getEnterpriseName, enterpriseName.trim()).orderByDesc(EnterpriseTaxPlus::getDate).last(months > 0, "LIMIT " + months));
        // 将倒序转为正序，按时间从早到晚排序
        Collections.reverse(enterpriseTaxPlusList);
        // 日期
        List<String> date = enterpriseTaxPlusList.stream().map(tax -> tax.getYear() + "年" + tax.getMonth() + "月").collect(Collectors.toList());
        // 销售额
        List<Double> salesData = enterpriseTaxPlusList.stream().map(EnterpriseTaxPlus::getSales).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        ChartData sales = ChartData.builder().title("销售额").category(date).data(salesData).build();
        // 增值税
        List<Double> addedTaxData = enterpriseTaxPlusList.stream().map(EnterpriseTaxPlus::getAddedTax).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        ChartData addedTax = ChartData.builder().title("增值税").category(date).data(addedTaxData).build();
        // 企业所得税
        List<Double> incomeTaxData = enterpriseTaxPlusList.stream().map(EnterpriseTaxPlus::getIncomeTax).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        ChartData incomeTax = ChartData.builder().title("企业所得税").category(date).data(incomeTaxData).build();
        // 个人所得税
        List<Double> personTaxData = enterpriseTaxPlusList.stream().map(EnterpriseTaxPlus::getPersonTax).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        ChartData personTax = ChartData.builder().title("个人所得税").category(date).data(personTaxData).build();
        // 城建税
        List<Double> cityTaxData = enterpriseTaxPlusList.stream().map(EnterpriseTaxPlus::getCityTax).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        ChartData cityTax = ChartData.builder().title("城建税").category(date).data(cityTaxData).build();
        // 其他
        List<Double> otherTaxData = enterpriseTaxPlusList.stream().map(e -> e.getTotalTax() - e.getAddedTax() - e.getIncomeTax() - e.getPersonTax() - e.getCityTax()).map(v -> Util.roundDouble(v / 10000, 6)).collect(Collectors.toList());
        ChartData otherTax = ChartData.builder().title("其他").category(date).data(otherTaxData).build();
        return TaxChart.builder()
                .sales(sales)
                .addedTax(addedTax)
                .incomeTax(incomeTax)
                .personTax(personTax)
                .cityTax(cityTax)
                .otherTax(otherTax)
                .build();
    }

}
