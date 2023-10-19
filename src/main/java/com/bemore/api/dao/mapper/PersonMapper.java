package com.bemore.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bemore.api.entity.Person;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonMapper extends BaseMapper<Person> {
}
