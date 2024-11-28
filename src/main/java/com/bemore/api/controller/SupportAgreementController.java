package com.bemore.api.controller;

import com.bemore.api.dto.req.SupportAgreementReq;
import com.bemore.api.dto.req.SupportFixDataReq;
import com.bemore.api.entity.request.SettledQueryParam;
import com.bemore.api.entity.request.SupportAgreementParam;
import com.bemore.api.service.SupportAgreementService;
import com.bemore.api.util.GsonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support")
public class SupportAgreementController {

    private final SupportAgreementService supportAgreementService;

    public SupportAgreementController(SupportAgreementService supportAgreementService) {
        this.supportAgreementService = supportAgreementService;
    }

    @PostMapping("/recalculate")
    @ApiOperation(value = "重新计算扶持协议")
    public void recalculateSupportAgreement(@RequestBody SupportAgreementReq supportAgreementReq) {
        supportAgreementService.recomputeContract(supportAgreementReq);
    }

    @GetMapping("/getOne/{id}")
    @ApiOperation(value = "查询单个企业扶持协议")
    public String getSupportAgreement(@PathVariable("id") String id) {
        return GsonUtil.build(supportAgreementService.findSupportAgreement(id));
    }

    @GetMapping("/getAgreementList")
    @ApiOperation(value = "查询企业扶持协议列表（分页）")
    public String getAgreementListByPage(SettledQueryParam param) {
        return GsonUtil.build(supportAgreementService.findAgreementListByPage(param));
    }

    @DeleteMapping("/deleteOne/{id}")
    @ApiOperation(value = "删除企业扶持协议")
    public String deleteSupportAgreement(@PathVariable("id") String id) {
        supportAgreementService.deleteOneSupportAgreement(id);
        return GsonUtil.build("success");
    }

    @PostMapping("/modifyAgreement")
    @ApiOperation(value = "修改企业扶持协议")
    public String modifySupportAgreement(@RequestBody SupportAgreementParam param) {
        supportAgreementService.updateSupportAgreement(param);
        return GsonUtil.build("success");
    }

    @PostMapping("/addAgreement")
    @ApiOperation(value = "添加企业扶持协议")
    public String addSupportAgreement(@RequestBody SupportAgreementParam param) {
        supportAgreementService.saveSupportAgreement(param);
        return GsonUtil.build("success");
    }

    @PostMapping("/fixSupportData")
    @ApiOperation(value = "修正扶持数据")
    public String fixSupportData(@RequestBody SupportFixDataReq req) {
        supportAgreementService.fixSupportData(req);
        return GsonUtil.build("success");
    }

}
