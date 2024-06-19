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
package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 国际化配置
 *
 * @author wind
 * @version v3.0.0
 * @date 2022-10-12
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_i18n_config")
public class I18nConfig extends BaseEntity<String> {
    private static final long serialVersionUID = 11501L;

    /**
     * type字段的关联字典
     */
    public static final String DICT_I18N_TYPE = "I18N_TYPE";

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    /**
     * 类型
     */
    private String type;

    /**
     * 语言
     */
    @NotNull
    private String language;

    /**
     * 资源标识
     */
    @NotNull
    private String code;
    /**
     * 内容
     */
    @NotNull
    private String content;

}
