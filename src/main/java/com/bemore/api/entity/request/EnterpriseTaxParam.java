package com.bemore.api.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "企业税收信息入参")
public class EnterpriseTaxParam {
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="税收年月 yyyy-mm", name="date")
    private String date;
    @ApiModelProperty(value="本月销售额", name="sales")
    private String sales;
    @ApiModelProperty(value="累计销售额", name="salesTotal")
    private String salesTotal;
    @ApiModelProperty(value="本月营业额", name="business")
    private String business;
    @ApiModelProperty(value="累计营业额", name="businessTotal")
    private String businessTotal;
    @ApiModelProperty(value="本月增值税", name="addedTax")
    private String addedTax;
    @ApiModelProperty(value="累计增值税", name="addedTaxTotal")
    private String addedTaxTotal;
    @ApiModelProperty(value="本月改增增值税", name="toAddedTax")
    private String toAddedTax;
    @ApiModelProperty(value="累计改增增值税", name="toAddedTaxTotal")
    private String toAddedTaxTotal;
    @ApiModelProperty(value="本月营业税", name="businessTax")
    private String businessTax;
    @ApiModelProperty(value="累计营业税", name="businessTaxTotal")
    private String businessTaxTotal;
    @ApiModelProperty(value="本月消费税", name="excise")
    private String excise;
    @ApiModelProperty(value="累计消费税", name="exciseTotal")
    private String exciseTotal;
    @ApiModelProperty(value="本月所得税", name="incomeTax")
    private String incomeTax;
    @ApiModelProperty(value="累计所得税", name="incomeTaxTotal")
    private String incomeTaxTotal;
    @ApiModelProperty(value="本月个税", name="personTax")
    private String personTax;
    @ApiModelProperty(value="累计个税", name="personTaxTotal")
    private String personTaxTotal;
    @ApiModelProperty(value="本月房产税", name="houseTax")
    private String houseTax;
    @ApiModelProperty(value="累计房产税", name="houseTaxTotal")
    private String houseTaxTotal;
    @ApiModelProperty(value="本月车船税", name="carTax")
    private String carTax;
    @ApiModelProperty(value="累计车船税", name="carTaxTotal")
    private String carTaxTotal;
    @ApiModelProperty(value="本月印花税", name="stampTax")
    private String stampTax;
    @ApiModelProperty(value="累计印花税", name="stampTaxTotal")
    private String stampTaxTotal;
    @ApiModelProperty(value="本月土地税", name="landTax")
    private String landTax;
    @ApiModelProperty(value="累计土地税", name="landTaxTotal")
    private String landTaxTotal;
    @ApiModelProperty(value="本月土增税", name="addedLandTax")
    private String addedLandTax;
    @ApiModelProperty(value="累计土增税", name="addedLandTaxTotal")
    private String addedLandTaxTotal;
    @ApiModelProperty(value="本月城建税", name="cityTax")
    private String cityTax;
    @ApiModelProperty(value="累计城建税", name="cityTaxTotal")
    private String cityTaxTotal;
    @ApiModelProperty(value="本月环保税", name="environmentTax")
    private String environmentTax;
    @ApiModelProperty(value="累计环保税", name="environmentTaxTotal")
    private String environmentTaxTotal;
    @ApiModelProperty(value="本月耕地占用税", name="farmlandTax")
    private String farmlandTax;
    @ApiModelProperty(value="累计耕地占用税", name="farmlandTaxTotal")
    private String farmlandTaxTotal;
    @ApiModelProperty(value="本月车购税", name="carPurchaseTax")
    private String carPurchaseTax;
    @ApiModelProperty(value="累计车购税", name="carPurchaseTaxTotal")
    private String carPurchaseTaxTotal;
    @ApiModelProperty(value="本月契税", name="deedTax")
    private String deedTax;
    @ApiModelProperty(value="累计契税", name="deedTaxTotal")
    private String deedTaxTotal;
    @ApiModelProperty(value="本月税金合计", name="totalTax")
    private String totalTax;
    @ApiModelProperty(value="累计税金合计", name="totalTaxTotal")
    private String totalTaxTotal;

}
