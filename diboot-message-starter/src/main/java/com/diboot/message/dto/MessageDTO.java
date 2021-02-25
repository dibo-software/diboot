package com.diboot.message.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.message.entity.Message;
import com.diboot.message.entity.MessageTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 消息 DTO定义
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageDTO extends Message {

    private static final long serialVersionUID = 9013608670040655788L;

    /**
     * 关联字段 MessageTemplate.title
     */
    @BindQuery(comparison = Comparison.EQ, entity = MessageTemplate.class, field = "title", condition = "this.template_id=id")
    private String messageTemplateTitle;
}
