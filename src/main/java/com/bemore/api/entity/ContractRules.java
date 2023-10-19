
package com.bemore.api.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "t_contract_rules")
public class ContractRules {
  @Id
  @GeneratedValue(generator = "idGenerator")
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  private String id;
  private String contractId;
  private long taxStart;
  private long taxEnd;
  private double groupRate;
  private double incomeTaxRate;
  private double incomeTaxRateValue;
  private double personTaxRate;
  private double personTaxRateValue;
  private double addedTaxRate;
  private double addedTaxRateValue;

}
