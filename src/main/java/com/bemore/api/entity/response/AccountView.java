package com.bemore.api.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountView {
    private String id;
    @ApiModelProperty(value="用户昵称", name="userName")
    private String userName;
    @ApiModelProperty(value="账号", name="account")
    private String account;
    @ApiModelProperty(value="角色", name="roleStr")
    private String roleStr;
    @ApiModelProperty(value="部门", name="department")
    private String department;
    @ApiModelProperty(value="状态", name="status")
    private String status;
    @ApiModelProperty(value="创建时间", name="createTime")
    private String createTime;
}
