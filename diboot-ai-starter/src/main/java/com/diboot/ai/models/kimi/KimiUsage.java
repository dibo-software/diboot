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
package com.diboot.ai.models.kimi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Kimi token 使用情况
 * @author JerryMa
 * @version v3.4.0
 * @date 2024-04-28
 */
@Getter @Setter @Accessors(chain = true)
public class KimiUsage implements Serializable {
    private static final long serialVersionUID = -5562582362343016552L;

    @JsonProperty("prompt_tokens")
    private int promptTokens;

    @JsonProperty("completion_tokens")
    private int completionTokens;

    @JsonProperty("total_tokens")
    private int totalTokens;

}