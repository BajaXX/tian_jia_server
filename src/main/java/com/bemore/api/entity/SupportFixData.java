package com.bemore.api.entity;

import lombok.Data;

@Data
public class SupportFixData {
    private String companyName;
    private String fixMonth;
    private Double fixAmount;
}