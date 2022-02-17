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
package com.diboot.iam.dto;

import com.diboot.iam.config.Cons;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 登录凭证
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/02/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class OAuth2SSOCredential extends AuthCredential {
    private static final long serialVersionUID = -5020652662632896556L;

    /**
     * 授权码
     */
    private String code;
    /**
     * 账户
     */
    private String authAccount;

    public OAuth2SSOCredential() {
        this.setAuthType(Cons.DICTCODE_AUTH_TYPE.SSO.name());
    }

    @Override
    public String getAuthAccount() {
        return this.authAccount;
    }

    @Override
    public String getAuthSecret() {
        return null;
    }

}
