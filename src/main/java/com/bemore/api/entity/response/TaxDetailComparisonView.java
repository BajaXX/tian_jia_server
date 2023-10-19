package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxDetailComparisonView {
    @ApiModelProperty(value="年月", name="enterpriseNo")
    private String date;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="增值税", name="enterpriseNo")
    private String addedTax;
    @ApiModelProperty(value="城建税", name="enterpriseNo")
    private String cityTax;
    @ApiModelProperty(value="企业所得税", name="enterpriseNo")
    private String incomeTax;
    @ApiModelProperty(value="个人所得税", name="enterpriseNo")
    private String personTax;
    @ApiModelProperty(value="消费税", name="enterpriseNo")
    private String excise;
    @ApiModelProperty(value="土地增值税", name="enterpriseNo")
    private String addedLandTax;
    @ApiModelProperty(value="房产税", name="enterpriseNo")
    private String houseTax;
    @ApiModelProperty(value="印花税", name="enterpriseNo")
    private String stampTax;
    @ApiModelProperty(value="税金合计", name="enterpriseNo")
    private String totalTaxTotal;

}
