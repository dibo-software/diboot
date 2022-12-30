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
package com.diboot.notification.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.notification.entity.MessageTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 消息模版 DTO定义
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageTemplateDTO extends MessageTemplate {

    private static final long serialVersionUID = 4601855668034533381L;

    /**
     * 创建时间-起始
     */
    @BindQuery(comparison = Comparison.GE, column = "create_time")
    private LocalDateTime createTime;

    /**
     * 创建时间-截止
     */
    @BindQuery(comparison = Comparison.LT, column = "create_time")
    private LocalDateTime createTimeEnd;

    public MessageTemplateDTO setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        this.createTimeEnd = createTime != null? createTime.plusDays(1) : null;
        return this;
    }

}
