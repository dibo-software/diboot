/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.notification.channel.MessageChannel;
import com.diboot.notification.config.Cons;
import com.diboot.notification.entity.Message;
import com.diboot.notification.entity.MessageTemplate;
import com.diboot.notification.mapper.MessageMapper;
import com.diboot.notification.service.MessageService;
import com.diboot.notification.service.MessageTemplateService;
import com.diboot.notification.utils.TemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息相关Service实现
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
@Slf4j
public class MessageServiceImpl extends BaseServiceImpl<MessageMapper, Message> implements MessageService {

    @Lazy
    @Autowired
    private MessageTemplateService messageTemplateService;

    /**
     * 发送通道
     */
    private Map<String, MessageChannel> typeToChannelMap = new HashMap<>();

    public MessageServiceImpl(List<MessageChannel> messageChannels, List<Class<?>> variableObjectClasses) {
        if (messageChannels != null) {
            for (MessageChannel channel : messageChannels) {
                typeToChannelMap.put(channel.type(), channel);
            }
        }
        MessageTemplateServiceImpl.extractVariablesFrom(variableObjectClasses);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean send(Message message) {
        return send(message, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean send(Message message, Object variableData) {
        // 获取发送通道
        MessageChannel channel = typeToChannelMap.get(message.getChannel());
        if (V.isEmpty(channel)) {
            log.error("[获取发送通道失败]，当前发送通道为：{}", message.getChannel());
            throw new InvalidUsageException("获取发送通道失败! " + message.getChannel());
        }
        String content = message.getContent();
        if (message.hasTemplate()) {
            // 是否根据模板构建邮件内容
            LambdaQueryWrapper<MessageTemplate> queryWrapper = Wrappers.<MessageTemplate>lambdaQuery()
                    .eq(V.notEmpty(message.getTemplateId()), MessageTemplate::getId, message.getTemplateId())
                    .eq(V.notEmpty(message.getTemplateCode()), MessageTemplate::getCode, message.getTemplateCode());
            if (queryWrapper.nonEmptyOfNormal()) {
                MessageTemplate messageTemplate = messageTemplateService.getSingleEntity(queryWrapper);
                if (V.isEmpty(messageTemplate)) {
                    if (V.isEmpty(message.getTemplateCode())) {
                        log.error("[获取模版失败] 模版id为：{}", message.getTemplateId());
                    } else if (V.isEmpty(message.getTemplateId())) {
                        log.error("[获取模版失败] 模版code为：{}", message.getTemplateCode());
                    } else {
                        log.error("[获取模版失败] 模版id为：{} ，模版code为：{}", message.getTemplateId(), message.getTemplateCode());
                    }
                    throw new BusinessException(Status.FAIL_OPERATION, "获取模版失败!");
                }
                message.setTemplateId(messageTemplate.getId());
                content = messageTemplate.getContent();
            }
        }
        if (V.notEmpty(content)) {
            try {
                // 设置模版内容
                if (V.notEmpty(variableData)) {
                    content = TemplateUtils.parseTemplateContent(content, variableData);
                    message.setContent(content);
                    // 将变量内容存储到数据库
                    Map<String, Object> extDataMap = message.getExtDataMap();
                    extDataMap = V.isEmpty(extDataMap) ? new HashMap<>() : extDataMap;
                    extDataMap.put(Message.VARIABLES, variableData);
                    message.setExtDataMap(extDataMap);
                }
            } catch (Exception e) {
                log.error("[消息解析失败]，消息体为：{}", message);
                throw new BusinessException(Status.FAIL_OPERATION, "消息解析失败!");
            }
        } else {
            throw new BusinessException("消息内容不能为 null");
        }
        // 设置定时发送，则等待定时任务发送
        if (V.notEmpty(message.getScheduleTime())) {
            message.setStatus("SCHEDULE");
        } else if (V.isEmpty(message.getStatus())) {
            message.setStatus(Cons.MESSAGE_STATUS.PENDING.name());
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
        channel.send(message);
        return true;
    }

}
