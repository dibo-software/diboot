package com.diboot.ai.models.deepseek;

import com.diboot.ai.common.AiMessage;
import com.diboot.ai.common.response.AiChoice;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
public class DeepSeekChoice  extends AiChoice implements Serializable {
    @Serial
    private static final long serialVersionUID = -9136799907747269267L;

    /**
     * 流式内容
     */
    private AiMessage delta;

    public DeepSeekChoice setDelta(AiMessage delta) {
        this.setMessage(delta);
        this.delta = delta;
        return this;
    }
}
