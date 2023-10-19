package com.bemore.api.entity.response;


import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@Data
public class PlatformsContractView {
    private String id;
    private String platformId;
    private long agreementStart;
    private long agreementEnd;
    private long taxStart;
    private long taxEnd;
    private long isFund;
    private double groupRate;
    private double incomeTaxRate;
    private double incomeTaxRateValue;
    private double personTaxRate;
    private double personTaxRateValue;
    private double addedTaxRate;
    private double addedTaxRateValue;
    private String platformName;
    private int cstatus;
    private int cancelDate;

}
