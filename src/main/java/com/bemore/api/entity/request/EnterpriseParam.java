package com.bemore.api.entity.request;

import lombok.Data;

import java.time.LocalDate;
@Data
public class EnterpriseParam {
    // 企业名称
    private String name;
    // 入驻日期
    private String settledDate;
    // 机构类型
    private String institutionalType;
    // 投资类型
    private String investmentType;
    // 注册地址
    private String registerAddress;
    private String source;
    private String follower;
}
