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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;

/**
 * 用户角色关联 Entity定义
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_user_role")
public class IamUserRole extends BaseEntity<String> {
    private static final long serialVersionUID = 7716603553049083815L;

    public IamUserRole() {
    }

    public IamUserRole(String userType, String userId, String roleId) {
        this.userType = userType;
        this.userId = userId;
        this.roleId = roleId;
    }

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    // 用户类型
    @NotNull(message = "{validation.iamUserRole.userType.NotNull.message}")
    @TableField()
    private String userType;

    // 用户ID
    @NotNull(message = "{validation.iamUserRole.userId.NotNull.message}")
    @TableField()
    private String userId;

    // 角色ID
    @NotNull(message = "{validation.iamUserRole.roleId.NotNull.message}")
    @TableField()
    private String roleId;

}
