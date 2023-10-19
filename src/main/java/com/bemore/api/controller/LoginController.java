package com.bemore.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.constant.ErrorCodeConstants;
import com.bemore.api.dao.mapper.AccountMapper;
import com.bemore.api.entity.Account;
import com.bemore.api.entity.request.AccountParam;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.AccountService;
import com.bemore.api.util.CidUtil;
import com.bemore.api.util.GsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.Objects;

@RestController
@RequestMapping("/garden")
public class LoginController {

    private final AccountMapper accountMapper;

    @Autowired
    private AccountService accountService;

    public LoginController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @PostMapping("/userLogin")
    public String login(@RequestBody AccountParam param) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Account::getAccount,param.getAccount());
        Account account = accountMapper.selectOne(queryWrapper);
        if (Objects.isNull(account)) throw new WebException(ErrorCodeConstants.NO_ACCOUNT_CODE,ErrorCodeConstants.NO_ACCOUNT_MSG);
        String decryption = CidUtil.decryption(account.getPwd());
        if (Objects.equals(param.getPwd(),decryption)) {
            return GsonUtil.build(account);
        } else {
//            return GsonUtil.build(403,"error",null);
           throw new WebException(ErrorCodeConstants.PASS_ERR_CODE,ErrorCodeConstants.PASS_ERR_MSG);
        }
    }
    @PostMapping("/cpLogin")
    public String cpLogin(@RequestBody Map<String,String> params) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Account::getAccount,params.get("account"));
        Account account = accountMapper.selectOne(queryWrapper);
        if(Objects.isNull(account)) throw new WebException(100,"用户不存在");

        Object obj=accountService.getCPLoginToken(account);
        if (!Objects.isNull(obj)) {
            return GsonUtil.build(obj);
        } else {
//            return GsonUtil.build(403,"error",null);
           throw new WebException(ErrorCodeConstants.PASS_ERR_CODE,ErrorCodeConstants.PASS_ERR_MSG);
        }
    }
}
