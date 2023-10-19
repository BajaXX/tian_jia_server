package com.bemore.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bemore.api.entity.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
    String findMeetingRoomManager();

    List<String> getEnterpriseMobile(String[] enterpriseNames);
}
