package com.diboot.components.msg.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.diboot.components.msg.config.EmailConfig;
import com.diboot.components.msg.config.MsgCons;
import com.diboot.components.msg.entity.BaseMessage;
import com.diboot.components.msg.entity.EmailMessage;
import com.diboot.components.msg.entity.Message;
import com.diboot.components.msg.service.EmailSendService;
import com.diboot.core.config.Cons;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class EmailSendServiceImpl implements EmailSendService {
    private static final Logger logger = LoggerFactory.getLogger(EmailSendServiceImpl.class);

    @Autowired
    EmailConfig emailConfig;

    @Override
    public boolean send(BaseMessage message) throws Exception {
        EmailMessage emailMessage = (EmailMessage)message;
        if(logger.isDebugEnabled()){
            logger.debug("发送邮件开始, 收件人:" + message.getReceiver() + ", message=" + message.getContent());
        }
        if(emailConfig.isEmpty()){
            logger.warn("发送邮件相关配置为空，发送失败");
            return false;
        }
        // Create the email message
        HtmlEmail email = new HtmlEmail();
        email.setHostName(emailConfig.getHost());
        email.setCharset(Cons.CHARSET_UTF8);
        email.setSSLOnConnect(true);
        if(V.notEmpty(emailConfig.getSslport())){
            email.setSslSmtpPort(emailConfig.getSslport());
        }
        email.setAuthentication(emailConfig.getAddress(), emailConfig.getPassword());
        email.addTo(emailMessage.getReceiver(), emailMessage.getReceiverName());
        if(V.notEmpty(emailMessage.getCcEmails())){
            for(String cc : emailMessage.getCcEmails()){
                email.addCc(cc);
            }
        }
        email.setFrom(emailConfig.getAddress(), emailConfig.getName());
        email.setSubject(emailMessage.getTitle());
        // set the html message
        email.setHtmlMsg(message.getContent());

        // 发送附件
        if(V.notEmpty(emailMessage.getFilePaths())){
            for(String path : emailMessage.getFilePaths()){
                File file = new File(path);
                if(!file.exists()){
                    logger.warn("附件文件不存在，无法发送！path="+path);
                    return false;
                }
                EmailAttachment attachment = new EmailAttachment();
                attachment.setName(file.getName());
                attachment.setPath(path);
                attachment.setDisposition(EmailAttachment.ATTACHMENT);
                email.attach(attachment);
            }
        }

        // send the email
        email.send();

        if(logger.isDebugEnabled()){
            logger.debug("发送邮件完成, 收件人:" + message.getReceiver());
        }

        return true;
    }

    @Override
    public EmailMessage parseToSendMessage(Message message) {
        EmailMessage email = new EmailMessage();
        email.setReceiver(message.getReceiver());
        email.setReceiverName(message.getReceiver());
        email.setTitle(message.getTitle());
        email.setContent(message.getContent());
        JSONArray jsonArray = (JSONArray)message.getFromExt("filePaths");
        List<String> filePathList = JSONArray.parseArray(jsonArray.toString(), String.class);
        String[] filePaths =  filePathList.toArray(new String[0]);
        email.setFilePaths(filePaths);

        return email;
    }

    @Override
    public String getType() {
        return MsgCons.MSG_TYPE.EMAIL.name();
    }
}
