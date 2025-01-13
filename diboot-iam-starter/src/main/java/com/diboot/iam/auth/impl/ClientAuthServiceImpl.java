/*
 * Copyright (c) 2015-2025, www.dibo.ltd (service@dibo.ltd).
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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.util.V;
import com.diboot.iam.dto.ClientCredential;
import com.diboot.iam.entity.Client;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.service.ClientService;
import com.diboot.iam.shiro.IamAuthToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Client认证的service实现
 *
 * @author uu
 * @version 3.5.1
 * @Date 2024/7/25
 */
@Service
@Slf4j
public class ClientAuthServiceImpl extends BaseAuthServiceImpl {

    @Autowired
    private ClientService clientService;

    @Override
    public String getAuthType() {
        return ClientCredential.AUTH_TYPE;
    }

    /**
     * 构建查询条件
     *
     * @param iamAuthToken
     * @return
     */
    @Override
    protected Wrapper<?> buildQueryWrapper(IamAuthToken iamAuthToken) {
        return Wrappers.<Client>lambdaQuery().eq(Client::getAppKey, iamAuthToken.getAuthAccount());
    }

    @Override
    public IamAccount getAccount(IamAuthToken iamAuthToken) throws AuthenticationException {
        Client client = clientService.getSingleEntity(buildQueryWrapper(iamAuthToken));
        if (client == null) {
            throw new AuthenticationException("clientId或clientSecret错误!");
        }
        if (V.notEquals("A", client.getStatus())) {
            throw new AuthenticationException("访客已失效!");
        }
        if (iamAuthToken.isValidPassword() && !isVisitorSecretMatched(client, iamAuthToken)) {
            throw new AuthenticationException("clientId或clientSecret错误!");
        }
        return new IamAccount().setUserId(client.getId());
    }

    /**
     * secret 是否一致
     *
     * @param client
     * @param authToken
     * @return
     */
    private static boolean isVisitorSecretMatched(Client client, IamAuthToken authToken) {
        return authToken.getAuthSecret().equals(client.getAppSecret());
    }

}
