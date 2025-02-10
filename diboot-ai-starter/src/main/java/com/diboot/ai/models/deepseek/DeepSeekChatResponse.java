package com.diboot.ai.models.deepseek;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * DeepSeek 返回结果
 */
@Getter
@Setter
@Accessors(chain = true)
public class DeepSeekChatResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 3769378398215170631L;

    private String id;
    private String object;
    private Long created;
    private String model;
    private List<DeepSeekChoice> choices;
    private DeepSeekUsage usage;
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;


    @Getter
    @Setter
    @Accessors(chain = true)
    public static class DeepSeekUsage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        @JsonProperty("total_tokens")
        private Integer totalTokens;
        @JsonProperty("prompt_tokens_details")
        private PromptTokensDetails promptTokensDetails;
        @JsonProperty("prompt_cache_hit_tokens")
        private Integer promptCacheHitTokens;
        @JsonProperty("prompt_cache_miss_tokens")
        private Integer promptCacheMissTokens;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class PromptTokensDetails {
        @JsonProperty("cached_tokens")
        private Integer cachedTokens;
    }
}
