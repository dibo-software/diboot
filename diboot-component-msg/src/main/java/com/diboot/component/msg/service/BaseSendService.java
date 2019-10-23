package com.diboot.component.msg.service;

import com.diboot.component.msg.entity.Message;

/*
 * 消息发送基础service
 * @author:wangyl
 * */
public interface BaseSendService {

    /***
     * 保存并发送消息
     * @param msg
     * @return
     */
    boolean createAndSendMsg(Message msg) throws Exception;

    /***
     * 发送消息
     * @param msg
     * @return
     */
    boolean sendMsg(Message msg) throws Exception;

}
