package com.bemore.api.enums;

public enum BylawsEnum {
    A1("不设董事会不设监事会（分期）"),
    A2("不设董事会不设监事会（一次性）"),
    A3("不设董事会设监事会（分期）"),
    A4("不设董事会设监事会（一次性）"),
    A5("设董事会不设监事会（分期）"),
    A6("设董事会不设监事会（一次性）"),
    A7("设董事会设监事会（分期）"),
    A8("设董事会设监事会（一次性）"),
    A9("一人公司不设董事会不设监事会"),
    A10("一人公司不设董事会不设监事会（分期）"),
    A11("一人公司不设董事会设监事会"),
    A12("一人公司设董事会不设监事会"),
    A13("一人公司设董事会设监事会"),
    A14("一人公司设董事会设监事会（分期）"),
    A15("章程修正案");

    BylawsEnum(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getType(String value) {
        BylawsEnum[] bylawsEnums = values();
        for (BylawsEnum bylawsEnum : bylawsEnums) {
            if (bylawsEnum.getValue().equals(value)) {
                return bylawsEnum.name();
            }
        }
        return null;
    }
}
