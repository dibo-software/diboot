package com.diboot.components.msg.entity;

import lombok.Data;

/*
* 消息基类，之后的邮件，短信等实现时都需要需要继承此类
* 并在此基础上扩展
* */
@Data
public class BaseMessage {

    //接收人
    private String receiver;

    //消息内容
    private String content;

}
