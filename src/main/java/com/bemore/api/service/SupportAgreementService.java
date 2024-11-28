package com.bemore.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bemore.api.dto.req.SupportAgreementReq;
import com.bemore.api.dto.req.SupportFixDataReq;
import com.bemore.api.entity.SupportAgreement;
import com.bemore.api.entity.request.SettledQueryParam;
import com.bemore.api.entity.request.SupportAgreementParam;
import com.bemore.api.entity.response.SupportAgreementView;
import java.util.List;
import java.util.Map;

public interface SupportAgreementService {

    // 重新计算扶持协议
    void recomputeContract(SupportAgreementReq supportAgreementReq);

    void computePlatform(SupportAgreementReq supportAgreementReq);

    void computeContract(SupportAgreementReq supportAgreementReq);

    SupportAgreementView findSupportAgreement(String id);

    Page<SupportAgreementView> findAgreementListByPage(SettledQueryParam param);

    void deleteOneSupportAgreement(String id);

    void updateSupportAgreement(SupportAgreementParam param);

    void saveSupportAgreement(SupportAgreementParam param);

    void fixSupportData(SupportFixDataReq req);

}
