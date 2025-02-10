package com.diboot.ai.models.deepseek;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface DeepSeekEnum {

    @Getter
    @AllArgsConstructor
    enum Model {

        DEEPSEEK_CHAT("deepseek-chat"),
        DEEPSEEK_REASONER("deepseek-reasoner");

        private final String code;
    }

}
