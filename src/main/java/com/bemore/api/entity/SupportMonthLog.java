package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@TableName(value = "t_support_month_log")
@Data
@Builder
@ApiModel(value = "企业扶持金月记录（每个企业每月只有一条记录）")
public class SupportMonthLog {
    @Id
    @TableId(type = IdType.ASSIGN_UUID)
    @Column(length = 36)
    private String id;
    @ApiModelProperty(value="财政所", name="financialLocation")
    private String financialLocation;
    @ApiModelProperty(value="经济区", name="garden")
    private String garden;
    @ApiModelProperty(value="操作方式", name="operation")
    private String operation;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="扶持协议ID", name="supportId")
    private String supportId;
    @ApiModelProperty(value="所属年份", name="enterpriseName")
    private Integer year;
    @ApiModelProperty(value="所属月份", name="enterpriseName")
    private Integer month;
    @ApiModelProperty(value="扶持领域", name="industry")
    private String supportAreas;
    @ApiModelProperty(value="扶持项目", name="supportProject")
    private String supportProject;
    @ApiModelProperty(value="开户银行", name="depositBank")
    private String depositBank;
    @ApiModelProperty(value="银行帐号", name="bankAccount")
    private String bankAccount;
    @ApiModelProperty(value="扶持金额", name="supportAmount")
    private String supportAmount;
    @ApiModelProperty(value="当月扶持", name="monthAmount")
    private String monthAmount;
    @ApiModelProperty(value="当月结余（满一万扶持，不满一万的累计至下月算扶持金的时候清算，清算时5000以下舍去，5000含以上扶持1万）", name="surplus")
    private String surplus;
    @ApiModelProperty(value="所属月度", name="date")
    private String date;
    private String platformId;
    private String platformAmount;
    private String baseAmount;
}
