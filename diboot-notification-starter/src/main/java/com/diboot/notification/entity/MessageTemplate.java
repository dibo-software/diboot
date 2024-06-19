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
package com.diboot.notification.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息模版 Entity定义
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_message_template")
public class MessageTemplate extends BaseEntity<String> {
    private static final long serialVersionUID = 5255165821023367198L;

    /**
     * 租户id
     */
    @TableField()
    private String tenantId;

    /**
     * 应用模块
     */
    @Length(max = 50, message = "{validation.messageTemplate.appModule.Length.message}")
    @TableField()
    private String appModule;

    /**
     * 模版编码
     */
    @NotNull(message = "{validation.messageTemplate.code.NotNull.message}")
    @Length(max = 20, message = "{validation.messageTemplate.code.Length.message}")
    @TableField()
    private String code;

    /**
     * 模版标题
     */
    @NotNull(message = "{validation.messageTemplate.title.NotNull.message}")
    @Length(max = 100, message = "{validation.messageTemplate.title.Length.message}")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField()
    private String title;

    /**
     * 模版内容
     */
    @NotNull(message = "{validation.messageTemplate.content.NotNull.message}")
    @TableField()
    private String content;

    /**
     * 扩展数据
     */
    @TableField()
    private String extData;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 扩展字段Map
     */
    @TableField(exist = false)
    private Map<String, Object> extDataMap;

    public Map<String, Object> getExtDataMap() {
        return V.isEmpty(this.extData) ? new HashMap<>(16) : JSON.toMap(this.extData);
    }

    public void setExtDataMap(Map<String, Object> extDataMap) {
        if (V.isEmpty(extDataMap)) {
            extDataMap = new HashMap<>(16);
        }
        this.extDataMap = extDataMap;
        this.extData = JSON.stringify(extDataMap);
    }
}
