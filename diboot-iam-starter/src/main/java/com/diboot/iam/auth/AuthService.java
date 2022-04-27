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
package com.diboot.iam.auth;

import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.util.TokenUtils;
import org.apache.shiro.authc.AuthenticationException;

/**
 * 账号认证的service
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/25
 */
public interface AuthService {

    /**
     * 获取认证类型
     * @return
     */
    String getAuthType();

    /**
     * 过期分钟数
     * @return
     */
    default int getExpiresInMinutes(){
        return TokenUtils.EXPIRES_IN_MINUTES;
    }

    /**
     * 获取用户
     * @param iamAuthToken
     * @return
     * @throws AuthenticationException
     */
    IamAccount getAccount(IamAuthToken iamAuthToken) throws AuthenticationException;

    /**
     * 申请Token
     * @param credential 登录凭证
     * @return token JWT Token
     */
    String applyToken(AuthCredential credential);

}
