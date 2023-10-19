package com.bemore.api.entity;

import javax.persistence.*;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bemore.api.annotation.ExcelColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_enterprise_person")
@TableName("t_enterprise_person")
public class Person {

	@Id
	@GeneratedValue(generator="idGenerator")
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@Column(length = 36)
	private String id;

	@ApiModelProperty("企业ID")
	private String enterpriseId;

	@ApiModelProperty("姓名")
	@ExcelColumn("姓名")
	private String name;

	@ApiModelProperty("性别")
	@ExcelColumn("性别")
	private String sex;

	@ApiModelProperty("国籍")
	@ExcelColumn("国籍")
	private String country;

	@ApiModelProperty("民族")
	@ExcelColumn("民族")
	private String nation;

	@ApiModelProperty("证件名称")
	@ExcelColumn("证件名称")
	private String type;

	@ApiModelProperty("证件号")
	@ExcelColumn("证件号")
	private String idcard;

	@ApiModelProperty("出生年月")
	@ExcelColumn("出生年月")
	private String birthday;

	@ApiModelProperty("发证机关")
	@ExcelColumn("发证机关")
	private String authority;

	@ApiModelProperty("证件开始有效期")
	@ExcelColumn("证件开始有效期")
	private String startDate;

	@ApiModelProperty("证件结束有效期")
	@ExcelColumn("证件结束有效期")
	private String endDate;

	@ApiModelProperty("办公室电话")
	@ExcelColumn("办公室电话")
	private String officePhone;

	@ApiModelProperty("办公室传真")
	@ExcelColumn("办公室传真")
	private String officeFax;

	@ApiModelProperty("办公室地址")
	@ExcelColumn("办公室地址")
	private String officeAddress;

	@ApiModelProperty("办公室邮编")
	@ExcelColumn("办公室邮编")
	private String officeZipcode;

	@ApiModelProperty("移动电话")
	@ExcelColumn("移动电话")
	private String mobile;

	@ApiModelProperty("住宅电话")
	@ExcelColumn("住宅电话")
	private String phone;

	@ApiModelProperty("住宅地址")
	@ExcelColumn("住宅地址")
	private String address;

	@ApiModelProperty("住宅邮编")
	@ExcelColumn("住宅邮编")
	private String zipcode;

	@ApiModelProperty("电子邮件")
	@ExcelColumn("电子邮件")
	private String email;

	@ApiModelProperty("备注")
	@ExcelColumn("备注")
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

}
