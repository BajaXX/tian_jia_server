package com.bemore.api.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageParam {
    @ApiModelProperty("页码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int page = 1;
    @ApiModelProperty("每页条数")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int size = 10;
}
