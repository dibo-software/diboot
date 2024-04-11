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
package com.diboot.notification.channel;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.notification.config.Cons;
import com.diboot.notification.entity.Message;
import com.diboot.notification.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

/**
 * 阿里云短信发送
 *
 * @author : uu
 * @version : v3.3
 * @Date 2024/3/19
 * @Copyright © diboot.com
 */
@Slf4j
public class AliyunSmsChannel implements MessageChannel {

    @Override
    public String type() {
        return Cons.MESSAGE_CHANNEL.SMS.name();
    }

    @Override
    @Async
    public void send(Message message) {
        log.debug("[开始发送短信]：短信内容：{}", JSON.stringify(message));
        Client client = ContextHolder.getBean(Client.class);
        // 构建短信请求体
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(message.getSender()) // 短信签名
                .setTemplateCode(message.getTemplateCode())
                .setPhoneNumbers(message.getReceiver())
                .setTemplateParam(message.getVariables());
        String result = "success";
        String status = Cons.MESSAGE_STATUS.DELIVERY.name();
        try {
            // 发送短信
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            // 发送结果失败时记录数据
            if (!"OK".equals(sendSmsResponse.getBody().getCode())) {
                result = sendSmsResponse.getBody().getCode() + "：" + sendSmsResponse.getBody().message;
            }
        } catch (Exception e) {
            log.error("[发送短信失败]：信息为： {} , 异常", message, e);
            result = e.getMessage();
            status = Cons.MESSAGE_STATUS.FAILED.name();
        }
        // 更新结果
        ContextHolder.getBean(MessageService.class).updateEntity(
                Wrappers.<Message>lambdaUpdate()
                        .set(Message::getResult, result)
                        .set(Message::getStatus, status)
                        .eq(Message::getId, message.getId())
        );
    }
}
