package com.diboot.components.msg.service.impl;

import com.diboot.components.msg.entity.BaseMessage;
import com.diboot.components.msg.entity.Message;
import com.diboot.components.msg.mapper.MessageMapper;
import com.diboot.components.msg.service.BaseSendService;
import com.diboot.components.msg.service.MessageService;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MessageServiceImpl extends BaseServiceImpl<MessageMapper, Message> implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private Map<String, BaseSendService> sendServiceMap;

    @Override
    public boolean createAndSendMsg(Message msg) {
        boolean success = super.createEntity(msg);
        if(success){
            success = sendMsg(msg);
        }
        return success;
    }

    @Override
    public boolean sendMsg(Message msg) {
        boolean success = false;
        try {
            //根据消息类型获取发送消息service
            BaseSendService sendService = getSendService(msg.getType());
            if(V.isEmpty(sendService)){
                logger.warn("没获取到相应发送消息的service");
                return false;
            }
            //将Message对象转化为可发送的消息对象
            BaseMessage baseMessage = sendService.parseToSendMessage(msg);
            //发送消息
            success = sendService.send(baseMessage);
        } catch (Exception e) {
            logger.warn("消息发送失败", e);
            return false;
        }
        return success;
    }

    @Override
    public BaseSendService getSendService(String type) {
        if(V.notEmpty(sendServiceMap)){
            for(BaseSendService sendService : sendServiceMap.values()){
                if(type.equals(sendService.getType())){
                    return sendService;
                }
            }
        }
        return null;
    }
}
