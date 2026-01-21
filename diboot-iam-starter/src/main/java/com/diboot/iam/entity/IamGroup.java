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
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户组
 *
 * @version 3.6.1
 * @date 2025/05/28
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "dbt_iam_group",autoResultMap = true)
public class IamGroup extends BaseEntity<String>{

    @Serial
    private static final long serialVersionUID = 8616365923523689962L;

    /**
     * 租户ID
     */
    @JsonIgnore
    private String tenantId;

    /**
     * 名称
     */
    @BindQuery(comparison = Comparison.LIKE)
    @NotNull(message = "{validation.iamGroup.name.NotNull.message}")
    @Length(max = 100, message = "{validation.iamGroup.name.Length.message}")
    private String name;

    /**
     * 组织ID
     */
    private String orgId;

    /**
     * 负责人ID
     */
    @TableField()
    private String managerId;

    /**
     * 成员
     */
    @NotNull(message = "{validation.iamGroup.members.NotNull.message}")
    @TableField(typeHandler = Jackson3TypeHandler.class)
    @BindQuery(comparison = Comparison.CONTAINS)
    private List<String> members;

    /**
     * 备注
     */
    private String description;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
