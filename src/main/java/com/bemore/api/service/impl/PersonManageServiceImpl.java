package com.bemore.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bemore.api.dao.mapper.TPersonMapper;
import com.bemore.api.entity.TPerson;
import com.bemore.api.service.PersonManageService;
import org.springframework.stereotype.Service;

/**
 * @author yujiangjun
 */
@Service
public class PersonManageServiceImpl extends ServiceImpl<TPersonMapper,TPerson> implements PersonManageService {
}
