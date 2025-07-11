/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.ai.models.deepseek;

import com.diboot.ai.common.request.AiChatRequest;
import com.diboot.ai.common.request.AiRequest;
import com.diboot.ai.common.request.AiRequestConvert;
import com.diboot.ai.common.response.AiChatResponse;
import com.diboot.ai.common.response.AiResponse;
import com.diboot.ai.common.response.AiResponseConvert;
import com.diboot.ai.config.AiConfiguration;
import com.diboot.ai.models.AbstractModelProvider;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSourceListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * DeepSeek Provider
 */
@Slf4j
public class DeepSeekChatModelProvider extends AbstractModelProvider implements AiRequestConvert<AiChatRequest, DeepSeekChatRequest>,
        AiResponseConvert<AiChatResponse, DeepSeekChatResponse> {

    public DeepSeekChatModelProvider(AiConfiguration configuration) {
        super(configuration, List.of(
                DeepSeekEnum.Model.DEEPSEEK_CHAT.getCode(),
                DeepSeekEnum.Model.DEEPSEEK_REASONER.getCode())
        );
    }

    @Override
    public DeepSeekChatRequest convertRequest(AiChatRequest aiRequest) {
        return new DeepSeekChatRequest().setModel(aiRequest.getModel()).setMessages(aiRequest.getMessages());
    }

    @Override
    public AiResponse convertResponse(DeepSeekChatResponse response) {
        if (V.isEmpty(response.getChoices())) {
            return null;
        }
        return new AiChatResponse()
                .setPattern(AiChatResponse.ResultPattern.INCREASE)
                .setChoices(response.getChoices());
    }

    @Override
    public void executeStream(AiRequest aiRequest, EventSourceListener listener) {
        // 将通用参数 转化为 具体模型参数
        DeepSeekChatRequest aiChatRequest = convertRequest((AiChatRequest) aiRequest);
        // 构建请求对象
        DeepSeekConfig deepSeekConfig = configuration.getDeepseek();
        Request request = new Request.Builder()
                .url(deepSeekConfig.getChatApi())
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + deepSeekConfig.getApiKey())
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .post(RequestBody.Companion.create(JSON.toJSONString(aiChatRequest), okhttp3.MediaType.parse(MediaType.APPLICATION_JSON_VALUE)))
                .build();
        // 实例化EventSource，注册EventSource监听器，包装外部监听器，对响应数据进行处理
        factory.newEventSource(request, wrapEventSourceListener(listener, (result) -> JSON.parseObject(result, DeepSeekChatResponse.class)));
    }

    @Override
    public boolean supports(String model) {
        if (supportModels.contains(model)) {
            // 检查配置是否完整
            DeepSeekConfig deepSeekConfig = configuration.getDeepseek();
            if (V.isEmpty(deepSeekConfig) || V.isEmpty(deepSeekConfig.getApiKey())) {
                log.error("未配置 {} 模型key", model);
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.modelProvider.unsettingModelKey");
            }
            return true;
        }
        return false;
    }
}
