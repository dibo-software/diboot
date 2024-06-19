package com.diboot.ai.models.kimi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/8
 */
public interface KimiEnum {

    @Getter
    @AllArgsConstructor
    enum Model {
        MOONSHOT_V1_8K("moonshot-v1-8k", "长度为 8k 的模型，适用于生成短文本"),
        MOONSHOT_V1_32K("moonshot-v1-32k", "长度为 32k 的模型，适用于生成长文本"),
        MOONSHOT_V1_128K("moonshot-v1-128k", "长度为 128k 的模型，适用于生成超长文本");

        /**
         * AI 模型编码
         */
        private String code;

        /**
         * AI 模型描述
         */
        private String desc;
    }

}
