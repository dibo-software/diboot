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

/**
 * Ai会话记录
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/1
 */

@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_ai_session_record")
public class AiSessionRecord extends BaseEntity<String> {
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

    @NotNull(message = "{validation.aiSessionRecord.sessionId.NotNull.message}")
    @Length(max = 32, message = "{validation.aiSessionRecord.sessionId.Length.message}")
    private String sessionId;

    @NotNull(message = "{validation.aiSessionRecord.model.NotNull.message}")
    @Length(max = 32, message = "{validation.aiSessionRecord.model.Length.message}")
    private String model;

    @NotNull(message = "{validation.aiSessionRecord.requestBody.NotNull.message}")
    private String requestBody;

    @NotNull(message = "{validation.aiSessionRecord.responseBody.NotNull.message}")
    private String responseBody;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT, insertStrategy = FieldStrategy.NOT_EMPTY)
    private String createBy;
}
