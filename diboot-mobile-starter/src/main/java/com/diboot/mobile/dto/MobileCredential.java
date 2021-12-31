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
package com.diboot.mobile.dto;

import com.diboot.iam.dto.AuthCredential;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 移动端登陆凭证
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/8/31  10:34
 */
@Getter
@Setter
@Accessors(chain = true)
public class MobileCredential extends AuthCredential {

    private static final long serialVersionUID = 7727490783637840611L;

    public MobileCredential() {
    }

    public MobileCredential(String authAccount) {
        this.authAccount = authAccount;
    }

    /**
     * 账号
     */
    private String authAccount;

    /**
     * 密码
     */
    private String authSecret;

    @Override
    public String getAuthAccount() {
        return this.authAccount;
    }

    @Override
    public String getAuthSecret() {
        return this.authSecret;
    }
}
