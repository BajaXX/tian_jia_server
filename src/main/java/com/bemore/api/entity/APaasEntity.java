package com.bemore.api.entity;

import lombok.Data;

/**
 * @author songjunchao
 */
@Data
public class APaasEntity {
    /**
     * 返回状态
     */
    private int code;
    /**
     * 返回信息
     */
    private String message;
    /**
     * 返回流水号
     */
    private String requestId;
    /**
     * 返回数据
     */
    private Object data;
}
