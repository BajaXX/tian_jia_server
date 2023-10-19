package com.bemore.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author yaobo
 * @version 1.0.0
 * @date 2021/3/4 11:27 PM
 * @description
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "t_enterprise_project")
public class ProjectLog {

    @Id
    @GeneratedValue(generator="idGenerator")
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @Column(length = 36)
    private String id;
    private String enterpriseId;
    /** 材料日期 */
    private String materialDate;
    /** 承房出租人 */
    private String lessor;
    /** 房出租人证件号 */
    private String lessorCredentials;
    /** 承房年租金 */
    private BigDecimal houseYearRental;
    /** 行政区域 */
    private String houseDistrict;
    /** 行政区域码 */
    private String houseDistrictCode;
    /** 合伙协议 */
    private String cooperationAgreement;
    /** 刊登报纸 */
    private String newspaperPublishing;
    /** 刊登日期 */
    private String publishingDate;
    /** 备案号 */
    private String recordNumber;
    /** 文件号 */
    private String docBatchNumber;
    /** 企业章程 */
    private String enterpriseArticle;
    /** 是否补偿 */
    private String isCompensate;
    /** 工商变更 */
    private String isBusinessChange;
    /** 收件凭据文号 */
    private String proofNumber;
    /** 章程修正案 */
    private String constitutionAmendment;
    /** 租赁面积 */
    private Float leaseArea;
    /** 租赁用途 */
    private String leasePurpose;
    /** 年租金 */
    private BigDecimal yearRental;
    /** 刻章委托人 */
    private String consignee;
    /** 约谈时间 */
    private String interviewDate;
    /** 查名日期 */
    private String nameQueryDate;
    /** 注销公示日期 */
    private String cancellationDate;
    /** 是否有效 */
    private String valid;
    /** 创建时间 */
    private String createTime;

}
