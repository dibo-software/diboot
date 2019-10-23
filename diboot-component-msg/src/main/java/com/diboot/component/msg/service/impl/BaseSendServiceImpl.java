package com.diboot.component.msg.service.impl;

import com.diboot.component.msg.entity.Message;
import com.diboot.component.msg.mapper.MessageMapper;
import com.diboot.component.msg.service.BaseSendService;
import com.diboot.core.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
 * 消息发送基础service
 * @author:wangyl
 * */
@Service
@Slf4j
public abstract class BaseSendServiceImpl extends BaseServiceImpl<MessageMapper, Message> implements BaseSendService {

    @Override
    public boolean createAndSendMsg(Message msg) throws Exception {
        boolean success = super.createEntity(msg);
        if(success){
            success = sendMsg(msg);
        }
        return success;
    }

}
