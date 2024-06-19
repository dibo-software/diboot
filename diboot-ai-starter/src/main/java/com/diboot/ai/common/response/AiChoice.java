package com.diboot.ai.common.response;

import com.diboot.ai.common.AiMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 对话请求返回结果
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/8
 */
@Getter @Setter @Accessors(chain = true)
public class AiChoice implements Serializable {
    @Serial
    private static final long serialVersionUID = -1529642294837903044L;

    /**
     * 完成原因
     */
    @JsonProperty("finish_reason")
    private String finishReason;

    /**
     * 内容
     */
    private AiMessage message;

}
