/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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

import com.diboot.iam.entity.IamClient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * open api登陆凭证
 *
 * @author uu
 * @version 3.5.1
 * @Date 2024/7/25
 */
@Getter@Setter
public class ClientCredential extends AuthCredential {
    @Serial
    private static final long serialVersionUID = 6419283916391871977L;

    public static final String AUTH_TYPE = "Client";

    @NotNull(message = "{validation.clientCredential.appKey.NotNull.message}")
    private String appKey;
    @NotNull(message = "{validation.clientCredential.appSecret.NotNull.message}")
    private String appSecret;

    public ClientCredential() {
        this.setAuthType(AUTH_TYPE);
        this.setUserTypeClass(IamClient.class);
    }

    public ClientCredential(String appKey, String appSecret) {
        this();
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    @Override
    public String getAuthAccount() {
        return this.appKey;
    }

    @Override
    public String getAuthSecret() {
        return appSecret;
    }
}
