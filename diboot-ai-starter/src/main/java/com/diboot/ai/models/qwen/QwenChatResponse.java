
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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 通义千问响应
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/25
 */
@Getter
@Setter
@Accessors(chain = true)
public class QwenChatResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 3484634300435327268L;

    /**
     * 本次请求的系统唯一码
     */
    @JsonProperty("request_id")
    private String requestId;

    /**
     * 结果
     */
    private QwenOutput output;

    /**
     * 本次调用使用的token信息
     */
    private QwenUsage usage;

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class QwenOutput {
        /**
         * 模型输出的内容。当result_format设置为text时返回该字段。
         */
        private String text;

        /**
         * 有三种情况：
         * 正在生成时为null，
         * 生成结束时如果由于停止token导致则为stop，
         * 生成结束时如果因为生成长度过长导致则为length。
         * <p>
         * 当result_format设置为text时返回该字段。
         */
        @JsonProperty("finish_reason")
        private String finishReason;

        /**
         * 当result_format设置为message时返回该字段。
         */
        private List<QwenChoice> choices;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class QwenUsage {
        /**
         * 模型输出内容的 token 个数。
         */
        @JsonProperty("output_tokens")
        private String outputTokens;
        /**
         * 本次请求输入内容的 token 个数。
         * 在enable_search设置为true时，输入的 token 数目由于需要添加搜索相关内容，因此会比您在请求中的输入 token 个数多。
         */
        @JsonProperty("input_tokens")
        private String inputTokens;
        /**
         * output_tokens与input_tokens的总和。
         */
        @JsonProperty("total_tokens")
        private String totalTokens;
    }

}
