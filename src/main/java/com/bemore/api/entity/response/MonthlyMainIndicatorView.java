package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class MonthlyMainIndicatorView {
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="企业名", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="企业类型（性质）", name="enterpriseType")
    private String enterpriseType;
    @ApiModelProperty(value="企业行业", name="industry")
    private String industry;
    @ApiModelProperty(value="本月销售额", name="sales")
    private Double sales;
    @ApiModelProperty(value="累计销售额", name="salesTotal")
    private Double salesTotal;
    @ApiModelProperty(value="本月税金", name="tax")
    private Double totalTax;
    @ApiModelProperty(value="累计税金", name="taxTotal")
    private String totalTaxTotal;
    private Double totalTaxTotalValue;
}
