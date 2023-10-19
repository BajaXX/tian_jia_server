package com.bemore.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.config.SMSConfig;
import com.bemore.api.dao.mapper.AccountMapper;
import com.bemore.api.dto.NoticePageDto;
import com.bemore.api.entity.Account;
import com.bemore.api.entity.Notice;
import com.bemore.api.entity.NoticeUsers;
import com.bemore.api.entity.NoticeFiles;
import com.bemore.api.entity.request.NoticeParam;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.NoticeService;
import com.bemore.api.util.GsonUtil;
import com.bemore.api.util.SmsUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName NoticeController
 * @Description 消息类控制层
 * @Author Louis
 * @Date 2022/04/24 23:27
 */
@RestController
@RequestMapping("/notice")
public class NoticeController extends BaseController {
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    SMSConfig smsConfig;


    /**
     * @param param
     * @return java.lang.String
     * @throws IOException
     * @Description 发送通知消息
     */
    @ApiOperation(value = "发送通知消息")
    @PostMapping("/sendNotice")
    public String sendNotice(@RequestBody NoticeParam param) {
        String res = noticeService.sendNotice(param);
        String[] names = param.getTargetEnterprise().toArray(new String[param.getTargetEnterprise().size()]);
        List<String> mobileList = accountMapper.getEnterpriseMobile(names);
        if (!mobileList.isEmpty()) {
            mobileList.forEach(k -> {
                SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您登陆“智慧园区”平台，查看最新消息。", k);
            });
        }
        return GsonUtil.build(res);
    }

    /**
     * @param param
     * @return java.lang.String
     * @throws IOException
     * @Description 外部接口发送通知消息
     */
    @ApiOperation(value = "外部接口发送通知消息")
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody NoticeParam param) {
        if(Objects.isNull(param.getSign())){
            throw new WebException(107,"缺少签名");
        }
        if(Objects.isNull(param.getT())){
            throw new WebException(108,"缺少时间戳");
        }

        //计算签名
        String zkey = "b59d5d2eebajaf128d8cf995817a1acf";
        String sign = DigestUtils.md5Hex((param.getT()+ zkey).getBytes());

        if(!sign.equals(param.getSign())){
            throw new WebException(103,"签名错误");
        }


        if(CollectionUtils.isEmpty(param.getTargetEnterprise())){
           throw new WebException(104,"企业名称不能为空");
        }

        if(Objects.isNull(param.getContent())){
           throw new WebException(105,"消息内容不能为空");
        }
        if(Objects.isNull(param.getTitle())){
           throw new WebException(106,"消息标题不能为空");
        }



        String res = noticeService.sendNotice(param);
        //发送短信，暂时不发。
//        String[] names = param.getTargetEnterprise().toArray(new String[param.getTargetEnterprise().size()]);
//        List<String> mobileList = accountMapper.getEnterpriseMobile(names);
//        if (!mobileList.isEmpty()) {
//            mobileList.forEach(k -> {
//                SmsUtil.sendSms4Meeting(smsConfig, "【长三角金融产业园】提醒您登陆“智慧园区”平台，查看最新消息。", k);
//            });
//        }
        return GsonUtil.build(res);
    }

    /**
     * @return java.lang.String
     * @throws IOException
     * @Description 获取可发送的企业名称
     */
    @ApiOperation(value = "获取可发送的企业名称")
    @PostMapping("/getNoticeEnterprise")
    public String sendNotice(@RequestParam(required = false) String enterpriseName) {
        return GsonUtil.build(noticeService.getNoticeEnterprise(enterpriseName));
    }

    /**
     * @return java.lang.String
     * @Description 删除消息
     */
    @ApiOperation(value = "删除消息")
    @PostMapping("/deleteNotice")
    public String deleteNotice(@RequestBody Map<String, Object> params) {
        return GsonUtil.build(noticeService.deleteNotice(params.get("id").toString()));
    }

    /**
     * @return java.lang.String
     * @Description 阅读消息
     */
    @ApiOperation(value = "阅读消息")
    @GetMapping("/readNotice")
    public String readNotice(@RequestParam String id) {
        return GsonUtil.build(noticeService.readNotice(id));
    }

    /**
     * @return java.lang.String
     * @throws IOException
     * @Description 获取已发送的消息
     */
    @ApiOperation(value = "获取已发送的消息")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "size", dataType = "int", required = false, value = "页数"),
            @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "页码")
    })
    public String list(@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "1") int page) {
        Page<Notice> noticeList = noticeService.getAllNotice(page, size);
        NoticePageDto noticePageDto = new NoticePageDto();

        noticePageDto.setCurrentPage(page - 1);
        noticePageDto.setTotalPage(noticeList.getTotalPages());
        noticePageDto.setTotalSize(noticeList.getTotalElements());
//        noticePageDto.setNotices(noticeList.getContent());
        List<NoticeParam> noticeParams = noticeList.getContent().stream().map(item -> {
            NoticeParam notice = new NoticeParam();
            notice.setId(item.getId());
            notice.setContent(item.getContent());
            notice.setIssueTime(item.getIssueTime());
            notice.setTitle(item.getTitle());
            //封装企业
            List<String> userList = item.getUsersList().stream().map(t -> t.getUserId()).collect(Collectors.toList());
            if (!userList.isEmpty()) {
                QueryWrapper<Account> queryWrapper = new QueryWrapper<Account>();
                queryWrapper.select("distinct enterprise_name").lambda().in(Account::getId, userList);
                List<Account> enterpriseList = accountMapper.selectList(queryWrapper);
                List<String> enterpriseName = enterpriseList.stream().map(t -> t.getEnterpriseName()).collect(Collectors.toList());
                notice.setTargetEnterprise(enterpriseName);
            }
            //封装附件
            List<NoticeFiles> filesList = item.getFiles().stream().map(t -> {
                NoticeFiles file = new NoticeFiles();
                file.setFileid(t.getFileid());
                file.setFileUrl(t.getFileUrl());
                file.setFileName(t.getFileName());
                file.setFileSize(t.getFileSize());
                return file;
            }).collect(Collectors.toList());
            notice.setFileList(filesList);
            return notice;
        }).collect(Collectors.toList());

        noticePageDto.setNotices(noticeParams);


        return GsonUtil.build(noticePageDto);
    }

    /**
     * @return java.lang.String
     * @throws IOException
     * @Description 根据企业获取收到消息
     */
    @ApiOperation(value = "根据企业获取收到消息")
    @GetMapping("/listMyNotice")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "size", dataType = "int", required = false, value = "页数"),
            @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "页码")
    })
    public String listMyNotice(@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "1") int page, @RequestParam(required = true) String userId) {
        Page<NoticeUsers> noticeList = noticeService.getMyNotice(page, size, userId);
        NoticePageDto noticePageDto = new NoticePageDto();

        noticePageDto.setCurrentPage(page - 1);
        noticePageDto.setTotalPage(noticeList.getTotalPages());
        noticePageDto.setTotalSize(noticeList.getTotalElements());


        List<NoticeParam> noticeParams = noticeList.getContent().stream().map(item -> {
            NoticeParam notice = new NoticeParam();
            notice.setId(item.getId());
            notice.setContent(item.getNotice().getContent());
            notice.setIssueTime(item.getIssueTime());
            notice.setTitle(item.getNotice().getTitle());
            notice.setMstatus(item.getMstatus());
            List<NoticeFiles> filesList = item.getNotice().getFiles().stream().map(t -> {
                NoticeFiles file = new NoticeFiles();
                file.setFileid(t.getFileid());
                file.setFileUrl(t.getFileUrl());
                file.setFileName(t.getFileName());
                file.setFileSize(t.getFileSize());
                return file;
            }).collect(Collectors.toList());
            notice.setFileList(filesList);
            return notice;
        }).collect(Collectors.toList());

        noticePageDto.setNotices(noticeParams);

        return GsonUtil.build(noticePageDto);
    }

    /**
     * @return java.lang.String
     * @throws IOException
     * @Description 获取未读消息数量
     */
    @ApiOperation(value = "获取未读消息数量")
    @GetMapping("/getUnreadCount")
    public String getUnreadCount(@RequestParam String userId) {
        Integer count = noticeService.getUnreadCount(userId);

        return GsonUtil.build(count);
    }

}
