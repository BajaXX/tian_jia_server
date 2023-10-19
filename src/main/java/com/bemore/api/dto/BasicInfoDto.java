package com.bemore.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BasicInfoDto {

    private String enterpriseNo;
    /**
     * 企业名称
     */
    private String name;
    /**
     * 三证合一
     */
    private String registerNum;
    /**
     * 企业字号
     */
    private String enterpriseWordNo;

    /**
     * 注册地址
     */
    private String registerAddress;

    /**
     * 实际联系地址
     */
    private String actContactAddress;

    /**
     * 招商来源
     */
    private String source;

    /**
     * 基金备案号
     */
    private String introducer;

    /**
     * 是否实际经营本区
     */
    private String thisRegion;

    /**
     * 财务姓名
     */
    private String financeName;
    /**
     * 财务电话
     */
    private String financePhone;

    /**
     * 法人姓名
     */
    private String masterName;
    /**
     * 法人电话
     */
    private String masterPhone;
    /**
     * 法人地址
     */
    private String masterAddress;

    /**
     * 法人身份证
     */
    private String masterIdCard;

    /**
     * 跟踪员
     */
    private String follower;


    /**
     * 入驻时间
     */
    private Date settledDateStart;
    private Date settledDateEnd;

}
