package com.diboot.component.msg.service.impl;

import com.diboot.component.msg.entity.Message;
import com.diboot.component.msg.mapper.MessageMapper;
import com.diboot.component.msg.service.MessageService;
import com.diboot.core.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
 * 消息相关service实现
 * @author:wangyl
 * */
@Service
@Slf4j
public class MessageServiceImpl extends BaseServiceImpl<MessageMapper, Message> implements MessageService {

}
