package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName(value = "t_support_agreement")
@Data
@ApiModel(value = "园区企业入驻信息表")
@Entity
@Table(name = "t_support_agreement")
public class SupportAgreement {
  @Id
  @TableId(type = IdType.ASSIGN_UUID)
  @Column(length = 36)
  private String id;
  @ApiModelProperty(value="企业名称", name="enterpriseName")
  private String enterpriseName;
  @ApiModelProperty(value="状态：创建，启用，禁用", name="status")
  private String state;
  @ApiModelProperty(value="协议开始时间", name="startDate")
  private LocalDate startDate;
  @ApiModelProperty(value="协议结束时间", name="endDate")
  private LocalDate endDate;
  @ApiModelProperty(value="扶持领域", name="industry")
  private String supportAreas;
  @ApiModelProperty(value="扶持项目", name="supportProject")
  private String supportProject;
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
  private LocalDateTime createTime;
  private String updateTime;
  private String platformId;

}
