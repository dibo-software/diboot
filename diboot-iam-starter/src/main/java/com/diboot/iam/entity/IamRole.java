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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 角色 Entity定义
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_role")
public class IamRole extends BaseEntity<String> {
    private static final long serialVersionUID = -1186305888909118267L;

    public IamRole() {
    }

    public IamRole(String name, String code) {
        this.name = name;
        this.code = code;
    }

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    // 名称
    @NotNull(message = "{validation.iamRole.name.NotNull.message}")
    @Length(max = 50, message = "{validation.iamRole.name.Length.message}")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField()
    private String name;

    // 编码
    @NotNull(message = "{validation.iamRole.code.NotNull.message}")
    @Length(max = 50, message = "{validation.iamRole.code.Length.message}")
    @TableField()
    private String code;

    // 备注
    @TableField()
    private String description;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
