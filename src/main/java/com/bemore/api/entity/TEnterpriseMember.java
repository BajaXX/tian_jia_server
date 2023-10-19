package com.bemore.api.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * t_enterprise_member
 * @author 
 */
@Data
public class TEnterpriseMember implements Serializable {
    private String id;

    private String enterpriseId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 国籍
     */
    private String country;

    /**
     * 民族
     */
    private String nation;

    /**
     * 证件类型
     */
    private String type;

    private String idcard;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 发证机关
     */
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

    /**
     * 正面照片
     */
    private String front;

    /**
     * 背面照片
     */
    private String back;

    /**
     * 0非，1是
     */
    private Byte isStock;

    /**
     * 0非，1是
     */
    private Byte isOldStock;

    private Byte isSupervisor;

    private Byte isOldSupervisor;

    private Byte isDirector;

    private Byte isOldDirector;

    private String oldPutType;

    private String putType;

    private String oldPutAmount;

    private String putAmount;

    private String realPutAmount;

    private String putDate;

    private String oldPutDate;

    private String realPutDate;

    private String supervisorType;

    private String directorType;

    private static final long serialVersionUID = 1L;
}