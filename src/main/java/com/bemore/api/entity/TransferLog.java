package com.bemore.api.entity;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_transfer_log")
public class TransferLog {

  @Id
  @GeneratedValue(generator = "idGenerator")
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  private String id;
  private String enterpriseId;
  private String enterpriseName;
  private String oldStock;
  private String oldStockIdcard;
  private String oldStockName;
  private String newStock;
  private String newStockIdcard;
  private String newStockName;
  private double amount;
  private double transAmount;
  private String putType;

  private Integer transType;


}
