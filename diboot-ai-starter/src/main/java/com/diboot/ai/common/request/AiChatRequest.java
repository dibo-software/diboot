/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.ai.common.request;

import com.diboot.ai.common.AiMessage;
import com.diboot.ai.models.qwen.QwenEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 对话请求
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/26
 */
@Getter
@Setter
public class AiChatRequest implements AiRequest {

    /**
     * 对话消息
     */
    private List<AiMessage> messages;

    /**
     * 对话模型
     */
    String model;

}
