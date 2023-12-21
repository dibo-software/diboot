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
package com.diboot.tenant.dto;

import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.PwdCredential;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 登录凭证
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
/**
 * 租户登录凭证
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class TenantPwdCredential extends PwdCredential {
    private static final long serialVersionUID = -5020652642432896556L;

    public TenantPwdCredential(){
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    public TenantPwdCredential(String username, String password, String tenantCode){
        super(username, password);
        this.tenantCode = tenantCode;
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    @NotNull(message = "租户编码不能为空")
    private String tenantCode;

    @Override
    public String getAuthAccount() {
        return this.getUsername();
    }

    @Override
    public String getAuthSecret() {
        return this.getPassword();
    }
}
