package com.diboot.component.msg.email.service;

import com.diboot.component.msg.email.utils.EmailUtil;
import com.diboot.component.msg.entity.Message;
import com.diboot.component.msg.service.impl.BaseSendServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSendServiceImpl extends BaseSendServiceImpl implements EmailSendService {
    private static final Logger logger = LoggerFactory.getLogger(EmailSendServiceImpl.class);

    @Override
    public boolean sendMsg(Message message) throws Exception {
        String to = message.getReceiver();
        String toName = String.valueOf(message.getFromExt("toName"));
        String title = message.getTitle();
        String content = message.getContent();
        String[] ccEmails = (String[])message.getFromExt("ccEmails");
        String[] filePaths = (String[])message.getFromExt("filePaths");
        return EmailUtil.send(to, toName, title, content, ccEmails, filePaths);
    }
}
