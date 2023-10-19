package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxMarginTableView {
    @ApiModelProperty(value="年月", name="enterpriseNo")
    private String date;
    @ApiModelProperty(value="增值税", name="addedTax")
    private Double addedTax;
    @ApiModelProperty(value="本月改增增值税", name="toAddedTax")
    private Double toAddedTax;
    @ApiModelProperty(value="本月营业税", name="businessTax")
    private Double businessTax;
    @ApiModelProperty(value="城建税", name="cityTax")
    private Double cityTax;
    @ApiModelProperty(value="企业所得税", name="incomeTax")
    private Double incomeTax;
    @ApiModelProperty(value="个人所得税", name="personTax")
    private Double personTax;
    @ApiModelProperty(value="消费税", name="excise")
    private Double excise;
    @ApiModelProperty(value="土地增值税", name="addedLandTax")
    private Double addedLandTax;
    @ApiModelProperty(value="房产税", name="houseTax")
    private Double houseTax;
    @ApiModelProperty(value="印花税", name="stampTax")
    private Double stampTax;
    @ApiModelProperty(value="税金合计", name="totalTaxTotal")
    private Double totalTaxTotal;
    @ApiModelProperty(value="增减率", name="differenceRatio")
    private String differenceRatio;
}
