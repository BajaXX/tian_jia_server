package com.bemore.api.controller.officialWebsite;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterParam {
    @ApiModelProperty(value="注册手机号", name="mobile")
    private String mobile;
    @ApiModelProperty(value="用户昵称", name="userName")
    private String userName;
    @ApiModelProperty(value="密码", name="pwd")
    private String pwd;
    @ApiModelProperty(value="公司", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="验证码", name="code")
    private String code;
}
