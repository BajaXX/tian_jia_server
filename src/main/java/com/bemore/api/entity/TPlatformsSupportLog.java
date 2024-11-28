package com.bemore.api.entity;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_platforms_support_log")
public class TPlatformsSupportLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String bankAccount;
  private String depositBank;
  private int date;
  private String garden;
  private int year;
  private long status;
  private String platformId;
  private String platformName;
  private String platformContractId;
  private String contractId;
  private double platformSupportAmount;
  private double platformMonthAmount;
  private double platformSurplus;
  private String supportAreas;
  private String supportProject;




}
