package com.bemore.api.controller.officialWebsite.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bemore.api.controller.officialWebsite.VerificationCode;
import com.bemore.api.controller.officialWebsite.VerificationCodeService;
import com.bemore.api.dao.mapper.VerificationCodeMapper;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeServiceImpl extends ServiceImpl<VerificationCodeMapper, VerificationCode> implements VerificationCodeService {
}
