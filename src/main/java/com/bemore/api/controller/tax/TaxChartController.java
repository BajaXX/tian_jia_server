package com.bemore.api.controller.tax;

import com.bemore.api.entity.response.chart.TaxDataPie;
import com.bemore.api.service.tax.TaxChartService;
import com.bemore.api.util.GsonUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName TaxChartController
 * @Description 税收图表控制层
 * @Author Louis
 * @Date 2022/04/28 13:32
 */
@RestController
@RequestMapping("/tax/chart")
public class TaxChartController {

    @Autowired private TaxChartService taxChartService;

    /**
     * @Description 查询税收统计数据
     * @Title getTaxStats
     * @Param [enterpriseName]
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/04/28 16:34
     */
    @ApiOperation(value = "查询税收统计数据")
    @GetMapping("/tax/stats")
    public String getTaxStats(@RequestParam @ApiParam("企业名称") String enterpriseName) {
        return GsonUtil.build(taxChartService.getTaxStats(enterpriseName));
    }

    /**
     * @Description 查询税收数据比例饼图
     * @Title getTaxDataPie
     * @Param [enterpriseName, months]
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/04/28 13:45
     */
    @ApiOperation(value = "查询税收数据比例饼图")
    @GetMapping("/tax/pie")
    public String getTaxDataPie(@RequestParam @ApiParam("企业名称") String enterpriseName,
                                @RequestParam @ApiParam("月份数量，0代表全部") int months) {
        List<TaxDataPie> taxDataPies = taxChartService.getTaxDataPie(enterpriseName, months);
        return CollectionUtils.isEmpty(taxDataPies) ? GsonUtil.build(222, "暂无数据", null) : GsonUtil.build(taxDataPies);
    }

    /**
     * @Description 查询税收数据趋势
     * @Title getTaxTrend
     * @Param [enterpriseName, months]
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/04/28 17:18
     */
    @ApiOperation(value = "查询税收数据趋势")
    @GetMapping("/tax/trend")
    public String getTaxTrend(@RequestParam @ApiParam("企业名称") String enterpriseName,
                                @RequestParam @ApiParam("月份数量，0代表全部") int months) {
        return GsonUtil.build(taxChartService.getTaxTrend(enterpriseName, months));
    }

    /**
     * @Description 查询税收数据折线图
     * @Title getTaxLine
     * @Param [enterpriseName, months]
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/04/29 10:41
     */
    @ApiOperation(value = "查询税收数据折线图")
    @GetMapping("/tax/line")
    public String getTaxLine(@RequestParam @ApiParam("企业名称") String enterpriseName,
                              @RequestParam @ApiParam("月份数量，0代表全部") int months) {
        return GsonUtil.build(taxChartService.getTaxLine(enterpriseName, months));
    }



}
