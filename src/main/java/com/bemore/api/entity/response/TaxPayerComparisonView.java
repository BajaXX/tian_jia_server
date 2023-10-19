package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxPayerComparisonView {
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="行业", name="industry")
    private String industry;
    @ApiModelProperty(value="今年的起始月结束月的税收", name="selectedMonthTax")
    private Double selectedMonthTax;
    @ApiModelProperty(value="去年的起始月结束月的税收", name="lastYearSelectedMonthTax")
    private Double lastYearSelectedMonthTax;
    @ApiModelProperty(value="今年的起始月结束月的排名", name="selectedMonthRanking")
    private Integer selectedMonthRanking;
    @ApiModelProperty(value="去年的起始月结束月的排名", name="lastYearSelectedMonthRanking")
    private Integer lastYearSelectedMonthRanking;
    @ApiModelProperty(value="今年与去年税收差额", name="yearDifferential")
    private Double yearDifferential;
    @ApiModelProperty(value="累计增减率", name="differenceRatio")
    private String differenceRatio;
    @ApiModelProperty(value="今年当月税收", name="monthTax")
    private Double monthTax;
    @ApiModelProperty(value="去年当月税收", name="lastYearMonthTax")
    private Double lastYearMonthTax;
    @ApiModelProperty(value="去年当月排名", name="lastYearMonthRanking")
    private Integer lastYearMonthRanking;
    @ApiModelProperty(value="今年当月排名", name="monthRanking")
    private Integer monthRanking;
    @ApiModelProperty(value="去年今年当月差额", name="monthDifferential")
    private Double monthDifferential;
    @ApiModelProperty(value="当月增减率", name="monthDifferenceRatio")
    private String monthDifferenceRatio;

}
