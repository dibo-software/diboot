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
 * 角色权限关联 Entity定义
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_role_resource")
public class IamRoleResource extends BaseEntity<String> {
    private static final long serialVersionUID = -8228772361638435896L;

    public IamRoleResource() {
    }

    public IamRoleResource(String roleId, String resourceId) {
        this.roleId = roleId;
        this.resourceId = resourceId;
    }

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    // 角色ID
    @NotNull(message = "{validation.iamRoleResource.roleId.NotNull.message}")
    @TableField()
    private String roleId;

    // 权限ID
    @NotNull(message = "{validation.iamRoleResource.resourceId.NotNull.message}")
    @TableField()
    private String resourceId;


}
