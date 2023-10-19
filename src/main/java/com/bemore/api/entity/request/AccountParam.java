package com.bemore.api.entity.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountParam {
    @ApiModelProperty(value="用户昵称", name="userName")
    private String userName;
    @ApiModelProperty(value="账号", name="account")
    private String account;
    @ApiModelProperty(value="密码", name="pwd")
    private String pwd;
    @ApiModelProperty(value="角色：管理员-admin，工商-business，税务-tax", name="role")
    private String roleStr;
    @ApiModelProperty(value="部门", name="department")
    private String department;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    private String enterpriseName;
    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;
    @ApiModelProperty(value="第三方权限组ID", name="roleGroupId")
    private String roleGroupId;
}
