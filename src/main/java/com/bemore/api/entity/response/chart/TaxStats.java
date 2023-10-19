package com.bemore.api.entity.response.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TaxStats
 * @Description 税收统计
 * @Author Louis
 * @Date 2022/04/28 15:36
 */
@ApiModel("税收统计")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxStats {

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("年份")
    private int year;

    @ApiModelProperty("月份")
    private int month;

    @ApiModelProperty("当月总税收")
    private Double currentMonthTotalTax;

    @ApiModelProperty("当年总税收")
    private Double currentYearTotalTax;

    @ApiModelProperty("当月排名")
    private int currentMonthRanking;

    @ApiModelProperty("当年排名")
    private int currentYearRanking;

}
