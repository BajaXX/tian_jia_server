package com.bemore.api.controller.officialWebsite;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bemore.api.dao.mapper.OfficialWebsiteUserMapper;
import com.bemore.api.entity.EnterpriseSettled;
import com.bemore.api.entity.Notice;
import com.bemore.api.exception.WebException;
import com.bemore.api.util.CidUtil;
import com.bemore.api.util.GsonUtil;
import com.bemore.api.util.VerificationCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Api("官网的注册登录接口")
@RestController
@Slf4j
@RequestMapping("/officialWebsite")
public class OfficialWebsiteController {

    private final VerificationCodeService verificationCodeService;
    private final OfficialWebsiteUserService officialWebsiteUserService;


    public OfficialWebsiteController(VerificationCodeService verificationCodeService,
                                     OfficialWebsiteUserService officialWebsiteUserService) {
        this.verificationCodeService = verificationCodeService;
        this.officialWebsiteUserService = officialWebsiteUserService;
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录接口")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
                    @ApiImplicitParam(name = "password", value = "登录密码", required = true, dataType = "String")}

    )
    public String officialWebsiteLogin(@RequestBody RegisterParam registerParam) {
        String mobile = registerParam.getMobile();
        if (!StringUtils.hasLength(mobile)) throw new RuntimeException("手机号不能为空");
        QueryWrapper<OfficialWebsiteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OfficialWebsiteUser::getMobile, mobile);
        OfficialWebsiteUser one = officialWebsiteUserService.getOne(queryWrapper);
        if (Objects.isNull(one)) throw new WebException(104, "此用户未注册");

        if (CidUtil.encryption(registerParam.getPwd()).equals(one.getPwd())) {
            return GsonUtil.build(one);
        } else {
            throw new WebException(105, "密码错误");
        }

    }

    @PostMapping("/register")
    @ApiOperation(value = "注册接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "enterpriseName", value = "公司名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "form", dataType = "String")
    })
    public String officialWebsiteAccountRegister(@RequestBody RegisterParam registerParam) {
        String code = registerParam.getCode();
        String mobile = registerParam.getMobile();
        if (!StringUtils.hasLength(mobile)) throw new RuntimeException("手机号不能为空");
        if (!StringUtils.hasLength(code)) throw new RuntimeException("验证码不能为空");
        QueryWrapper<OfficialWebsiteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OfficialWebsiteUser::getMobile, mobile);
        OfficialWebsiteUser one = officialWebsiteUserService.getOne(queryWrapper);
        if (!Objects.isNull(one)) throw new WebException(101, "此手机号已注册");

        // 验证手机号
        VerificationCode byId = verificationCodeService.getById(registerParam.getMobile());
        if (Objects.isNull(byId)) throw new WebException(102, "手机号验证码错误");
        if (!Objects.equals(byId.getCode(), code)) throw new WebException(103, "验证码错误");

        OfficialWebsiteUser user = new OfficialWebsiteUser();
        user.setMobile(registerParam.getMobile());
        user.setUserName(registerParam.getUserName());
        user.setPwd(CidUtil.encryption(registerParam.getPwd()));
        user.setEnterpriseName(registerParam.getEnterpriseName());
        officialWebsiteUserService.save(user);
        verificationCodeService.removeById(byId);
        return GsonUtil.build("success");
    }

    @PostMapping("/resetPwd")
    @ApiOperation(value = "重置密码接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "form", dataType = "String")
    })
    public String resetPwd(@RequestBody RegisterParam registerParam) {
        String code = registerParam.getCode();
        String mobile = registerParam.getMobile();
        if (!StringUtils.hasLength(mobile)) throw new RuntimeException("手机号不能为空");
        if (!StringUtils.hasLength(code)) throw new RuntimeException("验证码不能为空");
        QueryWrapper<OfficialWebsiteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OfficialWebsiteUser::getMobile, mobile);
        OfficialWebsiteUser one = officialWebsiteUserService.getOne(queryWrapper);
        if (Objects.isNull(one)) throw new WebException(101, "该手机号未注册");

        // 验证手机号
        VerificationCode byId = verificationCodeService.getById(registerParam.getMobile());
        if (Objects.isNull(byId)) throw new WebException(102, "手机号验证码错误");
        if (!Objects.equals(byId.getCode(), code)) throw new WebException(103, "验证码错误");


        one.setPwd(CidUtil.encryption(registerParam.getPwd()));

        officialWebsiteUserService.saveOrUpdate(one);
        verificationCodeService.removeById(byId);
        return GsonUtil.build("success");
    }

    @GetMapping("/createCode")
    @ApiOperation(value = "生成验证码")
    public String createCode(@RequestParam String mobile) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            buffer.append(random.nextInt(10));
        }
        VerificationCode code = new VerificationCode();
        code.setMobile(mobile);
        code.setCode(buffer.toString());
        verificationCodeService.saveOrUpdate(code);
        VerificationCodeUtil.sendMessage(buffer.toString(), mobile);
        return GsonUtil.build("success");
    }

    @GetMapping("/getZCUrl")
    @ApiOperation(value = "获取政策服务地址")
    public String getZCUrl(@RequestParam String uid) throws UnsupportedEncodingException {
        QueryWrapper<OfficialWebsiteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OfficialWebsiteUser::getId, uid);
        OfficialWebsiteUser one = officialWebsiteUserService.getOne(queryWrapper);
        if (Objects.isNull(one)) throw new WebException(101, "未找到用户信息，请重新登录");
        String homepage = "http://yrdfp.com/login";
        String args = "enterpriseName=%s&failRedirect=%s&mobile=%s&timestamp=%d&uid=%s";
        String zkey = "b59d5d2eebajaf128d8cf995817a1acf";
        String nosign = String.format(args, URLEncoder.encode(one.getEnterpriseName(), "UTF-8"), URLEncoder.encode(homepage, "UTF-8"), one.getMobile(), System.currentTimeMillis(), one.getId());
        String sign = DigestUtils.md5Hex((nosign + "&zkey=" + zkey).getBytes());
        String url = String.format("http://policyservice.yrdfp.com/policy-matching/#/home?%s&sign=%s", nosign, sign);
        return GsonUtil.build(url);
    }
    @GetMapping("/getCRUrl")
    @ApiOperation(value = "获取政策服务地址")
    public String getCRUrl(@RequestParam String uid) throws UnsupportedEncodingException {
        QueryWrapper<OfficialWebsiteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OfficialWebsiteUser::getId, uid);
        OfficialWebsiteUser one = officialWebsiteUserService.getOne(queryWrapper);
        if (Objects.isNull(one)) throw new WebException(101, "未找到用户信息，请重新登录");
        String homepage = "http://yrdfp.com/login";
        String args = "enterpriseName=%s&failRedirect=%s&mobile=%s&timestamp=%d&uid=%s";
        String zkey = "b59d5d2eebajaf128d8cf995817a1acf";
        String nosign = String.format(args, URLEncoder.encode(one.getEnterpriseName(), "UTF-8"), URLEncoder.encode(homepage, "UTF-8"), one.getMobile(), System.currentTimeMillis(), one.getId());
        String sign = DigestUtils.md5Hex((nosign + "&zkey=" + zkey).getBytes());
        String url = String.format("https://vip8.console.clickpaas.com/policy-matching/#/financialServices?%s&sign=%s", nosign, sign);
        return GsonUtil.build(url);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取官网注册列表")
    public String list(@RequestParam int size, @RequestParam int page) {
        QueryWrapper<OfficialWebsiteUser> queryWrapper = new QueryWrapper<OfficialWebsiteUser>();
        IPage<Map<String, Object>> ipage = new Page<>(page, size);
        queryWrapper.lambda().orderByDesc(OfficialWebsiteUser::getCreateTime);
        IPage<Map<String, Object>> page1 = officialWebsiteUserService.pageMaps(ipage,queryWrapper);

        return GsonUtil.build(page1);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "获取官网注册列表")
    public String delete(@RequestParam String id) {
        officialWebsiteUserService.removeById(id);
        return GsonUtil.build();
    }
}
