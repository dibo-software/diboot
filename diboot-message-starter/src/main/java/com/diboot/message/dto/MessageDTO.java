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
package com.diboot.message.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.message.entity.Message;
import com.diboot.message.entity.MessageTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 消息 DTO定义
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageDTO extends Message {

    private static final long serialVersionUID = 9013608670040655788L;

    /**
     * 标题
     */
    @BindQuery(comparison = Comparison.LIKE)
    private String title;

    /**
     * 关联字段 MessageTemplate.title
     */
    @BindQuery(comparison = Comparison.LIKE, entity = MessageTemplate.class, column = "title", condition = "this.template_id=id")
    private String messageTemplateTitle;
}
