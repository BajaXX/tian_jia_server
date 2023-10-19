package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxMarginComparisonView {
    @ApiModelProperty(value="企业名", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="今年税收累计", name="currentYearTax")
    private String currentYearTax;
    @ApiModelProperty(value="去年税收累计", name="lastYearTax")
    private String lastYearTax;
    @ApiModelProperty(value="前年税收累计", name="yearBeforeLastTax")
    private String yearBeforeLastTax;
    @ApiModelProperty(value="今年增减幅", name="differential")
    private String differential;
    @ApiModelProperty(value="去年增减幅", name="differential")
    private String lastYearDifferential;
    @ApiModelProperty(value="前年增减幅", name="differential")
    private String yearBeforeLastDifferential;
}
