package com.bemore.api.service;

import com.bemore.api.entity.Notice;
import com.bemore.api.entity.NoticeUsers;
import com.bemore.api.entity.request.NoticeParam;
import org.springframework.data.domain.Page;

import java.util.List;


public interface NoticeService {


    String sendNotice(NoticeParam noticeParam);

    String deleteNotice(String id);

    String readNotice(String id);

    List<String> getNoticeEnterprise(String enterpriseName);

    Page<Notice> getAllNotice(Integer page, Integer size);

    Page<NoticeUsers> getMyNotice(Integer page, Integer size, String userId);

    Integer getUnreadCount(String enterpriseName);


}
