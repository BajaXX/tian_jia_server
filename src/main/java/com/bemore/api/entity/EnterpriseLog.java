package com.bemore.api.entity;

import javax.persistence.*;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bemore.api.annotation.ExcelColumn;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@Table(name = "t_enterprise_log")
@TableName("t_enterprise_log")
public class EnterpriseLog {

//	@Id
//	@GeneratedValue(generator="idGenerator")
//	@GenericGenerator(name="idGenerator", strategy="uuid")
//	@Column(length = 36)
//	private String id;
//	// 所处流程(1新开，2迁入，3变更，4注销，5正常)
//	private Integer process;
//	// 园区名字
//	private String garden;
//
//	// 三合一
//	private String registerNum;
//	// 企业编号
//	private String enterpriseNo;
//	// 企业名称
//	private String name;
//	// 企业字号
//	private String enterpriseWordNo;
//	// 执照编号
//	private String paperNo;
//	// 成立日期(营业执照开始日期)
//	private String startDate;
//	// 企业类型
//	private String type;
//	// 从事行业
//	private String belongIndustry;
//	// 工商行业
//	private String industry;
//	// 工商行业代码
//	private String industryCode;
//	// 基金备案号
//	private String introducer;
//	// 招商来源
//	private String source;
//	// 母公司名称
//	private String motherCompany;
//	// 注册资本币种
//	private String currency;
//	// 跟踪员
//	private String follower;
//	// 注册资本
//	private String capital;
//	// 实收资本
//	private String realCapital;
//	// 注册地址
//	private String registerAddress;
//	// 邮编
//	private String zipcode;
//	// 不约定期限(1不约定，0约定)
//	private String noLimit;
//	// 营业执照结束日期
//	private String endDate;
//	// 经营范围
//	private String business;
//
//	// 营业执照文件名
//	private String paperName;
//
	private String enterpriseId;
	private String createTime;

	@Id
	@GeneratedValue(generator="idGenerator")
	@GenericGenerator(name="idGenerator", strategy="uuid")
	private String id;

	// 园区名字
	@ApiModelProperty("园区名称")
	@ExcelColumn("园区名称")
	private String garden;

	// 三合一
	@ApiModelProperty("注册编号")
	@ExcelColumn("注册编号")
	private String registerNum;

	// 企业编号
	@ApiModelProperty("企业编号")
	@ExcelColumn("企业编号")
	private String enterpriseNo;

	// 企业名称
	@ApiModelProperty("企业名称")
	private String name;

	// 企业字号
	@ApiModelProperty("企业字号")
	@ExcelColumn("企业字号")
	private String enterpriseWordNo;

	// 执照编号
	@ApiModelProperty("执照编号")
	@ExcelColumn("执照编号")
	private String paperNo;

	@ApiModelProperty("成立日期(营业执照开始日期)")
	@ExcelColumn("成立日期")
	private String startDate;

	// 成立日期(营业执照开始日期) 日期格式,用于查询
	private LocalDate startTimez;

	// 入驻日期
	@ApiModelProperty("入驻日期：年月日 格式，默认为空 输入时间不得早于2020年1月1日")
	@ExcelColumn("入驻日期")
	private String settledDate;

	// 企业类型
	@ApiModelProperty("企业类型")
	@ExcelColumn("企业类型")
	private String type;

	// 机构类型
	@ApiModelProperty("机构类型")
	@ExcelColumn("机构类型")
	private String institutionalType;

	// 投资类型
	@ApiModelProperty("投资类型")
	@ExcelColumn("投资类型")
	private String investmentType;

	// 从事行业
	@ApiModelProperty("从事行业")
	@ExcelColumn("从事行业")
	private String belongIndustry;

	// 工商行业
	@ApiModelProperty("工商行业")
	@ExcelColumn("工商行业")
	private String industry;

	// 工商行业代码
	@ApiModelProperty("工商行业代码")
	@ExcelColumn("工商行业代码")
	private String industryCode;

	// 基金备案号
	@ApiModelProperty("基金备案号")
	@ExcelColumn("基金备案号")
	private String introducer;

	// 招商来源
	@ApiModelProperty("招商来源")
	@ExcelColumn("招商来源")
	private String source;

	// 母公司名称
	@ApiModelProperty("母公司名称")
	@ExcelColumn("母公司名称")
	private String motherCompany;

	// 注册资本币种
	@ApiModelProperty("注册资本币种")
	@ExcelColumn("注册资本币种")
	private String currency;

	// 跟踪员
	@ApiModelProperty("跟踪员")
	@ExcelColumn("跟踪员")
	private String follower;

	// 注册资本
	@ApiModelProperty("注册资本")
	@ExcelColumn("注册资本")
	private String capital;

	// 实收资本
	@ApiModelProperty("实收资本")
	@ExcelColumn("实收资本")
	private String realCapital;

	// 注册地址
	@ApiModelProperty("注册地址")
	@ExcelColumn("注册地址")
	private String registerAddress;

	// 邮编
	@ApiModelProperty("邮编")
	@ExcelColumn("邮编")
	private String zipcode;

	// 不约定期限(1不约定，0约定)
	@ApiModelProperty("不约定期限(1不约定，0约定)")
	@ExcelColumn("不约定期限")
	private String noLimit;

	// 营业执照结束日期
	@ApiModelProperty("营业执照结束日期")
	@ExcelColumn("营业执照结束日期")
	private String endDate;

	// 经营范围
	@ApiModelProperty("经营范围")
	@ExcelColumn("经营范围")
	private String business;

	// 营业执照文件名
	@ApiModelProperty("营业执照文件名")
	@ExcelColumn("营业执照文件名")
	private String paperName;

	// 联系电话
	@ApiModelProperty("联系电话")
	@ExcelColumn("联系电话")
	private String contactPhone;

	// 实际联系地址
	@ApiModelProperty("实际联系地址")
	@ExcelColumn("实际联系地址")
	private String actContactAddress;

	// 实际经营本区 是，否
	@ApiModelProperty("实际经营本区")
	@ExcelColumn("实际经营本区")
	private String thisRegion;

	// 是否合伙 1是 0否
	@ApiModelProperty("是否合伙")
	@ExcelColumn("是否合伙")
	private Integer isPartner;

	// 是否重点企业
	@ApiModelProperty("是否重点企业")
	@ExcelColumn("是否重点企业")
	private Integer keyEnterprise;

	// 起缴日期
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	@ApiModelProperty("起缴日期")
	@ExcelColumn("起缴日期")
	private LocalDate paymentDate;

	// 所处流程(1新开，2迁入，3变更，4注销，5正常)
	@ApiModelProperty("所处流程(1新开，2迁入，3变更，4注销，5正常)")
	@ExcelColumn("所处流程")
	private Integer process = 5;

	@ApiModelProperty("基金管理规模或基金规模（亿元）：默认为空，空值用正斜杠表示")
	@ExcelColumn("基金管理规模或基金规模")
	private String fundManagementScale;

	@ApiModelProperty("迁入/新设：默认为空，不加额外字符表示。输入时只能在以下内容中选择“新设、本区迁入、外区迁入、外省市迁入”")
	@ExcelColumn("迁入/新设")
	private String moveType;

	@ApiModelProperty("是否自主招商：默认为空，输入时只能选择“是”或者“否”")
	@ExcelColumn("是否自主招商")
	private String beIndependentInvestmentPromotion;

	@ApiModelProperty("指定联系人")
	@ExcelColumn("指定联系人")
	private String designatedContact;

	@ApiModelProperty("指定联系人电话")
	@ExcelColumn("指定联系人电话")
	private String designatedContactPhone;

	// 备注
	@ApiModelProperty("备注")
	@ExcelColumn("备注")
	private String remake;

	@ApiModelProperty("行业大类")
	@ExcelColumn("行业大类")
	private String industryMainClass;

	@ApiModelProperty("行业小类")
	@ExcelColumn("行业小类")
	private String industryChildClass;

	@ApiModelProperty("经济性质")
	@ExcelColumn("经济性质")
	private String economicNature;

	private String valid;
	
}
