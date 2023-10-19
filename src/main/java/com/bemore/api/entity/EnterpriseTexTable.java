package com.bemore.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EnterpriseTexTable {
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="企业名", name="name")
    private String enterpriseName;
    @ApiModelProperty(value="所处流程(1新开，2迁入，3变更，4注销，5正常)", name="process")
    private String process;
    @ApiModelProperty(value="本区(实际经营本区)", name="thisRegion")
    private String thisRegion;
    @ApiModelProperty(value="三合一", name="registerNum")
    private String registerNum;
    @ApiModelProperty(value="成立日期(营业执照开始日期)", name="startDate")
    private String foundDate;
    @ApiModelProperty(value="企业类型（性质）", name="enterpriseType")
    private String enterpriseType;
    @ApiModelProperty(value="企业行业", name="industry")
    private String industry;
    @ApiModelProperty(value="招商来源", name="source")
    private String source;
    @ApiModelProperty(value="跟踪员", name="follower")
    private String follower;
    @ApiModelProperty(value="注册资本", name="capital")
    private Double capital;
    @ApiModelProperty(value="注册地址", name="registerAddress")
    private String registerAddress;
    @ApiModelProperty(value="实际联系地址", name="actContactAddress")
    private String actContactAddress;
    @ApiModelProperty(value="报税日期", name="taxTate")
    private String date;
    @ApiModelProperty(value="报税月份", name="taxMonth")
    private Integer month;
    @ApiModelProperty(value="累计月税收", name="totalTax")
    private Double totalTax;
    @ApiModelProperty(value="变更流程上报日期", name="reportDate")
    private String reportDate;
    @ApiModelProperty(value="起缴日期", name="paymentDate")
    private String paymentDate;
}
