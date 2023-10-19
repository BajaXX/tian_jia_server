package com.bemore.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bemore.api.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
