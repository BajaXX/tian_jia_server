package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EnterpriseComprehensiveReportColumn
 * @Description 企业综合报表字段
 * @Author Louis
 * @Date 2022/04/21 18:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseComprehensiveReportColumn {

    @ApiModelProperty(value="字段")
    private String field;

    @ApiModelProperty(value="名称")
    private String label;

}
