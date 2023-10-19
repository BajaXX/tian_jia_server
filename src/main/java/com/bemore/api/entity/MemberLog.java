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
@Table(name = "t_enterprise_member_log")
@TableName("t_enterprise_member_log")
public class MemberLog {

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
	// 是否新股东
	private Integer isStock;
	// 是否新监事
	private Integer isSupervisor;
	// 是否新董事
	private Integer isDirector;
	// 是否老股东
	private Integer isOldStock;
	// 是否老监事
	private Integer isOldSupervisor;
	// 是否老董事
	private Integer isOldDirector;	
	// 原有出资方式
	private String oldPutType;
	// 出资方式
	private String putType;
	// 原有出资额
	private String putAmount;
	// 当前出资额
	private String oldPutAmount;
	// 实际出资额
	private String realPutAmount;
	// 出资比例
	private String putRate;
	// 原出资日期
	private String putDate;
	// 出资日期
	private String oldPutDate;
	// 实际出资日期
	private String realPutDate;
	// 监事选举方式
	private String supervisorType;
	// 董事选举方式
	private String directorType;

	// 担任职务
	private String holdPost;
	/**
	 * 合伙性质 1普通合伙 2有限合伙
	 */
	private Integer partnerType;

	private String memberType;
	
	private String valid;
	private String createTime;
	
}
