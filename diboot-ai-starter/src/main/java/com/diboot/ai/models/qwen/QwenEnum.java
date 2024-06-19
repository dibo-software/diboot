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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : uu
 * @version v3.4
 * @Date 2024/4/26
 */
public interface QwenEnum {

    @Getter
    @AllArgsConstructor
    enum Model {
        // 通义千问模型 ：https://help.aliyun.com/zh/dashscope/developer-reference/model-introduction?spm=a2c4g.11186623.0.0.6e2a512086lFix
        ALI_QWEN_TURBO("qwen-turbo", "通义千问超大规模语言模型，支持中文、英文等不同语言输入。"),
        ALI_QWEN_PLUS("qwen-plus", "通义千问超大规模语言模型增强版，支持中文、英文等不同语言输入。"),
        ALI_QWEN_MAX("qwen-max", "通义千问千亿级别超大规模语言模型，支持中文、英文等不同语言输入。随着模型的升级，qwen-max将滚动更新升级。")
        ;
        /**
         * AI 模型编码
         */
        private String code;

        /**
         * AI 模型描述
         */
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum FinishReason {
        NULL("null", "正在生成"),
        STOP("stop", "生成结束"),
        LENGTH("length", "生成长度过长");

        private final String code;
        private final String desc;
    }
}
