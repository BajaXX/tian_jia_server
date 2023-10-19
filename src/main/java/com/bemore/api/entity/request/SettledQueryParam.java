package com.bemore.api.entity.request;

import lombok.Data;

@Data
public class SettledQueryParam extends PageParam {
    private String queryString;
}
