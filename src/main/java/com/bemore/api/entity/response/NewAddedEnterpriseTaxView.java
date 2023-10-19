package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewAddedEnterpriseTaxView {
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="机构代码证（三合一码）", name="registerNum")
    private String registerNum;
    @ApiModelProperty(value="招商来源", name="source")
    private String source;
    @ApiModelProperty(value="实际经营地址", name="actContactAddress")
    private String actContactAddress;
    @ApiModelProperty(value="开业类型(所处流程)", name="process")
    private String process;
    @ApiModelProperty(value="成立日期", name="foundDate")
    private String foundDate;
    @ApiModelProperty(value="上报（建档）日期", name="reportDate")
    private String reportDate;
    @ApiModelProperty(value="起缴日期", name="paymentDate")
    private String paymentDate;
    @ApiModelProperty(value="本月税金合计", name="totalTax")
    private Double totalTax;
    @ApiModelProperty(value="本年度累计税金合计", name="totalTaxTotal")
    private Double totalTaxTotal;
    @ApiModelProperty(value="是否新增有效户", name="totalTaxTotal")
    private String newValidAccount;
    @ApiModelProperty(value="全部年度纳税总额", name="total")
    private Double total;
}
