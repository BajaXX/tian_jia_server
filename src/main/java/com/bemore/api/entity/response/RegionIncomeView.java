package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionIncomeView {
    @ApiModelProperty(value="开发区", name="garden")
    private String garden;
    @ApiModelProperty(value="企业号", name="enterpriseNo")
    private String enterpriseNo;
    @ApiModelProperty(value="企业名", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="企业行业", name="industry")
    private String industry;
    @ApiModelProperty(value="公司成立日期", name="foundDate")
    private String foundDate;
    @ApiModelProperty(value="全口径合计（增值税+营改增+企业所得税+个人所得税+城建税+土地增值税+房产税+印花税+其他税）", name="threeTaxCombined")
    private String threeTaxCombined;
    @ApiModelProperty(value="增值税", name="addedTax")
    private String addedTax;
    @ApiModelProperty(value="区级增值税", name="districtAddedTax")
    private String districtAddedTax;
    @ApiModelProperty(value="营改增", name="toAddedTax")
    private String toAddedTax;
    @ApiModelProperty(value="区级营改增", name="districtToAddedTax")
    private String districtToAddedTax;
    @ApiModelProperty(value="所得税", name="incomeTax")
    private String incomeTax;
    @ApiModelProperty(value="区级所得税", name="districtIncomeTax")
    private String districtIncomeTax;
    @ApiModelProperty(value="个税", name="personTax")
    private String personTax;
    @ApiModelProperty(value="区级个税", name="districtPersonTax")
    private String districtPersonTax;
    @ApiModelProperty(value="城建税", name="cityTax")
    private String cityTax;
    @ApiModelProperty(value="区级城建税", name="districtCityTax")
    private String districtCityTax;
    @ApiModelProperty(value="土增税", name="addedLandTax")
    private String addedLandTax;
    @ApiModelProperty(value="区级土增税", name="districtAddedLandTax")
    private String districtAddedLandTax;
    @ApiModelProperty(value="房产税", name="houseTax")
    private String houseTax;
    @ApiModelProperty(value="区级房产税", name="districtHouseTax")
    private String districtHouseTax;
    @ApiModelProperty(value="印花税", name="stampTax")
    private String stampTax;
    @ApiModelProperty(value="区级印花税", name="districtStampTax")
    private String districtStampTax;
    @ApiModelProperty(value="区级收入合计", name="districtIncomeTotal")
    private String districtIncomeTotal;
    @ApiModelProperty(value="其他税（消费税、土地使用税、车船使用税）", name="otherTax")
    private String otherTax;
}
