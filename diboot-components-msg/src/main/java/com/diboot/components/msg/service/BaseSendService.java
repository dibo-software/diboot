package com.diboot.components.msg.service;

import com.diboot.components.msg.entity.BaseMessage;
import com.diboot.components.msg.entity.Message;

public interface BaseSendService {

    //发送消息
    boolean send(BaseMessage message) throws Exception;

    //转化为可发送的消息对象
    BaseMessage parseToSendMessage(Message message);

    //获取发送消息类型(邮件，短信，微信等类型)
    String getType();

}
