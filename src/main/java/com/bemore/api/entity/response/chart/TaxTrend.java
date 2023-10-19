package com.bemore.api.entity.response.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName TaxTrend
 * @Description 税收趋势
 * @Author Louis
 * @Date 2022/04/28 17:13
 */
@ApiModel("税收趋势")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxTrend {

    @ApiModelProperty("时间排序日期")
    private List<String> timeSortDate;

    @ApiModelProperty("时间排序数据")
    private List<Double> timeSortValue;

    @ApiModelProperty("数据排序日期")
    private List<String> valueSortDate;

    @ApiModelProperty("数据排序数据")
    private List<Double> valueSortValue;

}
