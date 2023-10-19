package com.bemore.api.controller;

import com.bemore.api.service.EnterpriseService;
import com.bemore.api.util.GsonUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName EnterpriseController
 * @Description 企业信息控制层
 * @Author Louis
 * @Date 2022/04/24 23:27
 */
@RestController
@RequestMapping("/enterprise")
public class EnterpriseController extends BaseController {

    @Autowired private EnterpriseService enterpriseService;

    /**
     * @Description 根据Excel更新企业信息的几个新增字段，详见：Coding-Bugs-176
     * @Title updateByExcel
     * @Param [file]
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/04/24 23:57
     */
    @ApiOperation(value = "根据Excel更新企业信息")
    @PostMapping("/updateByExcel")
    public String updateByExcel(@ApiParam("Excel文件") @RequestPart MultipartFile file) throws IOException {
        enterpriseService.updateByExcel(file);
        return GsonUtil.build();
    }

}
