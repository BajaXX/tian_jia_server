package com.bemore.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bemore.api.entity.RollbackLog;

public interface RollbackLogMapper extends BaseMapper<RollbackLog> {
    RollbackLog selectNewestRollbackLog();
}
