package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EnterpriseSettledView {
    private String id;
    @ApiModelProperty(value="楼栋号", name="buildingNo")
    private String buildingNo;
    @ApiModelProperty(value="室号", name="roomNo")
    private String roomNo;
    @ApiModelProperty(value="面积（平方米）", name="enterpriseName")
    private String area;
    @ApiModelProperty(value="面积补充说明如：净288*公摊122", name="enterpriseName")
    private String areaExplain;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="企业管理（园区/孵化器）", name="enterpriseName")
    private String enterpriseManage;
    @ApiModelProperty(value="实际目前办公人数", name="enterpriseName")
    private Integer staff;
    @ApiModelProperty(value="入园凭证", name="enterpriseName")
    private String settledProof;
    @ApiModelProperty(value="是否签订入驻合同： 是/否", name="enterpriseName")
    private String isContract;
    @ApiModelProperty(value="合同签订周期", name="enterpriseName")
    private String contractDuration;
    @ApiModelProperty(value="租赁费（x元/平方米/天）", name="enterpriseName")
    private String rental;
    @ApiModelProperty(value="免租期", name="enterpriseName")
    private String rentHoliday;
    @ApiModelProperty(value="物业费（x元/平方米/天）", name="enterpriseName")
    private String propertyFee;
    @ApiModelProperty(value="押金", name="enterpriseName")
    private String deposit;
    @ApiModelProperty(value="是否配备家具：是/否", name="enterpriseName")
    private String furniture;
    @ApiModelProperty(value="家具清单是否移交企业", name="enterpriseName")
    private String furnitureHandOver;
    @ApiModelProperty(value="移交时间", name="enterpriseName")
    private String handOverTime;
    @ApiModelProperty(value="门禁卡数量（张）", name="enterpriseName")
    private String accessCardNumber;
    @ApiModelProperty(value="车牌录入数量（辆）", name="enterpriseName")
    private String licenseNumber;
    @ApiModelProperty(value="企业现场负责人", name="enterpriseName")
    private String siteManager;
    @ApiModelProperty(value="联系方式", name="enterpriseName")
    private String phone;
    @ApiModelProperty(value="备注", name="enterpriseName")
    private String remark;
}
