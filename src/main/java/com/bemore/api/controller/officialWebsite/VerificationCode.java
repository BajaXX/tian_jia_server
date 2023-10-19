package com.bemore.api.controller.officialWebsite;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(value = "verification_code")
@Data
@ApiModel(value = "验证码")
public class VerificationCode {
    @TableId
    private String mobile;
    @ApiModelProperty(value="验证码", name="code")
    private String code;
}
