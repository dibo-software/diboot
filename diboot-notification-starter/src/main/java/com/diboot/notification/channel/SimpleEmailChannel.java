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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.notification.config.Cons;
import com.diboot.notification.entity.Message;
import com.diboot.notification.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.util.Date;

/**
 * 简单邮件发送通道
 * <p>
 * 只支持发送文本，其他自行扩展
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/20  15:45
 * @Copyright © diboot.com
 */
@Slf4j
public class SimpleEmailChannel implements MessageChannel {

    @Override
    public String type() {
        return Cons.MESSAGE_CHANNEL.EMAIL.name();
    }

    @Override
    @Async
    public void send(Message message) {
        log.debug("[开始发送邮件]：邮件内容：{}", JSON.stringify(message));
        String result = "success";
        String status = Cons.MESSAGE_STATUS.DELIVERY.name();
        JavaMailSender javaMailSender = ContextHolder.getBean(JavaMailSender.class);
        if(javaMailSender == null) {
            throw new InvalidUsageException("exception.invalidUsage.simpleEmailChannel.send.message");
        }
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            //发送有附件邮件
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 设置邮件主题
            messageHelper.setSubject(message.getTitle());
            // 设置邮件内容
            messageHelper.setText(message.getContent(), true);
            // 设置发送人
            messageHelper.setFrom(message.getSender());
            // 优先使用Receivers
            if (V.notEmpty(message.getReceivers())) {
                // 设置发送人
                messageHelper.setTo(message.getReceivers());
            } else {
                messageHelper.setTo(message.getReceiver());
            }
            // 设置抄送人
            messageHelper.setCc(message.getCcEmails());
            // 设置隐秘抄送人
            messageHelper.setBcc(message.getBccEmails());
            // 设置邮件发送日期
            messageHelper.setSentDate(new Date());
            //添加附件
            String[] fileUrlList = message.getAttachments();
            for (String url : fileUrlList) {
                FileSystemResource fileSystemResource = new FileSystemResource(url);
                messageHelper.addAttachment(MimeUtility.encodeWord(fileSystemResource.getFilename(), "utf-8", "B"), fileSystemResource);
            }
            //发送邮件
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("[发送邮件失败]：信息为： {} , 异常", message, e);
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
