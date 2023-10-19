package com.bemore.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class TaxInfoDto {
    /**
     * 税收汇总开始
     */
    private Date taxTotalStart;
    private Date taxTotalEnd;

    /**
     * 税金合计小于
     */
    private BigDecimal lessTaxTotal;

    /**
     * 税金合计大于
     */
    private BigDecimal thanTaxTotal;
}
