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
package com.diboot.ai.models.qwen;

import com.diboot.ai.common.AiMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 通义千问请求
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/25
 */
@Getter@Setter@Accessors(chain = true)
public class QwenChatRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4779276706100882299L;

    /**
     * 指定用于对话的通义千问模型名:默认使用qwen-turbo
     */
    private String model = QwenEnum.Model.ALI_QWEN_TURBO.getCode();

    /**
     * 输入模型的信息
     */
    private Input input;

    /**
     * 模型生成的参数
     */
    private Parameters parameters = new Parameters();

    /**
     * 控制模型生成的参数
     */
    @Getter@Setter@Accessors(chain = true)
    public static class Parameters {
        /**
         * 用于指定返回结果的格式
         */
        @JsonProperty("result_format")
        private String resultFormat = "message";
        /**
         * 生成时使用的随机数种子
         */
        private Integer seed;
        /**
         * 用于限制模型生成token的数量
         */
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        /**
         * 生成时，核采样方法的概率阈值
         */
        @JsonProperty("top_p")
        private float topP = 0.8f;
        /**
         * 生成时，采样候选集的大小
         */
        @JsonProperty("top_k")
        private Integer topK;
        /**
         * 用于控制模型生成时的重复度。
         */
        @JsonProperty("repetition_penalty")
        private float repetitionPenalty = 1.1f;
        /**
         * 用于控制随机性和多样性的程度。
         *
         * 高的temperature值会降低概率分布的峰值，使得更多的低概率词被选择，生成结果更加多样化；而较低的temperature值则会增强概率分布的峰值，使得高概率词更容易被选择，生成结果更加确定
         */
        private float temperature = 0.85f;
        /**
         * 是否参考使用互联网搜索结果
         *
         * true：启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑“自行判断”是否使用互联网搜索结果。
         *
         * false（默认）：关闭互联网搜索。
         */
        @JsonProperty("enable_search")
        private boolean enableSearch = false;
        /**
         * 控制在流式输出模式下是否开启增量输出
         */
        @JsonProperty("incremental_output")
        private boolean incrementalOutput;
        /**
         * 用于指定可供模型调用的工具列表
         */
        private List tools;
    }

    /**
     * 通义千问 输入模型的信息
     *
     * 暂时只考虑messages
     */
    @Getter@Setter@Accessors(chain = true)
    public static class Input {

        /**
         * 表示用户与模型的对话历史
         */
        private List<AiMessage> messages;

    }
}
