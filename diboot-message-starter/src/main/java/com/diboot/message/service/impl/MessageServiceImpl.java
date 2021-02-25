package com.diboot.message.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.message.entity.Message;
import com.diboot.message.mapper.MessageMapper;
import com.diboot.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息相关Service实现
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 */
@Service
@Slf4j
public class MessageServiceImpl extends BaseServiceImpl<MessageMapper, Message> implements MessageService {

}
