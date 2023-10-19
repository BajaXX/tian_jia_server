package com.bemore.api.dto.req;

import lombok.Data;

/**
 * 下载企业文件Req
 */
@Data
public class downDocReq {

    private String filePath;
    private String enterpriseId;
}
