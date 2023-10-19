package com.bemore.api.controller.officialWebsite;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "official_website_user")
@Data
@ApiModel(value = "官网用户表")
public class OfficialWebsiteUser {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value="注册手机号", name="mobile")
    private String mobile;
    @ApiModelProperty(value="用户昵称", name="userName")
    private String userName;
    @ApiModelProperty(value="密码", name="pwd")
    private String pwd;
    @ApiModelProperty(value="公司", name="EnterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="状态", name="status")
    private String status;
    @ApiModelProperty(value="创建时间", name="createTime")
    private String createTime;
}
