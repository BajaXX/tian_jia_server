package com.bemore.api.entity;

import javax.persistence.*;

import com.baomidou.mybatisplus.annotation.TableName;
import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "t_enterprise_person_log")
@TableName("t_enterprise_person_log")
public class PersonLog {

	@Id
	@GeneratedValue(generator="idGenerator")
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@Column(length = 36)
	private String id;
	private String enterpriseId;	
	private String name;
	private String sex;
	private String country;
	private String nation;
	private String type;
	private String idcard;
	private String birthday;
	private String authority;	
	private String startDate;
	private String endDate;
	private String officePhone;
	private String officeFax;	
	private String officeAddress;
	private String officeZipcode;
	private String mobile;
	private String phone;	
	private String address;
	private String zipcode;
	private String email;
	private String memo;	
	// 证件正负面
	private String front;
	private String back;
	// 法人
	private Integer isMaster;
	// 联系人
	private Integer isContact;
	// 财务
	private Integer isFinance;
	// 发票购买人
	private Integer isTicket;
	// 办税员
	private Integer isTax;
	// 原法人
	private Integer isOldMaster;
	// 原联系人
	private Integer isOldContact;
	// 原财务
	private Integer isOldFinance;
	// 原发票购买人
	private Integer isOldTicket;	
	// 原办税员
	private Integer isOldTax;
	
	private String valid;
	private String createTime;
	
}
