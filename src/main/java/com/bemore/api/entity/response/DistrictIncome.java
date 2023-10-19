package com.bemore.api.entity.response;

import com.bemore.api.annotation.ExcelColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DistrictIncome
 * @Description 区级收入
 * @Author Louis
 * @Date 2022/04/26 15:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictIncome {

    @ExcelColumn("区级增值税")
    @ApiModelProperty("区级增值税：32.5%")
    private Double districtAddedTax;

    @ExcelColumn("区级城建税")
    @ApiModelProperty("区级城建税：65%")
    private Double districtCityTax;

    @ExcelColumn("区级企业所得税")
    @ApiModelProperty("区级企业所得税：20%")
    private Double districtIncomeTax;

    @ExcelColumn("区级个人所得税")
    @ApiModelProperty("区级个人所得税：22%")
    private Double districtPersonTax;

    @ExcelColumn("区级房产税")
    @ApiModelProperty("区级房产税：80%")
    private Double districtHouseTax;

    @ExcelColumn("区级土地增值税")
    @ApiModelProperty("区级土地增值税：80%")
    private Double districtAddedLandTax;

    @ExcelColumn("区级印花税")
    @ApiModelProperty("区级印花税：100%")
    private Double districtStampTax;

    @ExcelColumn("区级收入合计")
    @ApiModelProperty("区级收入合计")
    private Double districtTotalTax;

}
