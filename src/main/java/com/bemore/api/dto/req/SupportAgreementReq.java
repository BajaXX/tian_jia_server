package com.bemore.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SupportAgreementReq {
    @NotBlank(message = "开始时间不能为空")
    private String startDate;
    @NotBlank(message = "结束时间不能为空")
    private String endDate;
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    private String contractId;
}
