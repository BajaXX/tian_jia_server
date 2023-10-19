package com.bemore.api.entity.request;

import com.bemore.api.entity.response.EnterpriseComprehensiveReportColumnView;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName EnterpriseComprehensiveReportParam
 * @Description 企业综合报表导出参数
 * @Author Louis
 * @Date 2022/04/21 21:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseComprehensiveReportParam {

    @ApiModelProperty("开始时间")
    private String startDate;

    @ApiModelProperty("结束时间")
    private String endDate;

    @ApiModelProperty("企业名称集合")
    private List<String> enterpriseNameList;

    @ApiModelProperty("字段")
    private EnterpriseComprehensiveReportColumnView enterpriseComprehensiveReportColumnView;


}
