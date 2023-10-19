package com.bemore.api.entity.response;

import com.bemore.api.entity.EnterpriseTaxPlus;
import com.bemore.api.entity.request.EnterpriseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EnterpriseTaxExcelView {
    @ApiModelProperty(value="导入总数", name="count")
    private Integer count;
    @ApiModelProperty(value="查询失败的企业列表", name="count")
    private List<EnterpriseParam> failedList;
    @ApiModelProperty(value="查询成功的企业税收数据", name="count")
    private List<EnterpriseTaxPlus> taxList;
}
