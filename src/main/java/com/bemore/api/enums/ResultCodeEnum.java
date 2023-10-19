package com.bemore.api.enums;


/**
 * @author : jackie.yao
 * @date: 2021/3/4 10:43 PM
 */
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    SYSTEM_ERROR(500,"系统异常"),
    INVALID_PARAM(400,"参数不合法"),

    ;



    private Integer code;
    private String desc;

    ResultCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ResultCodeEnum getInstanceByDesc(String desc) {
        for (ResultCodeEnum codeEnum : ResultCodeEnum.values()) {
            if (codeEnum.toString().equals(desc)) {
                return codeEnum;
            }
        }
        return null;
    }
}
