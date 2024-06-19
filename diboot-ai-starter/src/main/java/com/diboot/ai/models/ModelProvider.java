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
package com.diboot.ai.models;

import com.diboot.ai.common.request.AiRequest;
import okhttp3.sse.EventSourceListener;

/**
 * 模型Provider
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/25
 */
public interface ModelProvider {

    /**
     * 流式 执行器
     *
     * @param aiRequest
     * @param listener
     * @return
     */
    void executeStream(AiRequest aiRequest, EventSourceListener listener);

    /**
     * 当前provider是否支持模型
     *
     * @param model 模型名称
     * @return
     */
    boolean supports(String model);
}
