package com.bemore.api.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 通用
 */
public class CommonConstants {

    public final static List<String> ALL_ENTERPRISE_TYPE = Arrays.asList(
            "|-内资公司",
            "--|-有限责任公司",
            "----|-有限责任公司(外商投资企业投资)",
            "------|-有限责任公司(外商投资企业合资)",
            "------|-有限责任公司(外商投资企业与内资合资)",
            "------|-有限责任公司(外商投资企业法人独资)",
            "----|-有限责任公司(自然人投资或控股)",
            "----|-一人有限责任公司",
            "------|-有限责任公司(自然人独资)",
            "------|-有限责任公司(法人独资)",
            "------|-有限责任公司(自然人投资或者控股的法人独资)",
            "------|-有限责任公司(非自然人投资或者控股的法人独资)",
            "----|-有限责任公司(法人独资)",
            "----|-有限责任公司(国内合资)",
            "----|-其他有限责任公司",
            "--|-股份有限责任公司",
            "----|-股份有限责任公司(非上市)",
            "------|-股份有限责任公司(非上市、外商投资企业投资)",
            "------|-股份有限责任公司(非上市、自然人投资企业投资)",
            "------|-股份有限责任公司(非上市、国有控股)",
            "------|-其他股份有限公司(非上市)",
            "|-内资分公司",
            "--|-有限责任分公司",
            "----|-有限责任分公司(国有独资)",
            "----|-有限责任分公司(外商投资企业投资)",
            "----|-有限责任分公司(外商投资企业合资)",
            "------|-有限责任分公司(外商投资企业与内资合资)",
            "----|-有限责任分公司(自然人投资或控股)",
            "----|-一人有限责任分公司",
            "------|-有限责任分公司(自然人独资)",
            "------|-有限责任分公司(法人独资)",
            "----|-其他有限责任分公司",
            "--|-股份有限公司分公司",
            "----|-股份有限公司分公司(上市)",
            "------|-股份有限公司分公司(上市、外商投资企业投资)",
            "------|-股份有限公司分公司(上市、自然人投资或控股)",
            "------|-股份有限公司分公司(上市、国有控股)",
            "------|-其他股份有限公司分公司(上市)",
            "----|-股份有限公司分公司(非上市)",
            "------|-股份有限公司分公司(非上市、外商投资企业投资)",
            "------|-股份有限公司分公司(非上市、自然人投资或控股)",
            "------|-股份有限公司分公司(非上市、国有控股)",
            "|-内资非法人企业，非公司私营企业及内资非公司企业分支机构",
            "--|-非公司私营企业",
            "----|-合伙企业",
            "------|-普通合伙企业",
            "------|-有限合伙企业",
            "----|-个人独资企业",
            "|-外商投资企业",
            "--|-非公司",
            "----|-非公司外商投资企业(中外合作)",
            "----|-非公司外商投资企业(外商合资)",
            "----|-其他",
            "|-外国(地区)企业",
            "--|-外国(地区)公司分支机构",
            "----|-外国(地区)无限责任公司分支机构",
            "|-集团",
            "|-其他类型",
            "--|-农民专业合作经济组织分支机构"

    );

    public static enum IS_PARTNER {
        /**
         * 否
         */
        NO(0),
        /**
         * 是
         */
        YES(1),
        ;
        private int code;

        IS_PARTNER(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static enum PARTNER_TYPE {
        /**
         * 普通合伙
         */
        COMMON(1),
        /**
         * 有限合伙
         */
        LIMITED(2),
        ;
        private int code;

        PARTNER_TYPE(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 企业注册地址状态
     */
    public enum AddrEnable {
        /**
         * 可用
         */
        ENABLE("可用"),
        /**
         * 已用
         */
        DISABLE("已用"),
        ;
        private String value;

        AddrEnable(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 企业状态流转
     */
    public enum COMPANY_PROCESS_STATUS {
        /**
         * 新开
         */
        NEW(1),
        /**
         * 变更
         */
        UPDATE(23),
        /**
         * 注销
         */
        CANCEL(4),

        /**
         * 已入驻
         */
        SETTLED(5);
        private int code;

        COMPANY_PROCESS_STATUS(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

    }

    public static enum YesOrNo {
        YES(1, "是"),
        NO(0, "否"),
        ;
        private int code;
        private String msg;

        YesOrNo(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static int SUPPORT_LEVEL = 10000;
}
