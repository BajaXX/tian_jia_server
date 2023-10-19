package com.bemore.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BusinessInfoDto {

    /**
     * 工商状态
     */
    private Integer process;

    /**
     * 从事行业
     */
    private String belongIndustry;
    /**
     * 成立日期
     */
    private Date startDateStart;
    private Date startDateEnd;
    /**
     * 经营范围
     */
    private String business;

    /**
     * 工商吊销日期开始
     */
    private Date endDateEnd;
    private Date endDateStart;

    /**
     * 母公司
     */
    private String motherCompany;
}
