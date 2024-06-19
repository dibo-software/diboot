package com.diboot.ai.models.qwen;

import com.diboot.ai.common.response.AiChoice;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/8
 */
@Getter @Setter @Accessors(chain = true)
public class QwenChoice extends AiChoice implements Serializable {

    @Serial
    private static final long serialVersionUID = 7135003681395465791L;

    /**
     * 停止原因，null：生成过程中
     *
     * stop：stop token导致结束
     *
     * length：生成长度导致结束
     */
    @JsonProperty("finish_reason")
    private String finishReason;

}
