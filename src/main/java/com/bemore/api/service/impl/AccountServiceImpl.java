package com.bemore.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bemore.api.constant.CpConstants;
import com.bemore.api.dao.mapper.AccountMapper;
import com.bemore.api.entity.Account;
import com.bemore.api.entity.request.AccountParam;
import com.bemore.api.entity.request.UpdatePwdParam;
import com.bemore.api.enums.RoleEnum;
import com.bemore.api.enums.RoleGroupIDEnum;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.AccountService;
import com.bemore.api.util.CidUtil;
import com.bemore.api.util.CpSignUtil;

import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.khslbkyg.ApiResponse;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.khslbkyg.auth.extension.AuthConfig;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.khslbkyg.codegen.UsertableenterpriseApi;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.khslbkyg.model.SaveOrUpdateuserTableEnterpriseDTO;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.khslbkyg.model.UserTableEnterpriseDTOUpdate;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.khslbkyg.model.UserTableEnterpriseSaveOrUpdateDataResponseObject;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.pdpwzyvy.codegen.EnterpriseinformationApi;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.pdpwzyvy.model.*;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.vidyxcdq.codegen.UsertableApi;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.vidyxcdq.model.SaveOrUpdateuserTableDTO;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.vidyxcdq.model.UserTableDTOUpdate;
import com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.vidyxcdq.model.UserTableSaveOrUpdateDataResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Value("${account.defaultPwd}")
    private String defaultPwd;

    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public Account findAccountOne(String uid) {
        Account account = accountMapper.selectById(uid);
        return account;
    }

    @Override
    public IPage<Account> findAccountPageList(Integer page, Integer size) {
        IPage<Account> iPage = new Page<>(page, size);
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().notLike(Account::getRole, "supper_admin").select(Account.class, i -> !i.getColumn().equals("pwd")).orderByDesc(Account::getCreateTime);

        IPage<Account> iPage1 = accountMapper.selectPage(iPage, queryWrapper);
        return iPage1;
    }

    @Override
    public void deleteAccount(String uid) {
        accountMapper.deleteById(uid);
    }


    @Override
    public  void updateNoticeInfo(String uid, Integer openNotice, String noticeEmail){
        Account account = accountMapper.selectById(uid);
        if (Objects.isNull(account)) throw new WebException(101,"该用户不存在");

        account.setNoticeEmail(noticeEmail);
        account.setOpenNotice(openNotice);

        accountMapper.updateById(account);

    }

    @Override
    public void updateAccount(Account account) {
        Account result = accountMapper.selectById(account.getId());
        if (Objects.isNull(result)) throw new RuntimeException("account not found");
        String value = RoleEnum.valueOf(account.getRoleStr()).getValue();
        String rolegroupid = RoleGroupIDEnum.valueOf(account.getRoleStr()).getValue();
        account.setRole(value);
        account.setRoleGroupId(rolegroupid);
        account.setOpenNotice(result.getOpenNotice());
        account.setNoticeEmail(result.getNoticeEmail());

        //修改了手机号
        if (!account.getMobile().equals(result.getMobile())) {
            QueryWrapper<Account> accountWrapper = new QueryWrapper<Account>();
            accountWrapper.eq("mobile", account.getMobile());
            Account existsAccount = accountMapper.selectOne(accountWrapper);

            if (!Objects.isNull(existsAccount)) {
                throw new WebException(100, "用户手机号已经注册");
            }
            //调用同步接口，修改手机号
            JSONObject body = new JSONObject();
            body.put("newMobilePhone", account.getMobile());
            body.put("externalId", result.getMobile());
            body.put("externalTenantId", result.getMobile());

            DataResponseBatchAsyncResult dataResponseBatchAsyncResultMoible = CpSignUtil.openApiObject(CpConstants.B008, body, DataResponseBatchAsyncResult.class);
            if (dataResponseBatchAsyncResultMoible.getCode() != 0) {
                throw new WebException(101, "同步数据错误");
            }
        }
        //修改企业名称
        if (!account.getEnterpriseName().equals(result.getEnterpriseName())) {
            com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.pdpwzyvy.auth.extension.AuthConfig enterAuthConfig = new com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.pdpwzyvy.auth.extension.AuthConfig("PUubcmUDP5oiufUH", "UXXIo8Day0L0XysFaxJcaNRij0lhVw");
            enterAuthConfig.setUserId(result.getThirdUserId1());
            //调用同步企业信息接口
            EnterpriseinformationApi enterpriseinformationApi = new EnterpriseinformationApi();
            //1.3 没有 创建企业
            SaveOrUpdateEnterpriseinformationDTO saveOrUpdateEnterpriseinformationDTO = new SaveOrUpdateEnterpriseinformationDTO();
            EnterpriseinformationDTOUpdate enterpriseinformationDTOUpdate = new EnterpriseinformationDTOUpdate();
            enterpriseinformationDTOUpdate.setName(account.getEnterpriseName());
            saveOrUpdateEnterpriseinformationDTO.setUpdate(enterpriseinformationDTOUpdate);
            enterpriseinformationApi.getApiClient().setVerifyingSsl(false);
            EnterpriseinformationSaveOrUpdateDataResponseObject enterpriseinformationSaveOrUpdateDataResponseObject = enterpriseinformationApi.saveOrUpdateEnterpriseinformationUsingPOST(saveOrUpdateEnterpriseinformationDTO);
            if (enterpriseinformationSaveOrUpdateDataResponseObject.getCode() != 0) {
                throw new WebException(101, "同步数据错误");
            }
            String enterpriseID = "";
            Object saveEnterpriseResult = enterpriseinformationSaveOrUpdateDataResponseObject.getData();
            if (!Objects.isNull(saveEnterpriseResult)) {
                if (saveEnterpriseResult instanceof List) {
                    List<Object> list = (List<Object>) saveEnterpriseResult;
                    if (list.size() > 0) {
                        Map obj = (Map) list.get(0);
                        enterpriseID = obj.get("saveDataId").toString();
                    } else {
                        throw new WebException(101, "同步数据错误");
                    }
                } else {
                    enterpriseID = saveOrUpdateId(saveEnterpriseResult);
                }
            } else {
                throw new WebException(101, "同步数据错误");
            }
            //绑定企业和用户关系
            //2. 绑定企业和用户
            AuthConfig authConfig = new AuthConfig("PUubcmUDP5oiufUH", "UXXIo8Day0L0XysFaxJcaNRij0lhVw");
            authConfig.setUserId(result.getThirdUserId1());
            SaveOrUpdateuserTableEnterpriseDTO dto = new SaveOrUpdateuserTableEnterpriseDTO();
            UserTableEnterpriseDTOUpdate dd = new UserTableEnterpriseDTOUpdate();
            dd.setEnterpriseName(enterpriseID);
            dd.setUserName(result.getThirdUserId2());
            dd.setMainEnterprise("是");
            dto.setUpdate(dd);
            UsertableenterpriseApi usertableenterpriseApi = new UsertableenterpriseApi();
            usertableenterpriseApi.getApiClient().setVerifyingSsl(false);
            ApiResponse<UserTableEnterpriseSaveOrUpdateDataResponseObject> res = usertableenterpriseApi.saveOrUpdateUserTableEnterpriseUsingPOSTWithHttpInfo(dto);


            log.info("aPaasEntity:{}", res);
        }
        //权限发生修改
        if (!account.getRoleGroupId().equals(result.getRoleGroupId())) {

            //解除绑定，重新绑定
            JSONObject body = new JSONObject();
            body.put("adminUserId", result.getThirdUserId1());

            DataResponseBatchAsyncResult dataResponseBatchAsyncResult1 = CpSignUtil.openApiObject(CpConstants.F002, body, DataResponseBatchAsyncResult.class);
            if (dataResponseBatchAsyncResult1.getCode() != 0) {
                throw new WebException(101, "同步数据错误");
            }

            //重新绑定
            JSONObject body1 = new JSONObject();
            body1.put("adminUserId", result.getThirdUserId1());
            body1.put("authTeamId", account.getRoleGroupId());

            DataResponseBatchAsyncResult dataResponseBatchAsyncResult2 = CpSignUtil.openApiObject(CpConstants.F001, body1, DataResponseBatchAsyncResult.class);
            if (dataResponseBatchAsyncResult2.getCode() != 0) {
                throw new WebException(101, "同步数据错误");
            }


        }


        accountMapper.updateById(account);
    }

    @Override
    public void resetAccountPwd(String uid) {
        Account account = accountMapper.selectById(uid);
        if (Objects.isNull(account)) throw new RuntimeException("account not found");
        account.setPwd(CidUtil.encryption(defaultPwd));
        accountMapper.updateById(account);
    }

    @Override
    public String updateAccountPwd(UpdatePwdParam updatePwdParam) {
        String id = updatePwdParam.getId();
        if (!StringUtils.hasLength(id)) return "userId cannot be null";
        Account result = accountMapper.selectById(id);
        if (Objects.isNull(result)) return "account not found";
        String pwd = CidUtil.decryption(result.getPwd());
        if (!Objects.equals(pwd, updatePwdParam.getPwd())) return "password error";
        result.setPwd(CidUtil.encryption(updatePwdParam.getNewPwd()));
        accountMapper.updateById(result);
        return "success";
    }

    @Override
    public void insertNewAccount(AccountParam param) {
        //判断是否注册过
        QueryWrapper<Account> accountWrapper = new QueryWrapper<Account>();
        accountWrapper.eq("mobile", param.getMobile()).or().eq("account", param.getAccount());
        Account account = accountMapper.selectOne(accountWrapper);

        if (!Objects.isNull(account)) {
            throw new WebException(100, "用户手机号已经注册");
        } else {
            account = new Account();
        }


        BeanUtils.copyProperties(param, account);
        account.setPwd(CidUtil.encryption(defaultPwd));
        String value = RoleEnum.valueOf(param.getRoleStr()).getValue();
        String rolegroupid = RoleGroupIDEnum.valueOf(param.getRoleStr()).getValue();
        account.setRole(value);
        account.setRoleGroupId(rolegroupid);


        //同步到CP

        //同步用户信息  获取userid1
        JSONObject body = new JSONObject();
        body.put("mobilePhone", account.getMobile());
        body.put("name", account.getUserName());
        body.put("departmentId", "J25301");
        body.put("status", "NORMAL");
        body.put("authTeamId", account.getRoleGroupId());
        body.put("loginType", "mobilePhone");
        body.put("externalId", account.getMobile());
        body.put("externalTenantId", account.getMobile());
        JSONArray array = new JSONArray();
        array.add(body);
        JSONObject aPaasEntity = CpSignUtil.openApi(CpConstants.B002, array);
        JSONObject result = aPaasEntity.getJSONObject("data").getJSONObject("successfulIdMapping");
        String userID = "";
        try {
            userID = (String) result.entrySet().stream().findFirst().get().getValue();
            account.setThirdUserId1(userID);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebException(101, "创建用户失败");
        }

        //保存用户信息 获取userid2

        com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.vidyxcdq.auth.extension.AuthConfig authConfig2 = new com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.vidyxcdq.auth.extension.AuthConfig("PUubcmUDP5oiufUH", "UXXIo8Day0L0XysFaxJcaNRij0lhVw");
        authConfig2.setUserId(userID);

        UsertableApi usertableApi = new UsertableApi();
        SaveOrUpdateuserTableDTO saveOrUpdateuserTableDTO = new SaveOrUpdateuserTableDTO();
        UserTableDTOUpdate userTableDTOUpdate = new UserTableDTOUpdate();
        userTableDTOUpdate.setUserName(userID);
        userTableDTOUpdate.setName(account.getUserName());
        userTableDTOUpdate.setPhoneNumber(account.getMobile());

        saveOrUpdateuserTableDTO.setUpdate(userTableDTOUpdate);
        usertableApi.getApiClient().setVerifyingSsl(false);
        UserTableSaveOrUpdateDataResponseObject userTableSaveOrUpdateDataResponseObject = usertableApi.saveOrUpdateUserTableUsingPOST(saveOrUpdateuserTableDTO);

        if (userTableSaveOrUpdateDataResponseObject.getCode() != 0) {
            throw new WebException(101, "同步数据错误");
        }
        String userID2 = saveOrUpdateId(userTableSaveOrUpdateDataResponseObject);
        account.setThirdUserId2(userID2);


        //同步企业信息

        //1.查询企业是否存在

        com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.pdpwzyvy.auth.extension.AuthConfig enterAuthConfig = new com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.pdpwzyvy.auth.extension.AuthConfig("PUubcmUDP5oiufUH", "UXXIo8Day0L0XysFaxJcaNRij0lhVw");
        enterAuthConfig.setUserId(userID);
        EnterpriseinformationApi enterpriseinformationApi = new EnterpriseinformationApi();
        EnterpriseinformationDTO enterpriseinformationDTO = new EnterpriseinformationDTO();
        enterpriseinformationDTO.setName(account.getEnterpriseName());
        enterpriseinformationDTO.set$LikeQueryField("name");
        enterpriseinformationApi.getApiClient().setVerifyingSsl(false);
        com.bizcloud.ipaas.tb449ed34961d4e798e2dc60eba4e8b47.pdpwzyvy.ApiResponse<DataResponseListEnterpriseinformationDTO> enterprise = enterpriseinformationApi.findEnterpriseinformationUsingPOSTWithHttpInfo(enterpriseinformationDTO);

        //1.1 判断企业是否存在
        if (enterprise.getStatusCode() != 200) {
            throw new WebException(101, "同步数据错误");
        }
        String enterpriseID = "";
        List<EnterpriseinformationDTOResponse> enterpriseinformationDTOResponses = enterprise.getData().getData();
        //1.2 有 获取企业ID
        if (!enterpriseinformationDTOResponses.isEmpty()) {
            EnterpriseinformationDTOResponse e = enterpriseinformationDTOResponses.get(0);
            enterpriseID = e.getCompanyId().toString();
        } else {
            //1.3 没有 创建企业
            SaveOrUpdateEnterpriseinformationDTO saveOrUpdateEnterpriseinformationDTO = new SaveOrUpdateEnterpriseinformationDTO();
            EnterpriseinformationDTOUpdate enterpriseinformationDTOUpdate = new EnterpriseinformationDTOUpdate();
            enterpriseinformationDTOUpdate.setName(account.getEnterpriseName());
            saveOrUpdateEnterpriseinformationDTO.setUpdate(enterpriseinformationDTOUpdate);
            EnterpriseinformationSaveOrUpdateDataResponseObject enterpriseinformationSaveOrUpdateDataResponseObject = enterpriseinformationApi.saveOrUpdateEnterpriseinformationUsingPOST(saveOrUpdateEnterpriseinformationDTO);
            if (enterpriseinformationSaveOrUpdateDataResponseObject.getCode() != 0) {
                throw new WebException(101, "同步数据错误");
            }

            Object saveEnterpriseResult = enterpriseinformationSaveOrUpdateDataResponseObject.getData();
            if (!Objects.isNull(saveEnterpriseResult)) {
                if (saveEnterpriseResult instanceof List) {
                    List<Object> list = (List<Object>) saveEnterpriseResult;
                    if (list.size() > 0) {
                        Map obj = (Map) list.get(0);
                        enterpriseID = obj.get("saveDataId").toString();
                    } else {
                        throw new WebException(101, "同步数据错误");
                    }
                } else {
                    enterpriseID = saveOrUpdateId(saveEnterpriseResult);
                }
            } else {
                throw new WebException(101, "同步数据错误");
            }
        }


        //2. 绑定企业和用户
        AuthConfig authConfig = new AuthConfig("PUubcmUDP5oiufUH", "UXXIo8Day0L0XysFaxJcaNRij0lhVw");
        authConfig.setUserId(userID);
        SaveOrUpdateuserTableEnterpriseDTO dto = new SaveOrUpdateuserTableEnterpriseDTO();
        UserTableEnterpriseDTOUpdate dd = new UserTableEnterpriseDTOUpdate();
        dd.setEnterpriseName(enterpriseID);
        dd.setUserName(userID2);
        dd.setMainEnterprise("是");
        dto.setUpdate(dd);
        UsertableenterpriseApi usertableenterpriseApi = new UsertableenterpriseApi();
        usertableenterpriseApi.getApiClient().setVerifyingSsl(false);
        ApiResponse<UserTableEnterpriseSaveOrUpdateDataResponseObject> res = usertableenterpriseApi.saveOrUpdateUserTableEnterpriseUsingPOSTWithHttpInfo(dto);


        log.info("aPaasEntity:{}", res);


        accountMapper.insert(account);

    }

    /**
     * @param response aPaaS保存/更新响应值
     * @return 保存/更新的主键
     */
    private static String saveOrUpdateId(Object response) {
        return ofNullable(response).map(JSON::toJSONString).map(JSON::parseObject).filter(item -> 0 == item.getInteger("code")).map(item -> {
            TypeReference<List<JSONObject>> typeReference = new TypeReference<List<JSONObject>>() {
            };
            return item.<List<JSONObject>>getObject("data", typeReference);
        }).map(item -> item.get(0)).map(item -> {
            String saveDataId = item.getString("saveDataId");
            JSONArray updateDataIds = item.getJSONArray("updateIds");
            if (!CollectionUtils.isEmpty(updateDataIds)) {
                return updateDataIds.get(0).toString();
            }
            return saveDataId;
        }).orElse(null);
    }

    /**
     * descrption aPaaS登录
     *
     * @param account 用户对象
     * @return token
     */
    @Override
    public Object getCPLoginToken(Account account) {
        JSONObject body = new JSONObject();
        body.put("externalId", account.getMobile());
        body.put("loginType", "mobilePhone");

        DataResponseBatchAsyncResult dataResponseBatchAsyncResult = CpSignUtil.openApiObject(CpConstants.B006, body, DataResponseBatchAsyncResult.class);
        if (dataResponseBatchAsyncResult.getCode() != 0) {
            throw new WebException(101, "同步数据错误");
        }
        return dataResponseBatchAsyncResult.getData();
    }

}
