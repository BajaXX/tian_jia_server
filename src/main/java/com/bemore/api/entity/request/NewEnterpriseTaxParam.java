package com.bemore.api.entity.request;

import lombok.Data;

@Data
public class NewEnterpriseTaxParam extends ReportBaseRequest {

    // 税收开始年份
    private Integer taxYear;
    private Integer taxStartMonth;
    private Integer taxEndMonth;
    // 公司成立年份
    private Integer foundYear;
    private Integer foundStartMonth;
    private Integer foundEndMonth;
}
