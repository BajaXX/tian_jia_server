package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName YearTax
 * @Description 年度税收对象
 * @Author Louis
 * @Date 2022/04/25 16:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearTax {

    @ApiModelProperty(value="日期：可以为yyyy，可以为yyyy-MM")
    private String date;

    @ApiModelProperty(value="数值")
    private String value;


}
