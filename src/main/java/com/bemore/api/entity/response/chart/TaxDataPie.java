package com.bemore.api.entity.response.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TaxDataPie
 * @Description 税收数据饼图
 * @Author Louis
 * @Date 2022/04/28 13:49
 */
@ApiModel("税收数据饼图")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxDataPie {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("数值")
    private Double value;

    @ApiModelProperty("百分比")
    private String percentage;

}
