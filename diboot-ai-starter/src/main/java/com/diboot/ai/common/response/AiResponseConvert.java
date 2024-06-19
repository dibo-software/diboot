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

import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * 响应转换器
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/26
 */
public interface AiResponseConvert<R extends AiResponse, S> {

    /**
     * 响应值转换
     *
     * @param response
     * @return
     */
    AiResponse convertResponse(S response);

    /**
     * 包装EventSourceListener 对流式响应部分统一处理
     *
     * @param listener
     * @param dataConvertFunction
     * @return
     */
    default EventSourceListener wrapEventSourceListener(EventSourceListener listener, Function<String, S> dataConvertFunction) {
        return new EventSourceListener() {
            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                listener.onClosed(eventSource);
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                // 忽略 kimi 等返回结果结束标记
                if("[DONE]".equals(data)) {
                    return;
                }
                // 将data转化成具体模型的响应数据
                S response = dataConvertFunction.apply(data);
                // 将模型数据转化为统一封装的响应值
                AiResponse aiResponse = convertResponse(response);
                // 响应值存在，即正常生成中
                if (V.notEmpty(aiResponse)) {
                    // 使用被包装的监听器传递参数
                    listener.onEvent(eventSource, id, type, JSON.stringify(aiResponse));
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                listener.onFailure(eventSource, t, response);
            }

            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                listener.onOpen(eventSource, response);
            }
        };
    }
}
