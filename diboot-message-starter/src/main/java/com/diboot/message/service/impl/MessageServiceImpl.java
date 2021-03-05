package com.diboot.message.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.message.channel.ChannelStrategy;
import com.diboot.message.entity.BaseVariableData;
import com.diboot.message.entity.Message;
import com.diboot.message.entity.MessageTemplate;
import com.diboot.message.mapper.MessageMapper;
import com.diboot.message.service.MessageService;
import com.diboot.message.service.MessageTemplateService;
import com.diboot.message.service.TemplateVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    /**
     * 模版策略
     */
    @Autowired
    private TemplateVariableService templateVariableService;

    /**
     * 发送通道
     */
    @Autowired
    @Lazy
    private Map<String, ChannelStrategy> channelStrategyMap;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Override
    public boolean send(Message message, BaseVariableData variableData) throws Exception {
        MessageTemplate messageTemplate = messageTemplateService.getEntity(message.getTemplateId());
        if (V.isEmpty(messageTemplate)) {
            log.error("[获取模版失败]，模版id为：{}",  message.getTemplateId());
            throw new BusinessException(Status.FAIL_OPERATION, "获取模版失败!");
        }
        // 获取发送通道
        ChannelStrategy channelStrategy = channelStrategyMap.get(message.getChannel());
        if (V.isEmpty(channelStrategy)) {
            log.error("[获取发送通道失败]，当前发送通道为：{}", message.getChannel());
            throw new BusinessException(Status.FAIL_OPERATION, "获取发送通道失败!");
        }
        try {
            // 设置模版内容
            String content = templateVariableService.parseTemplate2Content(messageTemplate.getContent(), variableData);
            message.setContent(content);
        } catch (Exception e) {
            log.error("[消息解析失败]，消息体为：{}", message);
            throw new BusinessException(Status.FAIL_OPERATION, "消息解析失败!");
        }
        // 设置定时发送，则等待定时任务发送
        if (V.notEmpty(message.getScheduleTime())) {
            message.setStatus("SCHEDULE");
        }
        // 创建Message
        boolean success = createEntity(message);
        // 停止发送
        if (V.notEmpty(message.getScheduleTime())) {
            return true;
        }
        if (!success) {
            log.error("[消息创建失败]，消息体为：{}", message);
            throw new BusinessException(Status.FAIL_OPERATION, "消息发送失败！");
        }
        // 异步发送消息
        channelStrategy.send(message);
        return true;
    }
}
