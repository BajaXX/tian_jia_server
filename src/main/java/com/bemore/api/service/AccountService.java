package com.bemore.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bemore.api.entity.Account;
import com.bemore.api.entity.request.AccountParam;
import com.bemore.api.entity.request.UpdatePwdParam;


public interface AccountService {

    Account findAccountOne(String uid);

    IPage<Account> findAccountPageList(Integer page, Integer size);

    void deleteAccount(String uid);

    void updateAccount(Account account);

    void updateNoticeInfo(String uid, Integer openNotice, String noticeEmail);

    void resetAccountPwd(String uid);

    String updateAccountPwd(UpdatePwdParam updatePwdParam);

    void insertNewAccount(AccountParam param);

    Object getCPLoginToken(Account account);
}
