package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SupportAgreementView {
    private String id;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="状态：创建，启用，禁用", name="status")
    private String state;
    @ApiModelProperty(value="协议开始时间", name="startDate")
    private String startDate;
    @ApiModelProperty(value="协议结束时间", name="endDate")
    private String endDate;
    @ApiModelProperty(value="扶持领域", name="industry")
    private String industry;
    @ApiModelProperty(value="开户银行", name="depositBank")
    private String depositBank;
    @ApiModelProperty(value="银行帐号", name="bankAccount")
    private String bankAccount;
    @ApiModelProperty(value="基金企业", name="enterpriseFund")
    private String enterpriseFund;
    @ApiModelProperty(value="集团率基金", name="groupRateFund")
    private String groupRateFund;
    @ApiModelProperty(value="集团率", name="groupRate")
    private String groupRate;
    @ApiModelProperty(value="增值税企业率", name="addedTaxRate")
    private String addedTaxRate;
    @ApiModelProperty(value="所得税企业率", name="incomeTaxRate")
    private String incomeTaxRate;
    @ApiModelProperty(value="个人所得税企业率", name="personTaxRate")
    private String personTaxRate;
    @ApiModelProperty(value="增税率计算值", name="addedTaxRateValue")
    private String addedTaxRateValue;
    @ApiModelProperty(value="企税率计算值", name="incomeTaxRateValue")
    private String incomeTaxRateValue;
    @ApiModelProperty(value="个税率计算值", name="personTaxRateValue")
    private String personTaxRateValue;
    private String createTime;
    private String updateTime;
    private String platformName;
    private String platformId;
}
