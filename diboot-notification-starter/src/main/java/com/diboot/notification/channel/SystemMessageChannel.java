/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.util.ContextHolder;
import com.diboot.notification.config.Cons;
import com.diboot.notification.entity.Message;
import com.diboot.notification.service.MessageService;

/**
 * 系统消息通道
 *
 * @author : wind
 * @version : v2.7.0
 * @Date 2022/08/16  15:55
 */
public class SystemMessageChannel implements MessageChannel {

    @Override
    public String type() {
        return Cons.MESSAGE_CHANNEL.SYS_MSG.name();
    }

    @Override
    public void send(Message message) {
        if (Cons.MESSAGE_STATUS.DELIVERY.name().equals(message.getStatus())) {
            return;
        }
        String result = "success";
        String status = Cons.MESSAGE_STATUS.DELIVERY.name();
        // 更新结果
        ContextHolder.getBean(MessageService.class).updateEntity(
                Wrappers.<Message>lambdaUpdate()
                        .set(Message::getResult, result)
                        .set(Message::getStatus, status)
                        .eq(Message::getId, message.getId())
        );
    }

}
