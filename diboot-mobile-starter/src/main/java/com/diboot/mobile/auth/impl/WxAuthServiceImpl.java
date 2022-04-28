/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.mobile.auth.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.iam.auth.impl.BaseAuthServiceImpl;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.mobile.dto.MobileCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信认证实现
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/8/31  14:08
 */
@Slf4j
@Service
public class WxAuthServiceImpl extends BaseAuthServiceImpl {

    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.WX_MP.name();
    }

    @Override
    protected Wrapper buildQueryWrapper(IamAuthToken iamAuthToken) {
        // 查询最新的记录
        LambdaQueryWrapper<IamAccount> queryWrapper = new LambdaQueryWrapper<IamAccount>()
                .select(IamAccount::getAuthAccount, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus)
                .eq(IamAccount::getUserType, iamAuthToken.getUserType())
                .eq(IamAccount::getAuthType, iamAuthToken.getAuthType())
                .eq(IamAccount::getAuthAccount, iamAuthToken.getAuthAccount())
                .orderByDesc(IamAccount::getId);
        return queryWrapper;
    }

    /**
     * 初始化JwtAuthToken实例
     * @param credential
     * @return
     */
    protected IamAuthToken initAuthToken(AuthCredential credential){
        MobileCredential wxMpCredential = (MobileCredential)credential;
        IamAuthToken token = new IamAuthToken(getAuthType(), wxMpCredential.getUserTypeClass());
        // 设置登陆的
        token.setAuthAccount(wxMpCredential.getAuthAccount());
        token.setRememberMe(wxMpCredential.isRememberMe());
        // 生成token
        return token.generateAuthtoken();
    }

}
