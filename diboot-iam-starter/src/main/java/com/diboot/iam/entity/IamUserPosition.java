/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户岗位关联 Entity定义
 *
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2019-12-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_user_position")
public class IamUserPosition extends BaseEntity<String> {
    private static final long serialVersionUID = -8470407660973877945L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    // 用户类型
    @NotNull(message = "{validation.iamUserPosition.userType.NotNull.message}")
    @Length(max = 100, message = "{validation.iamUserPosition.userType.Length.message}")
    @TableField()
    private String userType;

    // 用户ID
    @NotNull(message = "{validation.iamUserPosition.userId.NotNull.message}")
    @TableField()
    private String userId;

    // 组织ID
    @NotNull(message = "{validation.iamUserPosition.orgId.NotNull.message}")
    @TableField()
    private String orgId;

    // 岗位ID
    @NotNull(message = "{validation.iamUserPosition.positionId.NotNull.message}")
    @TableField()
    private String positionId;

    // 是否主岗
    @TableField()
    private Boolean isPrimaryPosition = true;

    // 更新时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
