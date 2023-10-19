package com.bemore.api.service.tax;

import com.bemore.api.dto.AddEnterpriseDetailExportDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 新增企业明细
 * @author 余江俊
 */
public interface AddEnterpriseService {

    /**
     * 导出
     * @param response
     */
    public void exportAddEnterpriseDetail(Integer year, Integer month , HttpServletResponse response) throws IOException;

    List<AddEnterpriseDetailExportDto> getAddDetail(Integer year, Integer month);
}
