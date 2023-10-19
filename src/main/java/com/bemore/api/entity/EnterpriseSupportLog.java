package com.bemore.api.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_enterprise_support_log")
public class EnterpriseSupportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String bankAccount;
    private String depositBank;
    private int date;
    private String enterpriseName;
    private String garden;
    private double monthAmount;
    private double supportAmount;
    private double surplus;
    private double platformSupportAmount;
    private double platformMonthAmount;
    private double platformSurplus;
    private int year;
    private int status;
    private String platformId;
    private String platformName;
    private String platformContractId;
    private String contractId;
    private String supportContractId;
    private String supportId;

}
