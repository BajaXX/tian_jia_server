package com.bemore.api.entity.response.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ChartData
 * @Description 图表数据
 * @Author Louis
 * @Date 2022/04/29 16:09
 */
@ApiModel("图表数据")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartData {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty(value = "X轴")
    private List<String> category;

    @ApiModelProperty(value = "数据")
    private List<Double> data;

}
