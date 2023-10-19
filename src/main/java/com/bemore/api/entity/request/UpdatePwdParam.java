package com.bemore.api.entity.request;

import lombok.Data;

@Data
public class UpdatePwdParam {
    private String id;
    private String pwd;
    private String newPwd;
}
