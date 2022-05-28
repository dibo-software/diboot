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
package com.diboot.message.service;

import com.diboot.core.service.BaseService;
import com.diboot.message.entity.Message;

/**
 * 消息相关Service
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
public interface MessageService extends BaseService<Message> {
    /**
     * 发送信息（消息模板无变量）
     *
     * @param message      {@link Message} 待发送的消息
     * @return
     */
    boolean send(Message message);

    /**
     * 发送信息
     *
     * @param message      {@link Message} 待发送的消息
     * @param variableData {@link Object} 变量替换值
     * @return
     */
    boolean send(Message message, Object variableData);
}