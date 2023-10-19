package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bemore.api.annotation.ExcelColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
@Entity
@Table(name = "t_enterprise_tax")
@TableName(value = "t_enterprise_tax")
@Data
@Builder
@AllArgsConstructor
@ApiModel(value = "企业工商/税收信息表实体类")
public class EnterpriseTaxPlus {

    @Id
    @TableId(type = IdType.ASSIGN_UUID)
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

    @ExcelColumn(value="销售额")
    @ApiModelProperty(value="本月销售额", name="sales")
    private Double sales = 0.0;
    @ApiModelProperty(value="累计销售额", name="salesTotal")
    private Double salesTotal;

    @ExcelColumn(value="营业额")
    @ApiModelProperty(value="本月营业额", name="business")
    private Double business = 0.0;
    @ApiModelProperty(value="累计营业额", name="businessTotal")
    private Double businessTotal;

    @ExcelColumn(value="增值税")
    @ApiModelProperty(value="本月增值税", name="addedTax")
    private Double addedTax = 0.0;
    @ApiModelProperty(value="累计增值税", name="addedTaxTotal")
    private Double addedTaxTotal;

    @ExcelColumn(value="营改增")
    @ApiModelProperty(value="本月营改增", name="toAddedTax")
    private Double toAddedTax = 0.0;
    @ApiModelProperty(value="累计营改增", name="toAddedTaxTotal")
    private Double toAddedTaxTotal;

    @ExcelColumn(value="营业税")
    @ApiModelProperty(value="本月营业税", name="businessTax")
    private Double businessTax = 0.0;
    @ApiModelProperty(value="累计营业税", name="businessTaxTotal")
    private Double businessTaxTotal;

    @ExcelColumn(value="消费税")
    @ApiModelProperty(value="本月消费税", name="excise")
    private Double excise = 0.0;
    @ApiModelProperty(value="累计消费税", name="exciseTotal")
    private Double exciseTotal;

    @ExcelColumn(value="企业所得税")
    @ApiModelProperty(value="本月企业所得税", name="incomeTax")
    private Double incomeTax = 0.0;
    @ApiModelProperty(value="累计企业所得税", name="incomeTaxTotal")
    private Double incomeTaxTotal;

    @ExcelColumn(value="个人所得税")
    @ApiModelProperty(value="本月个人所得税", name="personTax")
    private Double personTax = 0.0;
    @ApiModelProperty(value="累计个人所得税", name="personTaxTotal")
    private Double personTaxTotal;

    @ExcelColumn(value="房产税")
    @ApiModelProperty(value="本月房产税", name="houseTax")
    private Double houseTax = 0.0;
    @ApiModelProperty(value="累计房产税", name="houseTaxTotal")
    private Double houseTaxTotal;

    @ExcelColumn(value="车船使用税")
    @ApiModelProperty(value="本月车船使用税", name="carTax")
    private Double carTax = 0.0;
    @ApiModelProperty(value="累计车船使用税", name="carTaxTotal")
    private Double carTaxTotal;

    @ExcelColumn(value="印花税")
    @ApiModelProperty(value="本月印花税", name="stampTax")
    private Double stampTax = 0.0;
    @ApiModelProperty(value="累计印花税", name="stampTaxTotal")
    private Double stampTaxTotal;

    @ExcelColumn(value="城镇土地使用税")
    @ApiModelProperty(value="本月城镇土地使用税", name="landTax")
    private Double landTax = 0.0;
    @ApiModelProperty(value="累计城镇土地使用税", name="landTaxTotal")
    private Double landTaxTotal;

    @ExcelColumn(value="土地增值税")
    @ApiModelProperty(value="本月土地增值税", name="addedLandTax")
    private Double addedLandTax = 0.0;
    @ApiModelProperty(value="累计土地增值税", name="addedLandTaxTotal")
    private Double addedLandTaxTotal;

    @ExcelColumn(value="城建税")
    @ApiModelProperty(value="本月城建税", name="cityTax")
    private Double cityTax = 0.0;
    @ApiModelProperty(value="累计城建税", name="cityTaxTotal")
    private Double cityTaxTotal;

    @ExcelColumn(value="环境保护税")
    @ApiModelProperty(value="本月环境保护税", name="environmentTax")
    private Double environmentTax = 0.0;
    @ApiModelProperty(value="累计环境保护税", name="environmentTaxTotal")
    private Double environmentTaxTotal;

    @ExcelColumn(value="耕地占用税")
    @ApiModelProperty(value="本月耕地占用税", name="farmlandTax")
    private Double farmlandTax = 0.0;
    @ApiModelProperty(value="累计耕地占用税", name="farmlandTaxTotal")
    private Double farmlandTaxTotal;

    @ExcelColumn(value="车购税")
    @ApiModelProperty(value="本月车购税", name="carPurchaseTax")
    private Double carPurchaseTax = 0.0;
    @ApiModelProperty(value="累计车购税", name="carPurchaseTaxTotal")
    private Double carPurchaseTaxTotal;

    @ExcelColumn(value="契税")
    @ApiModelProperty(value="本月契税", name="deedTax")
    private Double deedTax = 0.0;
    @ApiModelProperty(value="累计契税", name="deedTaxTotal")
    private Double deedTaxTotal;

    @ExcelColumn(value="税金合计")
    @ApiModelProperty(value="本月税金合计", name="totalTax")
    private Double totalTax = 0.0;
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
    private Double capital;

    @ApiModelProperty(value="招商来源", name="source")
    private String source;

    @ApiModelProperty(value="入住类型", name="inType")
    private String inType;

    @ApiModelProperty(value="开业类型(所处流程)", name="process")
    private String process;

    @ApiModelProperty(value="机构代码证（三合一码）", name="registerNum")
    private String registerNum;

    @ApiModelProperty(value="起缴日期", name="paymentDate")
    private LocalDate paymentDate;

    @ApiModelProperty(value="入驻日期", name="settledDate")
    private String settledDate;

    @ApiModelProperty(value="机构类型", name="institutionalType")
    private String institutionalType;

    @ApiModelProperty(value="投资类型", name="investmentType")
    private String investmentType;



    public EnterpriseTaxPlus() {
        this.sales = 0.0;
        this.salesTotal = 0.0;
        this.business = 0.0;
        this.businessTotal = 0.0;
        this.addedTax = 0.0;
        this.addedTaxTotal = 0.0;
        this.toAddedTax = 0.0;
        this.toAddedTaxTotal = 0.0;
        this.businessTax = 0.0;
        this.businessTaxTotal = 0.0;
        this.excise = 0.0;
        this.exciseTotal = 0.0;
        this.incomeTax = 0.0;
        this.incomeTaxTotal = 0.0;
        this.personTax = 0.0;
        this.personTaxTotal = 0.0;
        this.houseTax = 0.0;
        this.houseTaxTotal = 0.0;
        this.carTax = 0.0;
        this.carTaxTotal = 0.0;
        this.stampTax = 0.0;
        this.stampTaxTotal = 0.0;
        this.landTax = 0.0;
        this.landTaxTotal = 0.0;
        this.addedLandTax = 0.0;
        this.addedLandTaxTotal = 0.0;
        this.cityTax = 0.0;
        this.cityTaxTotal = 0.0;
        this.environmentTax = 0.0;
        this.environmentTaxTotal = 0.0;
        this.farmlandTax = 0.0;
        this.farmlandTaxTotal = 0.0;
        this.carPurchaseTax = 0.0;
        this.carPurchaseTaxTotal = 0.0;
        this.deedTax = 0.0;
        this.deedTaxTotal = 0.0;
        this.totalTax = 0.0;
        this.totalTaxTotal = 0.0;
    }
}
