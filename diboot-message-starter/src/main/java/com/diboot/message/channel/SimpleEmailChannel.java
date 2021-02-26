package com.diboot.message.channel;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.util.JSON;
import com.diboot.message.config.Cons;
import com.diboot.message.entity.Message;
import com.diboot.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

/**
 * 简单邮件发送通道
 * <p>
 * 只支持发送文本，其他自行扩展
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/20  15:45
 */
@Slf4j
public class SimpleEmailChannel implements ChannelStrategy {


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MessageService messageService;

    @Override
    @Async
    public void send(Message message) {
        log.debug("[开始发送邮件]：邮件内容：{}", JSON.stringify(message));
        String result = "success";
        String status = Cons.MESSAGE_STATUS.DELIVERY.getItemValue();
        try {
            // 构建一个邮件对象
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            // 设置邮件主题
            simpleMailMessage.setSubject(message.getTitle());
            // 设置发送人
            simpleMailMessage.setFrom(message.getSender());
            // 设置接受人
            simpleMailMessage.setTo(message.getReceivers());
            // 设置抄送人
            simpleMailMessage.setCc(message.getCcEmails());
            // 设置隐秘抄送人
            simpleMailMessage.setBcc(message.getBccEmails());
            // 设置邮件发送日期
            simpleMailMessage.setSentDate(new Date());
            // 设置邮件内容
            simpleMailMessage.setText(message.getContent());
            // 发送邮件
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            log.error("[发送邮件失败]：信息为： {} , 异常", message, e);
            result = e.getMessage();
            status = Cons.MESSAGE_STATUS.EXCEPTION.getItemValue();
        }
        // 更新结果
        messageService.updateEntity(
                Wrappers.<Message>lambdaUpdate()
                        .set(Message::getResult, result)
                        .set(Message::getStatus, status)
                        .eq(Message::getId, message.getId())
        );
    }
}
