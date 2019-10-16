package com.diboot.component.msg.email.utils;

import com.diboot.core.config.Cons;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.V;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class EmailUtil {
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    private static final String EMAIL_NAME = PropertiesUtils.get("email.name");// 发送方称呼
    private static final String EMAIL_ADDRESS = PropertiesUtils.get("email.address");// 发送方email地址
    private static final String EMAIL_PASSWORD = PropertiesUtils.get("email.password");// 发送方授权码
    private static final String EMAIL_HOST = PropertiesUtils.get("email.host");//发送方smptHost
    private static final String EMAIL_SSLPORT = PropertiesUtils.get("email.sslport");// 发送端口

    /*
    * 邮件发送
    * */
    public static boolean send(String to, String toName, String title, String content, String[] ccEmails, String... filePaths) throws Exception{
        if(logger.isDebugEnabled()){
            logger.debug(">>>发送邮件开始, 收件人:" + to);
        }
        // Create the email message
        HtmlEmail email = new HtmlEmail();
        email.setHostName(EMAIL_HOST);
        email.setCharset(Cons.CHARSET_UTF8);
        email.setSSLOnConnect(true);
        if(V.notEmpty(EMAIL_SSLPORT)){
            email.setSslSmtpPort(EMAIL_SSLPORT);
        }
        email.setAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
        email.addTo(to, toName);
        if(V.notEmpty(ccEmails)){
            for(String cc : ccEmails){
                email.addCc(cc);
            }
        }
        email.setFrom(EMAIL_ADDRESS, EMAIL_NAME);
        email.setSubject(title);
        // set the html message
        email.setHtmlMsg(content);
        // 发送附件
        if(V.notEmpty(filePaths)){
            for(String path : filePaths){
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
            logger.debug("<<<发送邮件结束");
        }

        return true;
    }

}
