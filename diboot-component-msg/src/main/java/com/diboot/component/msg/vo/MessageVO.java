package com.diboot.component.msg.vo;

import com.diboot.component.msg.entity.Message;
import com.diboot.component.msg.entity.MessageTemplate;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntity;
import lombok.Data;

@Data
public class MessageVO extends Message {
    private static final long serialVersionUID = 141470955506938430L;

    @BindDict(type="MSG_TYPE", field="type")
    private String typeLabel;

    @BindDict(type="MSG_STATUS", field="status")
    private String statusLabel;

    @BindEntity(entity=MessageTemplate.class, condition="this.template_id=id")
    private MessageTemplate messageTemplate;
}
