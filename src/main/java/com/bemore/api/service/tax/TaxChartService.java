package com.bemore.api.service.tax;

import com.bemore.api.entity.response.chart.TaxChart;
import com.bemore.api.entity.response.chart.TaxDataPie;
import com.bemore.api.entity.response.chart.TaxStats;
import com.bemore.api.entity.response.chart.TaxTrend;

import java.util.List;

/**
 * @ClassName TaxChartService
 * @Description 税收图表服务层
 * @Author Louis
 * @Date 2022/04/28 13:45
 */
public interface TaxChartService {

    /**
     * @Description 查询税收数据比例饼图
     * @Title getTaxDataPie
     * @Param [enterpriseName, months]
     * @Return java.util.List<com.bemore.api.entity.response.chart.TaxDataPie>
     * @Author Louis
     * @Date 2022/04/28 13:53
     */
    List<TaxDataPie> getTaxDataPie(String enterpriseName, int months);

    /**
     * @Description 查询税收统计数据
     * @Title getTaxStats
     * @Param [enterpriseName]
     * @Return com.bemore.api.entity.response.chart.TaxStats
     * @Author Louis
     * @Date 2022/04/28 16:08
     */
    TaxStats getTaxStats(String enterpriseName);

    /**
     * @Description 查询税收数据趋势
     * @Title getTaxTrend
     * @Param [enterpriseName, months]
     * @Return com.bemore.api.entity.response.chart.TaxTrend
     * @Author Louis
     * @Date 2022/04/28 17:19
     */
    TaxTrend getTaxTrend(String enterpriseName, int months);

    /**
     * @Description 查询税收数据折线图
     * @Title getTaxLine
     * @Param [enterpriseName, months]
     * @Return com.bemore.api.entity.response.chart.TaxChart
     * @Author Louis
     * @Date 2022/04/29 10:23
     */
    TaxChart getTaxLine(String enterpriseName, int months);

}
