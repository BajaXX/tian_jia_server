package com.bemore.api.dao.mapper;

import com.bemore.api.dto.AddEnterpriseDetailExportDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddEnterpriseDao {

    /**
     * 导出明细
     * @param year
     * @param month
     * @return
     */
    List<AddEnterpriseDetailExportDto> getExportDetail(@Param("year") int year, @Param("month") int month);
}
