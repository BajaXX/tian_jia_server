package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NewEnterpriseTaxView {
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="企业名", name="name")
    private String enterpriseName;
    @ApiModelProperty(value="所处流程(1新开，2迁入，3变更，4注销，5正常)", name="process")
    private String process;
    @ApiModelProperty(value="三合一", name="registerNum")
    private String registerNum;
    @ApiModelProperty(value="企业类型（性质）", name="enterpriseType")
    private String enterpriseType;
    @ApiModelProperty(value="注册地址", name="registerAddress")
    private String registerAddress;
    @ApiModelProperty(value="企业行业", name="industry")
    private String industry;
    @ApiModelProperty(value="注册资本", name="capital")
    private Double capital;
    @ApiModelProperty(value="成立日期(营业执照开始日期)", name="foundDate")
    private String foundDate;
    @ApiModelProperty(value="起缴日期", name="date")
    private String paymentDate;
    @ApiModelProperty(value="查询的结束月份税收", name="lastMonthTax")
    private Double monthTax;
    @ApiModelProperty(value="倒推去年查询的结束月份税收", name="lastYearLastMonthTax")
    private Double lastYearLastMonthTax;
    @ApiModelProperty(value="当月增减额", name="monthDifferential")
    private Double monthDifferential;
    @ApiModelProperty(value="当月增减率", name="monthDifferenceRatio")
    private String monthDifferenceRatio;
    @ApiModelProperty(value="查询条件的税收开始月份到税收结束月份税收合计", name="selectedMonthTax")
    private Double selectedMonthTax;
    @ApiModelProperty(value="查询条件的税收开始月份到税收结束月份倒推前一年相同月份的税收合计", name="lastYearSelectedMonthTax")
    private Double lastYearSelectedMonthTax;
    @ApiModelProperty(value="今年与去年税收差额", name="yearDifferential")
    private Double yearDifferential;
    @ApiModelProperty(value="累计增减率", name="differenceRatio")
    private String differenceRatio;
    @ApiModelProperty(value="今年至上月(当前月 -2)税收", name="currentYearLastMonthTax")
    private Double currentYearLastMonthTax;
    @ApiModelProperty(value="跟踪员", name="follower")
    private String follower;

}
