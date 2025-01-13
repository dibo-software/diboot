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
package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.Set;

/**
 * 客户端配置
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "dbt_client", autoResultMap = true)
public class Client extends BaseLoginUser {
    @Serial
    private static final long serialVersionUID = 8928160569300882231L;

    private String tenantId;

    private String name;

    @NotNull(message = "{validation.clientCredential.appKey.NotNull.message}")
    private String appKey;

    @NotNull(message = "{validation.clientCredential.appSecret.NotNull.message}")
    private String appSecret;

    private String status;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> permissions;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getUserType() {
        return Client.class.getSimpleName();
    }
}
