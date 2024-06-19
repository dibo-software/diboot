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
package com.diboot.ai.models.kimi;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


/**
 * Kimi 返回结果
 * @author JerryMa
 * @version v3.4.0
 * @date 2024-04-28
 */
@Getter @Setter @Accessors(chain = true)
public class KimiChatResponse implements Serializable {
    private static final long serialVersionUID = 2438290388456986212L;

    /* 返回结果样例:
     * "id":"cmpl-02cfbdd536894440a0b216ba9ebc837e",
     * "object":"chat.completion",
     * "created":3273315,
     * "model":"moonshot-v1-8k",
     * "choices":[
     *      {"index":0,
     *      "message":{"role":"assistant","content":"你好，李雷！1+1等于2。这是一个基本的数学加法运算。"},
     *      "finish_reason":"stop"}
     *  ],
     *  "usage":{
     *      "prompt_tokens":19,"completion_tokens":31,"total_tokens":50
     *  }
     */
    private String id;

    private String object;

    private long created;

    private String model;

    private List<KimiChoice> choices;

    private KimiUsage usage;

}
