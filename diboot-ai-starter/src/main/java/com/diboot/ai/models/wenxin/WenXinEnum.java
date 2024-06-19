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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 百度千帆 枚举
 * @author : uu
 * @version v3.4
 * @Date 2024/5/7
 */
public interface WenXinEnum {
    @Getter
    @AllArgsConstructor
    enum Model {
        // 千帆大模型 ：https://cloud.baidu.com/doc/WENXINWORKSHOP/s/clntwmv7t
        YI_34B_CHAT("Yi-34B-Chat", "Yi-34B是由零一万物开发并开源的双语大语言模型，使用4K序列长度进行训练，在推理期间可扩展到32K。"),
        ERNIE_4_0_8K("ERNIE-4.0-8K", "百度自研的旗舰级超大规模⼤语⾔模型，广泛适用于各领域复杂任务场景；支持自动对接百度搜索插件。"),
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
        NORMAL("normal", "输出内容完全由大模型生成，未触发截断、替换"),
        STOP("stop", "输出结果命中入参stop中指定的字段后被截断。【这里将作为统一的结束标记】"),
        LENGTH("length", "达到了最大的token数，根据EB返回结果is_truncated来截断"),
        CONTENT_FILTER("content_filter", "输出内容被截断、兜底、替换为**等");

        private final String code;
        private final String desc;
    }
}
