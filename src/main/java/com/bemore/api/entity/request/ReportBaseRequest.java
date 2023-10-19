package com.bemore.api.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportBaseRequest extends PageParam {
    // 日期格式 2021-04
    private String startDate;
    private String endDate;
    private String queryDate;
    // 年 2021
    private Integer year;
    private Integer month;
    // 月 4
    private Integer startMonth;
    private Integer endMonth;
    // 排名
    private Integer ranking;
    // 行业
    private String industry;
    // 经济性质
    private String economy;
    // 项目类型
    private String projectType;
    // 排序字段
    private String sort;
    private Sort.Direction direction;
    private int monthOffset;
    // 当月 累计
    private String scope;
    //单位w
    private int taxIndices;
    private String queryString;
}
