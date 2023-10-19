package com.bemore.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity(name = "t_enterprise_tax0")
@Data
@ApiModel(value = "企业工商/税收信息表实体类")
public class EnterpriseTax {
    @Id
    @GeneratedValue(generator="idGenerator")
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @Column(length = 36)
    private String id;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="税收年月yyyymm", name="date")
    private String date;
    @ApiModelProperty(value="税收年", name="year")
    private Integer year;
    @ApiModelProperty(value="税收月", name="month")
    private Integer month;
    @ApiModelProperty(value="本月销售额", name="sales")
    private Double sales;
    @ApiModelProperty(value="累计销售额", name="salesTotal")
    private Double salesTotal;
    @ApiModelProperty(value="本月营业额", name="business")
    private Double business;
    @ApiModelProperty(value="累计营业额", name="businessTotal")
    private Double businessTotal;
    @ApiModelProperty(value="本月增值税", name="addedTax")
    private Double addedTax;
    @ApiModelProperty(value="累计增值税", name="addedTaxTotal")
    private Double addedTaxTotal;
    @ApiModelProperty(value="本月改增增值税", name="toAddedTax")
    private Double toAddedTax;
    @ApiModelProperty(value="累计改增增值税", name="toAddedTaxTotal")
    private Double toAddedTaxTotal;
    @ApiModelProperty(value="本月营业税", name="businessTax")
    private Double businessTax;
    @ApiModelProperty(value="累计营业税", name="businessTaxTotal")
    private Double businessTaxTotal;
    @ApiModelProperty(value="本月消费税", name="excise")
    private Double excise;
    @ApiModelProperty(value="累计消费税", name="exciseTotal")
    private Double exciseTotal;
    @ApiModelProperty(value="本月所得税", name="incomeTax")
    private Double incomeTax;
    @ApiModelProperty(value="累计所得税", name="incomeTaxTotal")
    private Double incomeTaxTotal;
    @ApiModelProperty(value="本月个税", name="personTax")
    private Double personTax;
    @ApiModelProperty(value="累计个税", name="personTaxTotal")
    private Double personTaxTotal;
    @ApiModelProperty(value="本月房产税", name="houseTax")
    private Double houseTax;
    @ApiModelProperty(value="累计房产税", name="houseTaxTotal")
    private Double houseTaxTotal;
    @ApiModelProperty(value="本月车船税", name="carTax")
    private Double carTax;
    @ApiModelProperty(value="累计车船税", name="carTaxTotal")
    private Double carTaxTotal;
    @ApiModelProperty(value="本月印花税", name="stampTax")
    private Double stampTax;
    @ApiModelProperty(value="累计印花税", name="stampTaxTotal")
    private Double stampTaxTotal;
    @ApiModelProperty(value="本月土地税", name="landTax")
    private Double landTax;
    @ApiModelProperty(value="累计土地税", name="landTaxTotal")
    private Double landTaxTotal;
    @ApiModelProperty(value="本月土增税", name="addedLandTax")
    private Double addedLandTax;
    @ApiModelProperty(value="累计土增税", name="addedLandTaxTotal")
    private Double addedLandTaxTotal;
    @ApiModelProperty(value="本月城建税", name="cityTax")
    private Double cityTax;
    @ApiModelProperty(value="累计城建税", name="cityTaxTotal")
    private Double cityTaxTotal;
    @ApiModelProperty(value="本月环保税", name="environmentTax")
    private Double environmentTax;
    @ApiModelProperty(value="累计环保税", name="environmentTaxTotal")
    private Double environmentTaxTotal;
    @ApiModelProperty(value="本月耕地占用税", name="farmlandTax")
    private Double farmlandTax;
    @ApiModelProperty(value="累计耕地占用税", name="farmlandTaxTotal")
    private Double farmlandTaxTotal;
    @ApiModelProperty(value="本月车购税", name="carPurchaseTax")
    private Double carPurchaseTax;
    @ApiModelProperty(value="累计车购税", name="carPurchaseTaxTotal")
    private Double carPurchaseTaxTotal;
    @ApiModelProperty(value="本月契税", name="deedTax")
    private Double deedTax;
    @ApiModelProperty(value="累计契税", name="deedTaxTotal")
    private Double deedTaxTotal;
    @ApiModelProperty(value="本月税金合计", name="totalTax")
    private Double totalTax;
    @ApiModelProperty(value="累计税金合计", name="totalTaxTotal")
    private Double totalTaxTotal;
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="企业行业", name="industry")
    private String industry;
    @ApiModelProperty(value="开发区", name="garden")
    private String garden;
    @ApiModelProperty(value="成立日期", name="foundDate")
    private LocalDate foundDate;
    @ApiModelProperty(value="经济性质", name="enterpriseType")
    private String enterpriseType;
    @ApiModelProperty(value="项目类型", name="projectType")
    private String projectType;
    @ApiModelProperty(value="上报日期", name="reportDate")
    private String reportDate;
    @ApiModelProperty(value="本区(实际经营本区)", name="thisRegion")
    private String thisRegion;
    @ApiModelProperty(value="实际经营地址", name="actContactAddress")
    private String actContactAddress;
    @ApiModelProperty(value="注册地址", name="registerAddress")
    private String registerAddress;
    @ApiModelProperty(value="注册资本（万元）", name="capital")
    private String capital;
    @ApiModelProperty(value="招商来源", name="source")
    private String source;
    @ApiModelProperty(value="入住类型", name="inType")
    private String inType;
    @ApiModelProperty(value="开业类型(所处流程)", name="process")
    private String process;
    @ApiModelProperty(value="机构代码证（三合一码）", name="registerNum")
    private String registerNum;
}
