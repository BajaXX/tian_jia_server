package com.bemore.api.dto;

import lombok.Data;

@Data
public class EnterpriseQueryDto {

    private BasicInfoDto basic;
    private BusinessInfoDto business;
    private TaxInfoDto tax;

    private int page = 1;
    private int limit = 15;
    private int offset = 0;
}
