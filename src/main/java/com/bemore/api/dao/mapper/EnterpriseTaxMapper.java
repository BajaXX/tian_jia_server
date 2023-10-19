package com.bemore.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bemore.api.entity.EnterpriseTaxPlus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnterpriseTaxMapper extends BaseMapper<EnterpriseTaxPlus> {

    EnterpriseTaxPlus selectNewestTaxMonth();

    List<EnterpriseTaxPlus> selectNewTaxAccount(String queryDate);

    List<EnterpriseTaxPlus> selectNewValidAccountList(String queryDate);

    Double selectTotalTaxByName(String enterpriseName,String queryDate);
}
