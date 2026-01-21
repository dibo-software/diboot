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
package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Jackson3TypeHandler;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 三方客户端
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "dbt_iam_client", autoResultMap = true)
public class IamClient extends BaseLoginUser {
    @Serial
    private static final long serialVersionUID = 8928160569300882231L;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 名称
     */
    private String name;

    /**
     * 应用Key
     */
    @NotNull(message = "{validation.clientCredential.appKey.NotNull.message}")
    @BindQuery(comparison = Comparison.LIKE)
    private String appKey;

    /**
     * 应用密钥
     */
    @NotNull(message = "{validation.clientCredential.appSecret.NotNull.message}")
    private String appSecret;

    /**
     * 状态
     */
    private String status;

    /**
     * 权限
     */
    @TableField(typeHandler = Jackson3TypeHandler.class)
    private Set<String> permissions;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getUserType() {
        return IamClient.class.getSimpleName();
    }
}
