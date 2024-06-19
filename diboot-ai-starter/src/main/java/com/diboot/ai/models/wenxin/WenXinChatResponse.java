
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
package com.diboot.ai.models.wenxin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 百度千帆响应
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/7
 */
@Getter
@Setter
@Accessors(chain = true)
public class WenXinChatResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -778719184742395646L;

    /**
     * 本轮对话的id
     */
    private String id;

    /**
     * 回包类型
     * chat.completion：多轮对话返回
     */
    private String object;

    /**
     * 时间戳
     */
    private Long created;

    /**
     * 表示当前子句的序号。只有在流式接口模式下会返回该字段
     */
    @JsonProperty("sentence_id")
    private Long sentenceId;

    /**
     * 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段
     */
    @JsonProperty("is_end")
    private Boolean isEnd;

    /**
     * 当前生成的结果是否被截断
     */
    @JsonProperty("is_truncated")
    private Boolean isTruncated;

    /**
     * 输出内容标识
     * <p>
     * · normal：输出内容完全由大模型生成，未触发截断、替换
     * · stop：输出结果命中入参stop中指定的字段后被截断
     * · length：达到了最大的token数，根据EB返回结果is_truncated来截断
     * · content_filter：输出内容被截断、兜底、替换为**等
     */
    @JsonProperty("finish_reason")
    private String finishReason;

    /**
     * 搜索数据，当请求参数enable_citation或enable_trace为true，并且触发搜索时，会返回该字段
     */
    @JsonProperty("search_info")
    private String searchInfo;

    /**
     * 对话返回结果
     */
    private String result;

    /**
     * 表示用户输入是否存在安全风险，是否关闭当前会话，清理历史会话信息
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息
     * false：否，表示用户输入无安全风险
     */
    @JsonProperty("need_clear_history")
    private Boolean needClearHistory;

    /**
     * 0：正常返回
     * 其他：非正常
     */
    private Integer flag;

    /**
     * 当need_clear_history为true时，此字段会告知第几轮对话有敏感信息，如果是当前问题，ban_round=-1
     */
    @JsonProperty("ban_round")
    private Integer banRound;

    /**
     * token统计信息
     */
    private WenXinUsage usage;

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class WenXinUsage {

        /**
         * 问题tokens数
         */
        @JsonProperty("prompt_tokens")
        private long promptTokens;
        /**
         * 回答tokens数
         */
        @JsonProperty("completion_tokens")
        private long completionTokens;
        /**
         * 回答tokens数
         */
        @JsonProperty("total_tokens")
        private long totalTokens;
    }

    /**
     * 搜索数据
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class SearchInfo {

        /**
         * 搜索结果列表
         */
        @JsonProperty("search_results")
        private List<SearchResult> searchResults;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class SearchResult {

        /**
         * 序号
         */
        private Integer index;

        /**
         * 搜索结果URL
         */
        private String url;

        /**
         * 搜索结果标题
         */
        private String title;
    }
}
