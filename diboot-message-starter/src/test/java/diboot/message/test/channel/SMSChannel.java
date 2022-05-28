package diboot.message.test.channel;

import com.diboot.message.channel.MessageChannel;
import com.diboot.message.config.Cons;
import com.diboot.message.entity.Message;

/**
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/28
 * Copyright © diboot.com
 */
public class SMSChannel implements MessageChannel {

    @Override
    public String type() {
        return Cons.MESSAGE_CHANNEL.SMS.name();
    }

    @Override
    public void send(Message message) {
        System.out.println("SMSChannel 发送短信: "+ message.getContent());
    }
}
