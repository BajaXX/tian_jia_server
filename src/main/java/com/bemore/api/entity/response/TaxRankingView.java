package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxRankingView {
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="本月税金合计", name="totalTax")
    private Double totalTax;
    @ApiModelProperty(value="累计税金合计", name="totalTaxTotal")
    private Double totalTaxTotal;
    @ApiModelProperty(value="本月增值税", name="addedTax")
    private Double addedTax;
    @ApiModelProperty(value="累计增值税", name="addedTaxTotal")
    private Double addedTaxTotal;
    @ApiModelProperty(value="本月改增增值税", name="toAddedTax")
    private Double toAddedTax;
    @ApiModelProperty(value="累计改增增值税", name="toAddedTaxTotal")
    private Double toAddedTaxTotal;
    @ApiModelProperty(value="本月所得税", name="incomeTax")
    private Double incomeTax;
    @ApiModelProperty(value="累计所得税", name="incomeTaxTotal")
    private Double incomeTaxTotal;
    @ApiModelProperty(value="本月城建税", name="cityTax")
    private Double cityTax;
    @ApiModelProperty(value="累计城建税", name="cityTaxTotal")
    private Double cityTaxTotal;
    @ApiModelProperty(value="本月个税", name="personTax")
    private Double personTax;
    @ApiModelProperty(value="累计个税", name="personTaxTotal")
    private Double personTaxTotal;
    @ApiModelProperty(value="本月消费税", name="excise")
    private Double excise;
    @ApiModelProperty(value="累计消费税", name="exciseTotal")
    private Double exciseTotal;
    @ApiModelProperty(value="本月土增税", name="addedLandTax")
    private Double addedLandTax;
    @ApiModelProperty(value="累计土增税", name="addedLandTaxTotal")
    private Double addedLandTaxTotal;
    @ApiModelProperty(value="本月印花税", name="stampTax")
    private Double stampTax;
    @ApiModelProperty(value="累计印花税", name="stampTaxTotal")
    private Double stampTaxTotal;
    @ApiModelProperty(value="本月房产税", name="houseTax")
    private Double houseTax;
    @ApiModelProperty(value="累计房产税", name="houseTaxTotal")
    private Double houseTaxTotal;
    @ApiModelProperty(value="本月土地税", name="landTax")
    private Double landTax;
    @ApiModelProperty(value="累计土地税", name="landTaxTotal")
    private Double landTaxTotal;
    @ApiModelProperty(value="本月车船税", name="carTax")
    private Double carTax;
    @ApiModelProperty(value="累计车船税", name="carTaxTotal")
    private Double carTaxTotal;
    @ApiModelProperty(value="本月车购税", name="carPurchaseTax")
    private Double carPurchaseTax;
    @ApiModelProperty(value="累计车购税", name="carPurchaseTaxTotal")
    private Double carPurchaseTaxTotal;
}
