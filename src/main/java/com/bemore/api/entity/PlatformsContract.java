package com.bemore.api.entity;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "t_platforms_contract")
public class PlatformsContract {
  @Id
  @GeneratedValue(generator = "idGenerator")
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  private String id;
  private String platformId;
  private String platformName;
  private String contractNo;
  private String bankAccount;
  private String depositBank;
  private String remark;
  private int agreementStart;
  private int agreementEnd;
  private int isFund;
  private String contractId;
  private String contractName;
  private int cstatus;
  private int cancelDate;
  private String supportAreas;
  private String supportProject;
  private String supportFiles;

}
