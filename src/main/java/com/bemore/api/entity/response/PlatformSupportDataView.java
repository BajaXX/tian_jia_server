package com.bemore.api.entity.response;

import com.bemore.api.entity.EnterpriseSupportLog;
import lombok.Data;

import java.util.List;
@Data
public class PlatformSupportDataView {

    private String platformId;
    private String platformName;
    private double platformSupportAmount;
    private double platformMonthAmount;
    private double platformSurplus;
    private int date;

    private List<EnterpriseSupportLog> enterpriseSupportLogList;
}
