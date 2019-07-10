package com.diboot.components.msg.service.impl;

import com.diboot.components.msg.entity.MessageTemplate;
import com.diboot.components.msg.mapper.MessageTemplateMapper;
import com.diboot.components.msg.service.MessageTemplateService;
import com.diboot.core.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageTemplateServiceImpl extends BaseServiceImpl<MessageTemplateMapper, MessageTemplate> implements MessageTemplateService {
}
