package com.bemore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.dao.NoticeDao;
import com.bemore.api.dao.NoticeUsersDao;
import com.bemore.api.dao.NoticeFilesDao;
import com.bemore.api.dao.mapper.AccountMapper;
import com.bemore.api.entity.Account;
import com.bemore.api.entity.Notice;
import com.bemore.api.entity.NoticeUsers;
import com.bemore.api.entity.NoticeFiles;
import com.bemore.api.entity.request.NoticeParam;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeDao noticeDao;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private NoticeFilesDao noticeFilesDao;
    @Autowired
    private NoticeUsersDao noticeEnterpriseDao;


    @Override
    public String sendNotice(NoticeParam noticeParam) {
        Notice notice = new Notice();
        notice.setTitle(noticeParam.getTitle());
        notice.setContent(noticeParam.getContent());
        notice.setIssueTime(System.currentTimeMillis());
//        notice.setFiles(noticeParam.getFileList());

        List<NoticeFiles> noticeFiles = new ArrayList<NoticeFiles>();
        if (!CollectionUtils.isEmpty(noticeParam.getFileList())) {
            noticeParam.getFileList().forEach(item -> {
                item.setNotice(notice);
                noticeFiles.add(item);
            });
        }
        notice.setFiles(noticeFiles);
        List<NoticeUsers> noticeUsers = new ArrayList<NoticeUsers>();
        if (!noticeParam.getTargetEnterprise().isEmpty()) {

            QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(Account::getEnterpriseName, noticeParam.getTargetEnterprise());
            List<Account> users = accountMapper.selectList(queryWrapper);
            if (users.isEmpty()) throw new WebException(102, "找不到该企业对应的接收用户");
            users.forEach(user -> {
                NoticeUsers noticeUser = new NoticeUsers();
                noticeUser.setNotice(notice);
                noticeUser.setUserId(user.getId());
                noticeUser.setMstatus(0);
                noticeUser.setIssueTime(System.currentTimeMillis());
                noticeUsers.add(noticeUser);
            });

        }
        notice.setUsersList(noticeUsers);

        noticeDao.save(notice);


        return "success";
    }

    @Override
    public String deleteNotice(String id) {
        Notice notice = noticeDao.getOne(id);
        if(Objects.isNull(notice)) throw new WebException(101,"该消息不存在");
        noticeDao.delete(notice);

        return "success";
    }

    @Override
    public String readNotice(String id) {
        NoticeUsers notice = noticeEnterpriseDao.getOne(id);
        notice.setMstatus(1);
        noticeEnterpriseDao.saveAndFlush(notice);

        return "success";
    }

    @Override
    public List<String> getNoticeEnterprise(String enterpriseName) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        if (!Objects.isNull(enterpriseName) && !StringUtils.hasLength(enterpriseName)) {
            queryWrapper.lambda().eq(Account::getEnterpriseName, enterpriseName);
        } else {
            queryWrapper.lambda().isNotNull(Account::getEnterpriseName);
        }

        List<Account> accountList = accountMapper.selectList(queryWrapper);
        if (!accountList.isEmpty()) {
            return accountList.stream().map(Account::getEnterpriseName).distinct().collect(Collectors.toList());
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public Page<Notice> getAllNotice(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, "issueTime");
        Page<Notice> allPage = noticeDao.findAll(pageable);


        return allPage;

    }

    @Override
    public Page<NoticeUsers> getMyNotice(Integer page, Integer size, String userId) {
        Specification<NoticeUsers> specification = (Specification<NoticeUsers>) (root, query, criteriaBuilder) -> {
            Predicate p3 = criteriaBuilder.equal(root.get("userId").as(String.class), userId);
            return p3;
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, "issueTime");

        Page<NoticeUsers> allPage = noticeEnterpriseDao.findAll(specification, pageable);


        return allPage;

    }

    @Override
    public Integer getUnreadCount(String userId) {
        return noticeEnterpriseDao.countUnreadNotice(userId);

    }


}
