package com.bemore.api.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("扶持数据修正请求")
public class SupportFixDataReq {
    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("修正年月 格式：202301")
    private String fixMonth;

    @ApiModelProperty("修正后的扶持金额")
    private Double fixAmount;
}