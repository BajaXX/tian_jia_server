package com.bemore.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.bemore.api.config.SMSConfig;
import com.bemore.api.constant.ErrorCodeConstants;
import com.bemore.api.dao.MeetingRoomUseLogDao;
import com.bemore.api.dao.mapper.AccountMapper;
import com.bemore.api.dto.MeetingRoomUseLogPageDto;
import com.bemore.api.entity.MeetingRoomUseLog;
import com.bemore.api.exception.WebException;
import com.bemore.api.util.SmsUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bemore.api.dao.MeetingRoomDao;
import com.bemore.api.dto.MeetingRoomPageDto;
import com.bemore.api.entity.MeetingRoom;
import com.bemore.api.util.GsonUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/meeting")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomDao meetingRoomDao;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private MeetingRoomUseLogDao meetingRoomUseLogDao;

    @Autowired
    SMSConfig smsConfig;


    @PostMapping("/save")
    @ApiOperation(value = "保存会议室信息")
    public String save(@RequestBody MeetingRoom meetingRoom) {
        meetingRoomDao.save(meetingRoom);
        return GsonUtil.build();
    }

    @PostMapping("/saveLogs")
    @ApiOperation(value = "审核会议室预定信息")
    public String saveLogs(@RequestBody MeetingRoomUseLog meetingRoomUseLog) {
        Optional<MeetingRoomUseLog> userLog=meetingRoomUseLogDao.findById(meetingRoomUseLog.getId());
        MeetingRoomUseLog roomUseLog=new MeetingRoomUseLog();
        if(userLog.isPresent()){
            roomUseLog=userLog.get();
        }else{
            throw new WebException(101,"预订信息不存在");
        }


//        if (meetingRoomUseLog.getStatus() == 0) {
//            //发送信息给会议审核员
//            String mobile = accountMapper.findMeetingRoomManager();
//            SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您，有新的会议室预订成功，详细情况请登录“智慧园区”平台查看。", mobile);
//        } else
        if (meetingRoomUseLog.getStatus() == 1) {
            //发送消息给申请人
            SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您，您提交的会议室申请，审核结果为：已通过，详细情况请登录“智慧园区”平台查看。", roomUseLog.getMobile());
            //发送信息给会议管理员
            String mobile = accountMapper.findMeetingRoomManager();
            if (!Objects.isNull(mobile)) {
                SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您，有新的会议室预订成功，详细情况请登录“智慧园区”平台查看。", mobile);
            }
            //发送信息给食堂的
            if (roomUseLog.getLunch() == 1) {
                DateTime dateTime = new DateTime(roomUseLog.getStartTime());
                String date=dateTime.toString("yyyy年MM月dd日");

                SmsUtil.sendSms4Meeting(smsConfig, String.format("【长三角金融产业园】提醒您，%s会议室，%s需要餐食类型：%s，详细情况请联系会议室管理员。", roomUseLog.getMeetingRoomName(), date, roomUseLog.getLunchType() == 1 ? "自助" : "简餐"), "13816372844");
            }


        } else if (meetingRoomUseLog.getStatus() == 3) {
            //发送消息给申请人
            SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您，您提交的会议室申请，审核结果为：已撤销，详细情况请登录“智慧园区”平台查看。", roomUseLog.getMobile());
            //发送信息给会议管理员
            String mobile = accountMapper.findMeetingRoomManager();
            SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您，有新的会议室取消通知，详细情况请登录“智慧园区”平台查看。", mobile);
            //发送信息给食堂的
            if (roomUseLog.getLunch() == 1) {

                SmsUtil.sendSms4Meeting(smsConfig, String.format("【长三角金融产业园】提醒您，%s会议室，预订已取消，详细情况请联系会议室管理员。", roomUseLog.getMeetingRoomName()), "13816372844");
            }
        } else if (meetingRoomUseLog.getStatus() == 2) {
            //发送消息给申请人
            SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您，您预订的会议室：已拒绝，详细情况请登录“智慧园区”平台查看。", roomUseLog.getMobile());

        }
        roomUseLog.setStatus(meetingRoomUseLog.getStatus());
        meetingRoomUseLogDao.saveAndFlush(roomUseLog);
        return GsonUtil.build();
    }

    @GetMapping("/fetch/{id}")
    @ApiOperation(value = "获取会议室信息")
    public String fetch(@PathVariable String id) {
        return GsonUtil.build(meetingRoomDao.findById(id).get());
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询会议室信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "size", dataType = "int", required = false, value = "页数"),
            @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "页码")
    })
    public String list(@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "1") int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MeetingRoom> result = meetingRoomDao.findAll(new Specification<MeetingRoom>() {
            @Override
            public Predicate toPredicate(Root<MeetingRoom> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        }, pageRequest);
        MeetingRoomPageDto resultDto = new MeetingRoomPageDto();
        resultDto.setMeetingRooms(result.getContent());
        resultDto.setCurrentPage(page);
        resultDto.setTotalSize(result.getTotalElements());
        resultDto.setTotalPage(result.getTotalPages());
        return GsonUtil.build(resultDto);
    }


    @PostMapping("/newLog")
    @ApiOperation(value = "预定会议室信息")
    public String newLog(@RequestBody MeetingRoomUseLog meetingRoomUseLog) {
        MeetingRoom meetingRoom = meetingRoomDao.findById(meetingRoomUseLog.getMeetingRoomId()).get();
        if (meetingRoom != null) {
            meetingRoomUseLog.setMeetingRoomName(meetingRoom.getName());
            meetingRoomUseLog.setMeetingRoomAddress(meetingRoom.getAddress());
            meetingRoomUseLogDao.save(meetingRoomUseLog);
        } else {
            throw new WebException(ErrorCodeConstants.RESULT_NOT_FOUND_CODE, ErrorCodeConstants.RESULT_NOT_FOUND_MSG);
        }

        return GsonUtil.build();
    }

    @GetMapping("/approveList")
    @ApiOperation(value = "获取待审核预定")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "size", dataType = "int", required = false, value = "页数"),
            @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "页码")
    })
    public String approveList(@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "1") int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MeetingRoomUseLog> result = meetingRoomUseLogDao.findAll(new Specification<MeetingRoomUseLog>() {
            @Override
            public Predicate toPredicate(Root<MeetingRoomUseLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                List<Predicate> list = new ArrayList<Predicate>();
//
//                Predicate[] p = new Predicate[list.size()];
//                return cb.and(list.toArray(p));
                return query.orderBy(cb.desc(root.get("startTime"))).getRestriction();
            }
        }, pageRequest);
        MeetingRoomUseLogPageDto resultDto = new MeetingRoomUseLogPageDto();
        resultDto.setMeetingRoomUseLogs(result.getContent());
        resultDto.setCurrentPage(page);
        resultDto.setTotalSize(result.getTotalElements());
        resultDto.setTotalPage(result.getTotalPages());
        return GsonUtil.build(resultDto);
    }

    @GetMapping("/getOrderList")
    @ApiOperation(value = "获取已预订情况")
    public String getOrderList(@RequestParam String meetingRoomId, @RequestParam String startTime) {
        Specification<MeetingRoomUseLog> specification = (Specification<MeetingRoomUseLog>) (root, query, builder) -> {
            //
            query.where(builder.and(builder.equal(root.get("meetingRoomId").as(String.class), meetingRoomId), root.get("status").as(Integer.class).in(new Integer[]{1, 0}), builder.gt(root.get("startTime").as(Long.class), Long.valueOf(startTime))));
            return query.getRestriction();
        };
        List<MeetingRoomUseLog> meetingRoomUseLogList = meetingRoomUseLogDao.findAll(specification);
        return GsonUtil.build(meetingRoomUseLogList);
    }


}
