package com.bemore.api.entity.response;

import com.bemore.api.annotation.ExcelColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnterpriseSupportDetailView {

    @ExcelColumn("财政所")
    @ApiModelProperty(value="财政所", name="financialLocation")
    private String financialLocation;

    @ExcelColumn("经济区")
    @ApiModelProperty(value="经济区", name="garden")
    private String garden;

    @ExcelColumn("操作方式")
    @ApiModelProperty(value="操作方式", name="operation")
    private String operation;

    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;

    @ExcelColumn("扶持协议ID")
    @ApiModelProperty(value="扶持协议ID", name="supportId")
    private String supportId;

    @ApiModelProperty(value="所属年份", name="enterpriseName")
    private Integer year;

    @ApiModelProperty(value="所属月份", name="enterpriseName")
    private Integer month;

    @ExcelColumn("扶持领域")
    @ApiModelProperty(value="扶持领域", name="industry")
    private String supportAreas;

    @ExcelColumn("扶持项目")
    @ApiModelProperty(value="扶持项目", name="supportProject")
    private String supportProject;

    @ExcelColumn("开户银行")
    @ApiModelProperty(value="开户银行", name="depositBank")
    private String depositBank;

    @ExcelColumn("银行帐号")
    @ApiModelProperty(value="银行帐号", name="bankAccount")
    private String bankAccount;

    @ExcelColumn("扶持金额")
    @ApiModelProperty(value="扶持金额", name="supportAmount")
    private Double supportAmountValue;

    @ExcelColumn("当月扶持")
    @ApiModelProperty(value="当月扶持", name="monthAmount")
    private Double monthAmountValue;

    @ExcelColumn("当月结余")
    @ApiModelProperty(value="当月结余（满一万扶持，不满一万的累计至次年1月算扶持金的时候清算，清算时5000以下舍去，5000含以上扶持1万）", name="supportAmount")
    private Double surplusValue;

    @ApiModelProperty(value="扶持金额", name="supportAmount")
    private String supportAmount;

    @ApiModelProperty(value="当月扶持", name="monthAmount")
    private String monthAmount;

    @ApiModelProperty(value="当月结余", name="supportAmount")
    private String surplus;

    @ApiModelProperty(value="所属月度", name="date")
    private String date;

    @ExcelColumn("备注")
    @ApiModelProperty(value="备注", name="remark")
    private String remark;
}
