package com.bemore.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SupportMonthDto {

    private String supportAreas;
    private String supportProject;
    private double totalAmount;
}
