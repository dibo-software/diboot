/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 系统配置 Entity定义
 *
 * @author wind
 * @version v2.5.0
 * @date 2022-01-13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_system_config")
public class SystemConfig extends BaseEntity<String> {
    private static final long serialVersionUID = 2862339898530606166L;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 类别
     */
    private String category;

    /**
     * 属性名
     */
    private String propKey;

    /**
     * 属性值
     */
    private String propValue;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
