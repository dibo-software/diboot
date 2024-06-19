/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * Ai会话
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/1
 */

@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_ai_session")
public class AiSession extends BaseEntity<String> {
    @Serial
    private static final long serialVersionUID = -227354974840875467L;

    /**
     * 主键类型为String型UUID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 租户ID
     */
    @JsonIgnore
    private String tenantId;

    @NotNull(message = "{validation.aiSession.title.NotNull.message}")
    @Length(max = 100, message = "{validation.aiSession.title.Length.message}")
    @NotNull
    private String title;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT, insertStrategy = FieldStrategy.NOT_EMPTY)
    private String createBy;
}
