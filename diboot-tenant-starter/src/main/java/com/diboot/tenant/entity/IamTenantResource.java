/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;

/**
 * 租户资源 Entity定义
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_tenant_resource")
public class IamTenantResource extends BaseEntity<String> {
    private static final long serialVersionUID = -7104747524539861379L;

    /**
     * 租户ID
     */
    @NotNull(message = "{validation.iamTenantResource.tenantId.NotNull.message}")
    @TableField()
    private String tenantId;

    /**
     * 资源ID
     */
    @NotNull(message = "{validation.iamTenantResource.resourceId.NotNull.message}")
    @TableField()
    private String resourceId;

}
