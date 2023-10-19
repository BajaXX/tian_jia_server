package com.bemore.api.controller;

import com.bemore.api.entity.Account;
import com.bemore.api.entity.request.AccountParam;
import com.bemore.api.entity.request.UpdatePwdParam;
import com.bemore.api.service.AccountService;
import com.bemore.api.util.GsonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accountOne")
    @ApiOperation(value = "查询单个用户")
    public String getAccountOne(@RequestParam String uid) {
        return GsonUtil.build(accountService.findAccountOne(uid));
    }

    @GetMapping("/accountPageList")
    @ApiOperation(value = "查询用户列表（分页）")
    public String getAccountPageList(@RequestParam Integer page,@RequestParam Integer size) {
        return GsonUtil.build(accountService.findAccountPageList(page,size));
    }

    @DeleteMapping("/delete/{uid}")
    @ApiOperation(value = "删除用户")
    public String deleteAccount(@PathVariable("uid") String uid) {
        accountService.deleteAccount(uid);
        return GsonUtil.build("success");
    }

    @PostMapping("/updateAccount")
    @ApiOperation(value = "修改用户")
    public String updateRole(@RequestBody Account account) {
        accountService.updateAccount(account);
        return GsonUtil.build("success");
    }

    @PostMapping("/updateNoticeInfo")
    @ApiOperation(value = "修改预警邮件")
    public String updateNoticeInfo(@RequestBody Account account) {
        accountService.updateNoticeInfo(account.getId(),account.getOpenNotice(),account.getNoticeEmail());
        return GsonUtil.build("success");
    }

    @GetMapping("/resetPwd")
    @ApiOperation(value = "重置用户密码")
    public String resetPwd(@RequestParam String uid) {
        accountService.resetAccountPwd(uid);
        return GsonUtil.build("success");
    }

    @PostMapping("/updatePwd")
    @ApiOperation(value = "修改用户密码")
    public String updatePwd(@RequestBody UpdatePwdParam updatePwdParam) {
        String str = accountService.updateAccountPwd(updatePwdParam);
        return GsonUtil.build(str);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增用户")
    public String addNewAccount(@RequestBody AccountParam param) {
        accountService.insertNewAccount(param);
        return GsonUtil.build("success");
    }

}
