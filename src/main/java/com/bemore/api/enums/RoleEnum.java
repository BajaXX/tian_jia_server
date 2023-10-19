package com.bemore.api.enums;

public enum RoleEnum {
    管理员("admin"),
    普通员工("staff"),
    会议室管理员("meetingAdmin"),
    会议室审核员("meetingCheck"),
    企业用户("enterprise"),
    高级企业用户("enterpriseVip"),
    日用品入库员("depotImport"),
    日用品管理员("depot"),
    固定资产入库员("fixedAssetsImport"),
    固定资产管理员("fixedAssets");


    RoleEnum(String value) {
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
