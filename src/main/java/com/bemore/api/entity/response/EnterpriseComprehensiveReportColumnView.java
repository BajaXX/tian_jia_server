package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName EnterpriseComprehensiveReportColumnView
 * @Description 企业综合报表字段
 * @Author Louis
 * @Date 2022/04/21 17:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseComprehensiveReportColumnView {

    @ApiModelProperty(value="公司工商信息字段")
    private List<EnterpriseComprehensiveReportColumn> businessColumns;

    @ApiModelProperty(value="公司人员信息字段")
    private List<EnterpriseComprehensiveReportColumn> personColumns;

    @ApiModelProperty(value="税收信息字段")
    private List<EnterpriseComprehensiveReportColumn> taxColumns;

    @ApiModelProperty(value="扶持信息字段")
    private List<EnterpriseComprehensiveReportColumn> supportColumns;

    @ApiModelProperty(value="区级收入字段")
    private List<EnterpriseComprehensiveReportColumn> districtIncomeColumns;


}
