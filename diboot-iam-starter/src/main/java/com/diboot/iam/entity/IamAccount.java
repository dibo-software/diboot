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
package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.entity.BaseModel;
import com.diboot.iam.config.Cons;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 认证用户 Entity定义
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
@Getter @Setter @Accessors(chain = true)
@TableName("dbt_iam_account")
public class IamAccount extends BaseEntity<String> {
    private static final long serialVersionUID = -6825516429612507644L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    // 用户类型
    @NotNull(message = "{validation.iamAccount.userType.NotNull.message}")
    @Length(max = 100, message = "{validation.iamAccount.userType.Length.message}")
    @TableField()
    private String userType;

    // 用户ID
    @NotNull(message = "{validation.iamAccount.userId.NotNull.message}")
    @TableField()
    private String userId;

    // 认证方式
    @NotNull(message = "{validation.iamAccount.authType.NotNull.message}")
    @Length(max = 20, message = "{validation.iamAccount.authType.Length.message}")
    @TableField()
    private String authType = Cons.DICTCODE_AUTH_TYPE.PWD.name();

    // 用户名
    @NotNull(message = "{validation.iamAccount.authAccount.NotNull.message}")
    @Length(max = 100, message = "{validation.iamAccount.authAccount.Length.message}")
    @TableField()
    private String authAccount;

    // 密码
    @JsonIgnore
    @Length(max = 32, message = "{validation.iamAccount.authSecret.Length.message}")
    @TableField()
    private String authSecret;

    // 加密盐
    @JsonIgnore
    @Length(max = 32, message = "{validation.iamAccount.secretSalt.Length.message}")
    @TableField()
    private String secretSalt;

    // 加密盐
    @Length(max = 10, message = "{validation.iamAccount.status.Length.message}")
    @TableField()
    private String status;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
