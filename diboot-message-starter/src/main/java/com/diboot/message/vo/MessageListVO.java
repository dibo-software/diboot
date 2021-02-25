package com.diboot.message.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.message.entity.Message;
import com.diboot.message.entity.MessageTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 消息 ListVO定义
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageListVO extends Message {

    private static final long serialVersionUID = -8747685560582177392L;

    /**
     * 关联字典：DICT_MESSAGE_CHANNEL
     */
    @BindDict(type = DICT_MESSAGE_CHANNEL, field = "channel")
    private String channelLabel;

    /**
     * 关联字典：DICT_MESSAGE_STATUS
     */
    @BindDict(type = DICT_MESSAGE_STATUS, field = "status")
    private String statusLabel;

    /**
     * 关联字段：MessageTemplate.title
     */
    @BindField(entity = MessageTemplate.class, field = "title", condition = "this.template_id=id")
    private String messageTemplateTitle;
}
