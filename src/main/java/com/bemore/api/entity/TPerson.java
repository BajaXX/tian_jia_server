package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "t_person")
@TableName("t_person")
@Data
public class TPerson {
	@Id
	@GeneratedValue(generator = "idGenerator", strategy = GenerationType.AUTO)
	private Integer id;
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

}
