package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxMarginRankingView {
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="企业名", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="今年税收累计", name="currentYearTax")
    private String currentYearTax;
    private Double currentYearTaxValue;
    @ApiModelProperty(value="去年税收累计", name="lastYearTax")
    private String lastYearTax;
    @ApiModelProperty(value="增减幅", name="differential")
    private String differential;
}
