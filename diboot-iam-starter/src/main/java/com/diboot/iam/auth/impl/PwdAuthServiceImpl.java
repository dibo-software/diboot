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
package com.diboot.iam.auth.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.util.Encryptor;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * 用户名密码认证的service实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/25
 */
@Service
@Slf4j
public class PwdAuthServiceImpl extends BaseAuthServiceImpl {
    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.PWD.name();
    }

    /**
     * 构建查询条件
     * @param iamAuthToken
     * @return
     */
    @Override
    protected Wrapper buildQueryWrapper(IamAuthToken iamAuthToken) {
        // 查询最新的记录
        LambdaQueryWrapper<IamAccount> queryWrapper = new LambdaQueryWrapper<IamAccount>()
                .select(IamAccount::getAuthAccount, IamAccount::getAuthSecret, IamAccount::getSecretSalt, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus)
                .eq(IamAccount::getUserType, iamAuthToken.getUserType())
                .eq(IamAccount::getAuthType, iamAuthToken.getAuthType())
                .eq(IamAccount::getAuthAccount, iamAuthToken.getAuthAccount())
                .eq(IamAccount::getTenantId, iamAuthToken.getTenantId())
                .orderByDesc(IamAccount::getId);
        return queryWrapper;
    }

    @Override
    public IamAccount getAccount(IamAuthToken iamAuthToken) throws AuthenticationException {
        IamAccount latestAccount = super.getAccount(iamAuthToken);
        // 如果需要密码校验，那么无状态的时候不需要验证
        if (iamAuthToken.isValidPassword() && isPasswordMatched(latestAccount, iamAuthToken) == false){
            throw new AuthenticationException("用户名或密码错误! account="+iamAuthToken.getAuthAccount());
        }
        return latestAccount;
    }

    /**
     * 用户名密码是否一致
     * @param account
     * @param jwtToken
     * @return
     */
    private static boolean isPasswordMatched(IamAccount account, IamAuthToken jwtToken){
        //加密后比较
        String encryptedStr = IamSecurityUtils.encryptPwd(jwtToken.getAuthSecret(), account.getSecretSalt());
        // 暂时兼容RC2版本，后期移除
        String oldEncryptedStr = Encryptor.encrypt(jwtToken.getAuthSecret(), account.getSecretSalt());
        return encryptedStr.equals(account.getAuthSecret()) || oldEncryptedStr.equals(account.getAuthSecret());
    }

}
