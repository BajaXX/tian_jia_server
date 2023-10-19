package com.bemore.api.entity.request;

import com.spire.ms.System.Collections.ArrayList;
import lombok.Data;

import java.util.List;

@Data
public class TaxExportRequest {
    private String startDate;
    private String endDate;
    private String queryString;
    private List<ColumnModel> columns = new ArrayList();
}
