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

import com.baomidou.mybatisplus.annotation.*;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
* 岗位 Entity定义
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_position")
public class IamPosition extends BaseEntity {
    private static final long serialVersionUID = 8716775927523689964L;
    /**
     * 全局通用的OrgId
     */
    public static final String GENERAL_ORG_ID = "0";

    /***
     * 最新岗位ID的KEY
     */
    public static final String LATEST_POSITION_ID_KEY = "latestPositionId";

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    // 名称
    @NotNull(message = "名称不能为空")
    @Length(max=100, message="名称长度应小于100")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField()
    private String name;

    // 编码
    @NotNull(message = "编码不能为空")
    @Length(max=50, message="编码长度应小于50")
    @TableField()
    private String code;

    // 是否虚拟岗
    @TableField()
    private Boolean isVirtual = false;

    // 职级
    @TableField()
    private String gradeName;

    // 职级
    @TableField()
    private String gradeValue;

    // 数据权限类型
    @TableField()
    private String dataPermissionType;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
