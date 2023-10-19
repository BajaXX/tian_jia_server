package com.bemore.api.entity.response;

import com.bemore.api.annotation.ExcelColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName EnterpriseSettledTaxView
 * @Description 长三角金融产业园入驻企业清单
 * @Author Louis
 * @Date 2022/04/25 15:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseSettledTaxView {

    @ApiModelProperty(value="序号：1、2、3......")
    @ExcelColumn("序号")
    private int serialNumber;

    @ApiModelProperty(value="企业名称")
    @ExcelColumn("企业名称")
    private String enterpriseName;

    @ApiModelProperty("基金管理规模或基金规模（亿元）：默认为空，空值用正斜杠表示")
    @ExcelColumn("基金管理规模或基金规模（亿元）")
    private String fundManagementScale;

    @ApiModelProperty("机构类型")
    @ExcelColumn("机构类型")
    private String institutionalType;

    @ApiModelProperty("投资类型")
    @ExcelColumn("投资类型")
    private String investmentType;

    @ApiModelProperty("历年年度税收集合（万元）按年度正序排列")
    private List<YearTax> historyYearTaxList;

    @ApiModelProperty("迁入/新设：默认为空，不加额外字符表示。输入时只能在以下内容中选择“新设、本区迁入、外区迁入、外省市迁入”")
    @ExcelColumn("迁入/新设")
    private String moveType;

    @ApiModelProperty("当年月度税收集合（万元）按月度正序排列")
    private List<YearTax> currentYearTaxList;

    @ApiModelProperty("是否自主招商：默认为空，输入时只能选择“是”或者“否”")
    @ExcelColumn("是否自主招商")
    private String beIndependentInvestmentPromotion;

    @ApiModelProperty("入驻日期：年月日 格式，默认为空 输入时间不得早于2020年1月1日")
    @ExcelColumn("入驻日期")
    private String settledDate;

    @ApiModelProperty("备注")
    @ExcelColumn("备注")
    private String remake;

    @ApiModelProperty("招商来源")
    @ExcelColumn("招商来源")
    private String source;

    @ApiModelProperty("指定联系人")
    @ExcelColumn("指定联系人")
    private String designatedContact;

    @ApiModelProperty("指定联系人电话")
    @ExcelColumn("指定联系人电话")
    private String designatedContactPhone;

}
