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
package com.diboot.iam.dto;

import com.diboot.iam.config.Cons;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;

/**
 * 登录凭证
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class PwdCredential extends AuthCredential {
    private static final long serialVersionUID = -5020652642432896556L;

    public PwdCredential(){
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    public PwdCredential(String username, String password){
        this.username = username;
        this.password = password;
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    // 用户名，同authAccount
    @NotNull(message = "{validation.pwdCredential.username.NotNull.message}")
    private String username;

    // 密码
    @NotNull(message = "{validation.pwdCredential.password.NotNull.message}")
    private String password;

    // 登录的验证码
    private String captcha;

    @Override
    public String getAuthAccount() {
        return this.username;
    }

    @Override
    public String getAuthSecret() {
        return this.password;
    }
}
