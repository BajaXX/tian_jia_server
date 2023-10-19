package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName EnterpriseComprehensiveExcelView
 * @Description 企业综合报表Excel解析
 * @Author Louis
 * @Date 2022/04/21 17:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseComprehensiveExcelView {

    @ApiModelProperty(value="导入总数", name="count")
    private Integer count;
    @ApiModelProperty(value="查询失败的企业列表")
    private List<String> failedList;
    @ApiModelProperty(value="查询成功的企业列表")
    private List<String> list;

}
