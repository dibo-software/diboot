package com.diboot.message.channel;


import com.diboot.message.entity.Message;
import org.springframework.scheduling.annotation.Async;

/**
 * 通道策略接口
 *
 * <p>
 *     所有发送通道实现该接口，并实现发送方法
 * </p>
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/18  18:42
 */
public interface ChannelStrategy {

    /**
     * 发送消息，并设置消息的返回值
     *
     * @param message
     * @return
     * @throws Exception
     */
    String send(Message message) throws Exception;
}
