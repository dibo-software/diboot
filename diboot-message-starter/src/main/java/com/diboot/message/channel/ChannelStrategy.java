package com.diboot.message.channel;


import com.diboot.message.entity.Message;
import com.diboot.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 通道策略接口
 *
 * <p>
 * 所有发送通道实现该接口，并实现发送方法
 * </p>
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/18  18:42
 */
public interface ChannelStrategy {

    /**
     * 发送消息， 并更新发送结果
     *
     * @param message
     * @return
     * @throws Exception
     */
    void send(Message message);
}
