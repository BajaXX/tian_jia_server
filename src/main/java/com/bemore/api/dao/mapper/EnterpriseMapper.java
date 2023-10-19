package com.bemore.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bemore.api.dto.EnterpriseQueryDto;
import com.bemore.api.entity.Enterprise;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnterpriseMapper extends BaseMapper<Enterprise> {

    /**
     * 高级查询企业列表
     * @param query
     * @return
     */
    List<Enterprise> getEnterprise(EnterpriseQueryDto query);

    int getEnterpriseCount(EnterpriseQueryDto query);


}
