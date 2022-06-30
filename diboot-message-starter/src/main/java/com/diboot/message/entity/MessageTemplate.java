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
package com.diboot.message.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
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
public class MessageTemplate extends BaseEntity {
    private static final long serialVersionUID = 5255165821023367198L;

    /**
     * 租户id
     */
    @TableField()
    private Long tenantId;

    /**
     * 应用模块
     */
    @Length(max = 50, message = "应用模块长度应小于50")
    @TableField()
    private String appModule;

    /**
     * 模版编码
     */
    @NotNull(message = "模版编码不能为空")
    @Length(max = 20, message = "模版编码长度应小于20")
    @TableField()
    private String code;

    /**
     * 模版标题
     */
    @NotNull(message = "模版标题不能为空")
    @Length(max = 100, message = "模版标题长度应小于100")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField()
    private String title;

    /**
     * 模版内容
     */
    @NotNull(message = "模版内容不能为空")
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
    private Long createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

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
