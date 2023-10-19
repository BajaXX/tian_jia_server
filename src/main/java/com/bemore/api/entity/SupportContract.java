package com.bemore.api.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "t_support_contract")
public class SupportContract {
  @Id
  @GeneratedValue(generator = "idGenerator")
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  private String id;
  private String enterpriseName;
  private String bankAccount;
  private String depositBank;
  private int startDate;
  private int endDate;
  private int isFund;
  private String contractId;
  private String contractName;
  private String platformId;
  private String platformName;
  private int status;
  private int createTime;
  private int cancelDate;
  private String contractNo;
  private String remark;



}
