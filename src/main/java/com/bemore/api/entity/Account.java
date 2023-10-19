package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@TableName(value = "t_account")
@Data
@ApiModel(value = "用户表")
public class Account {
    @Id
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value="用户昵称", name="userName")
    private String userName;
    @ApiModelProperty(value="账号", name="account")
    private String account;
    @ApiModelProperty(value="密码", name="pwd")
    private String pwd;
    @ApiModelProperty(value="角色：管理员-admin，工商-business，税务-tax", name="role")
    private String role;
    @ApiModelProperty(value="角色", name="roleStr")
    private String roleStr;
    @ApiModelProperty(value="部门", name="department")
    private String department;
    @ApiModelProperty(value="状态", name="status")
    private String status;
    @ApiModelProperty(value="创建时间", name="createTime")
    private String createTime;
    @ApiModelProperty(value="企业名称", name="enterpriseName")
    @Column(name="enterprise_name")
    private String enterpriseName;
    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;
    @ApiModelProperty(value="第三方权限组ID", name="roleGroupId")
    private String roleGroupId;
    @ApiModelProperty(value="CP系统用户ID2", name="thirdUserId1")
    private String thirdUserId1;
    @ApiModelProperty(value="CP系统用户ID2", name="thirdUserId2")
    private String thirdUserId2;
    @ApiModelProperty(value="预警通知email", name="noticeEmail")
    private String noticeEmail;
    @ApiModelProperty(value="是否开启预警", name="openNotice")
    private Integer openNotice;

}
