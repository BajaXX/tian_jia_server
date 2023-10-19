package com.bemore.api.entity.response.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TaxChart
 * @Description 税收图表
 * @Author Louis
 * @Date 2022/04/28 19:06
 */
@ApiModel("税收图表")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxChart {

    @ApiModelProperty("销售额")
    private ChartData sales;

    @ApiModelProperty("增值税")
    private ChartData addedTax;

    @ApiModelProperty("企业所得税")
    private ChartData incomeTax;

    @ApiModelProperty("个人所得税")
    private ChartData personTax;

    @ApiModelProperty("城建税")
    private ChartData cityTax;

    @ApiModelProperty("其他")
    private ChartData otherTax;

}
