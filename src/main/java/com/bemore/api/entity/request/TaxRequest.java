package com.bemore.api.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxRequest extends PageParam {
    private String queryString;
    // 日期格式 2021-04
    private String startDate;
    private String endDate;
    private String queryDate;

    // 行业
    private String industry;
    // 经济性质
    private String economy;
    // 年 2021
    private Integer year;
    // 月 4
    private Integer startMonth;
    private Integer endMonth;

    private MultipartFile file;
}
