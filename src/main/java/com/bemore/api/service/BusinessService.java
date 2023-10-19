package com.bemore.api.service;

import com.bemore.api.dto.EnterprisePageDto;
import com.bemore.api.dto.EnterpriseQueryDto;
import com.bemore.api.dto.PersonDto;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.request.ReportBaseRequest;
import com.bemore.api.entity.response.HomeStatView;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BusinessService {

    void reportEnterpriseService(ReportBaseRequest request, HttpServletResponse response);

    void supplementary(MultipartFile file);

    HomeStatView getHomeStat();

    void importEnterpriseBatch(MultipartFile file);

    /**
     * 企业列表查询
     * @param queryDto
     * @return
     */
    EnterprisePageDto getEnterprises(EnterpriseQueryDto queryDto);

    /**
     * 现在企业成员
     * @param personDto
     */
    void savePerson(PersonDto personDto);
}
