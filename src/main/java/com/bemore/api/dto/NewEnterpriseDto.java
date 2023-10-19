package com.bemore.api.dto;

import com.bemore.api.entity.Enterprise;
import lombok.Data;

/**
 * 新开企业
 * @author 余江俊
 */
@Data
public class NewEnterpriseDto extends Enterprise {
    /**
     * 企业名称
     */
    private String name;
    /**
     * 企业类型
     */
    private String type;
}
