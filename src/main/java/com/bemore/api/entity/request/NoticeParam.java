package com.bemore.api.entity.request;

import com.bemore.api.entity.NoticeFiles;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NoticeParam {

    @ApiModelProperty(value="id", name="id")
    private String id;
    @ApiModelProperty(value="标题", name="title")
    private String title;
    @ApiModelProperty(value="内容", name="content")
    private String content;
    @ApiModelProperty(value="是否已读", name="mstatus")
    private Integer mstatus;
    @ApiModelProperty(value="发送企业", name="targetEnterprise")
    private List<String> targetEnterprise;
    @ApiModelProperty(value="发送时间", name="issueTime")
    private long issueTime;
    @ApiModelProperty(value="附件", name="fileList")
    private List<NoticeFiles> fileList;

    @ApiModelProperty(value="签名", name="sign")
    private String sign;

    @ApiModelProperty(value="时间戳", name="t")
    private Long t;
}
