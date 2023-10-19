package com.bemore.api.controller.officialWebsite.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bemore.api.controller.officialWebsite.OfficialWebsiteUser;
import com.bemore.api.controller.officialWebsite.OfficialWebsiteUserService;
import com.bemore.api.dao.mapper.OfficialWebsiteUserMapper;
import org.springframework.stereotype.Service;

@Service
public class OfficialWebsiteUserServiceImpl extends ServiceImpl<OfficialWebsiteUserMapper, OfficialWebsiteUser> implements OfficialWebsiteUserService {
}
