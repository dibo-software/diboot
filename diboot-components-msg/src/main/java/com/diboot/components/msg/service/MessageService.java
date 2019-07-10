package com.diboot.components.msg.service;

import com.diboot.components.msg.entity.Message;
import com.diboot.core.service.BaseService;

public interface MessageService extends BaseService<Message> {

    /***
     * 保存并发送消息
     * @param msg
     * @return
     */
    boolean createAndSendMsg(Message msg);

    /***
     * 发送消息
     * @param msg
     * @return
     */
    boolean sendMsg(Message msg);

    /***
     * 根据消息类型获取对应的发送消息service
     */
    BaseSendService getSendService(String type);
}
