/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.message.channel;


import com.diboot.message.entity.Message;

/**
 * 消息通道接口
 *
 * <p>
 * 所有发送通道实现该接口，并实现发送方法
 * </p>
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/18  18:42
 * @Copyright © diboot.com
 */
public interface MessageChannel {

    /**
     * 消息通道类型，如Email
     * @return
     */
    String type();

    /**
     * 发送消息， 并更新发送结果
     *
     * @param message
     * @return
     * @throws Exception
     */
    void send(Message message);
}
