package com.bemore.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bemore.api.dao.mapper.EnterpriseAddrMapper;
import com.bemore.api.entity.EnterpriseAddr;
import com.bemore.api.service.EnterpriseAddrService;
import org.springframework.stereotype.Service;

@Service
public class EnterpriseAddrServiceImpl extends ServiceImpl<EnterpriseAddrMapper, EnterpriseAddr> implements EnterpriseAddrService{
}
