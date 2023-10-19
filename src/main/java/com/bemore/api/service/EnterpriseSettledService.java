package com.bemore.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bemore.api.entity.EnterpriseSettled;
import com.bemore.api.entity.request.SettledQueryParam;
import com.bemore.api.entity.response.EnterpriseSettledTaxView;
import com.bemore.api.entity.response.EnterpriseSettledView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface EnterpriseSettledService {

    void deleteOneById(String id);

    EnterpriseSettledView findOneById(String id);

    IPage<EnterpriseSettled> findEnterpriseSettledPageOnPage(SettledQueryParam param);

    void updateInfo(EnterpriseSettledView settled);

    void saveInfo(EnterpriseSettled settled);

    /**
     * @Description 查询入驻企业清单
     * @Title findEnterpriseSettledTaxList
     * @Param []
     * @Return java.util.List<com.bemore.api.entity.response.EnterpriseSettledTaxView>
     * @Author Louis
     * @Date 2022/04/25 20:03
     */
    List<EnterpriseSettledTaxView> findEnterpriseSettledTaxList();

    /**
     * @Description 导出入驻企业清单
     * @Title exportEnterpriseSettledTaxList
     * @Param [response]
     * @Return void
     * @Author Louis
     * @Date 2022/04/25 20:04
     */
    void exportEnterpriseSettledTaxList(HttpServletResponse response);
}
