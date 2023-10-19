package com.bemore.api.dao.mapper;

import com.bemore.api.dto.EnterpriseMasterInfoDto;
import com.bemore.api.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TEnterpriseMemberMapper {
    int deleteByPrimaryKey(String id);

    int insert(Member record);

    int insertSelective(Member record);

    Member selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Member record);

    int updateByPrimaryKey(Member record);

    EnterpriseMasterInfoDto getMasterInfo(@Param("enterpriseId") String enterpriseId);
}