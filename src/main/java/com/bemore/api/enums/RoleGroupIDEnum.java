package com.bemore.api.enums;

public enum RoleGroupIDEnum {
    管理员("J12115"),
    普通员工("J13944"),
    企业用户("J14075"),
    高级企业用户("J14076"),
    会议室管理员("J13944"),
    会议室审核员("J13944"),
    日用品入库员("J13944"),
    日用品管理员("J13944"),
    固定资产入库员("J13944"),
    固定资产管理员("J13944");

    RoleGroupIDEnum(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
