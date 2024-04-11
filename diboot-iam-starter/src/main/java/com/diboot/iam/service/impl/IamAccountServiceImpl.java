/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.ChangePwdDTO;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.mapper.IamAccountMapper;
import com.diboot.iam.service.IamAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 认证用户相关Service实现
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
@Service
@Slf4j
public class IamAccountServiceImpl extends BaseServiceImpl<IamAccountMapper, IamAccount> implements IamAccountService {

    @Autowired(required = false)
    private IamCustomize iamCustomize;

    @Override
    public boolean createEntity(IamAccount iamAccount) {
        // 生成加密盐并加密
        if (V.notEmpty(iamAccount.getAuthSecret())) {
            iamCustomize.encryptPwd(iamAccount);
        }
        // 保存
        return super.createEntity(iamAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createEntities(List<IamAccount> accountList) {
        if (V.isEmpty(accountList)) {
            return true;
        }
        for (IamAccount account : accountList) {
            // 生成加密盐并加密
            if (V.notEmpty(account.getAuthSecret())) {
                iamCustomize.encryptPwd(account);
            }
        }
        return super.createEntities(accountList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changePwd(ChangePwdDTO changePwdDTO, IamAccount iamAccount) throws Exception {
        // 验证账号信息是否存在
        if (iamAccount == null) {
            throw new BusinessException(Status.FAIL_OPERATION, "账号信息不存在");
        }
        // 验证当前账号登录方式
        if (!Cons.DICTCODE_AUTH_TYPE.PWD.name().equals(iamAccount.getAuthType())) {
            throw new BusinessException(Status.FAIL_OPERATION, "该账号登录方式不支持更改密码");
        }
        // 验证密码一致性
        if (V.notEquals(changePwdDTO.getPassword(), changePwdDTO.getConfirmPassword())) {
            throw new BusinessException(Status.FAIL_OPERATION, "密码与确认密码不一致，请重新输入");
        }
        // 验证旧密码是否正确
        String oldAuthSecret = iamCustomize.encryptPwd(changePwdDTO.getOldPassword(), iamAccount.getSecretSalt());
        if (V.notEquals(oldAuthSecret, iamAccount.getAuthSecret())) {
            throw new BusinessException(Status.FAIL_OPERATION, "旧密码错误，请重新输入");
        }

        // 更改密码
        iamAccount.setAuthSecret(changePwdDTO.getPassword());
        iamCustomize.encryptPwd(iamAccount);
        boolean success = this.updateEntity(iamAccount);

        return success;
    }

    @Override
    public String getAuthAccount(String userType, String userId) {
        LambdaQueryWrapper<IamAccount> queryWrapper = new QueryWrapper<IamAccount>().lambda()
                .select(IamAccount::getAuthAccount)
                .eq(IamAccount::getUserType, userType)
                .eq(IamAccount::getUserId, userId);
        IamAccount account = getSingleEntity(queryWrapper);
        return account != null ? account.getAuthAccount() : null;
    }

    @Override
    public boolean isAccountExists(IamAccount iamAccount) {
        LambdaQueryWrapper<IamAccount> queryWrapper = new QueryWrapper<IamAccount>().lambda()
                .eq(IamAccount::getAuthAccount, iamAccount.getAuthAccount())
                .eq(IamAccount::getAuthType, iamAccount.getAuthType())
                .eq(IamAccount::getUserType, iamAccount.getUserType())
                .ne(V.notEmpty(iamAccount.getUserId()), IamAccount::getUserId, iamAccount.getUserId());
        return exists(queryWrapper);
    }

    @Override
    public boolean isAccountExists(String tenantId, String userType, String username, String userId) {
        return baseMapper.checkUsernameDuplicate(tenantId, userType, username, userId, BaseConfig.getActiveFlagValue());
    }

    @Override
    public boolean updateAccountStatus(String accountId, String status) {
        LambdaUpdateWrapper<IamAccount> updateWrapper = new UpdateWrapper<IamAccount>()
                .lambda().set(IamAccount::getStatus, status)
                .eq(IamAccount::getId, accountId);
        return super.update(updateWrapper);
    }

    /**
     * 判断账号是否存在
     *
     * @param iamAccount
     * @return
     */
    @Override
    public void beforeCreate(IamAccount iamAccount) {
        if (isAccountExists(iamAccount)) {
            String errorMsg = "账号 " + iamAccount.getAuthAccount() + " 已存在，请重新设置！";
            log.warn("保存账号异常: {}", errorMsg);
            throw new BusinessException(Status.FAIL_VALIDATION, errorMsg);
        }
    }

    /**
     * 判断账号是否存在
     *
     * @param iamAccount
     * @return
     */
    @Override
    public void beforeUpdate(IamAccount iamAccount) {
        beforeCreate(iamAccount);
    }

}
