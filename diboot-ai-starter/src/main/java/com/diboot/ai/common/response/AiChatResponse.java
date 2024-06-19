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
package com.diboot.ai.common.response;

import com.diboot.ai.common.AiMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 对话请求响应
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/26
 */
@Getter
@Setter
@Accessors(chain = true)
public class AiChatResponse implements AiResponse, Serializable {


    @Serial
    private static final long serialVersionUID = 1897111766782881992L;

    /**
     * 对话信息
     */
    private List<? extends AiChoice> choices;

    /**
     * 结果模式
     */
    private ResultPattern pattern = ResultPattern.REPLACE;

    /**
     * 结果模式
     */
    @Getter
    @AllArgsConstructor
    public enum ResultPattern {
        // 结果追加 （每次返回剩余部分）
        INCREASE("increase"),
        // 结果替换 （每次返回都是完整的结果）
        REPLACE("replace");

        private String code;
    }
}
