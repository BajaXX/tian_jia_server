package com.bemore.api.constant;

public class OCRConstants {

    public enum ID_CARD{
        FRONT(1,"FRONT"),BACK(0,"BACK");
        private int code;
        private String value;

        ID_CARD(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }
}
