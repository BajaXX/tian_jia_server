package com.bemore.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class AddEnterpriseDetailExportDto {
    /**
     * 序号
     */
    private int no;
    /**
     * 企业编号
     */
    private String enterpriseNo;
    /**
     * 组织机构代码
     */
    private String registerNum;
    /**
     * 单位名称
     */
    private String name;
    /**
     * 招商来源
     */
    private String source;
    /**
     * 实际联系地址
     */

    private String registerAddress;

    /**
     * 所处流程(1新开，2迁入，3变更，4注销，5正常)
      */
    private Integer process;

    /**
     * 成立日期(营业执照开始日期)
     */

    private String startDate;
    /**
     * 建档时间
     */
    private Date buildDate;
    /**
     * 启缴时间
     */
    private Date seizeDate;

    /**
     * 本月纳税额
     */
    private BigDecimal curMonthTax;

    /**
     * 累计纳税额
     */
    private BigDecimal totalTax;
}
