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

import com.diboot.ai.common.AiMessage;
import com.diboot.ai.common.request.AiChatRequest;
import com.diboot.ai.common.request.AiEnum;
import com.diboot.ai.common.request.AiRequest;
import com.diboot.ai.common.request.AiRequestConvert;
import com.diboot.ai.common.response.AiChatResponse;
import com.diboot.ai.common.response.AiChoice;
import com.diboot.ai.common.response.AiResponse;
import com.diboot.ai.common.response.AiResponseConvert;
import com.diboot.ai.config.AiConfiguration;
import com.diboot.ai.models.AbstractModelProvider;
import com.diboot.ai.models.qwen.QwenConfig;
import com.diboot.ai.models.wenxin.WenXinConfig;
import com.diboot.ai.models.wenxin.WenXinEnum;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kimi 模型提供
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/7
 */
@Slf4j
public class KimiChatModelProvider extends AbstractModelProvider implements AiRequestConvert<AiChatRequest, KimiChatRequest>,
        AiResponseConvert<AiChatResponse, KimiChatResponse> {

    public KimiChatModelProvider(AiConfiguration configuration) {
        super(configuration, Arrays.asList(
                KimiEnum.Model.MOONSHOT_V1_8K.getCode(),
                KimiEnum.Model.MOONSHOT_V1_32K.getCode(),
                KimiEnum.Model.MOONSHOT_V1_128K.getCode()
        ));
    }

    @Override
    public KimiChatRequest convertRequest(AiChatRequest source) {
        // 转换请求
        return new KimiChatRequest().setModel(source.getModel()).setMessages(source.getMessages());
    }

    @Override
    public AiResponse convertResponse(KimiChatResponse response) {
        if (V.isEmpty(response.getChoices())) {
            return null;
        }
        KimiChoice kimiChoice = response.getChoices().get(0);
        AiMessage aiMessage = kimiChoice.getDelta();
        return new AiChatResponse()
                .setPattern(AiChatResponse.ResultPattern.INCREASE)
                .setChoices(Collections.singletonList(new AiChoice()
                        .setFinishReason(kimiChoice.getFinishReason())
                        .setMessage(new AiMessage().setRole(AiEnum.Role.ASSISTANT.getCode())
                                .setContent(aiMessage.getContent()))
                ));
    }

    @Override
    public void executeStream(AiRequest aiRequest, EventSourceListener listener) {
        // 将通用参数 转化为 具体模型参数
        KimiChatRequest aiChatRequest = convertRequest((AiChatRequest) aiRequest);
        // 构建请求对象
        KimiConfig kimiConfig = configuration.getKimi();
        Request request = new Request.Builder()
                .url(kimiConfig.getChatApi())
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + kimiConfig.getApiKey())
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .post(RequestBody.Companion.create(JSON.toJSONString(aiChatRequest), okhttp3.MediaType.parse(MediaType.APPLICATION_JSON_VALUE)))
                .build();
        // 实例化EventSource，注册EventSource监听器，包装外部监听器，对响应数据进行处理
        factory.newEventSource(request, wrapEventSourceListener(listener, (result) -> JSON.parseObject(result, KimiChatResponse.class)));
    }

    @Override
    public boolean supports(String model) {
        if (supportModels.contains(model)) {
            // 检查配置是否完整
            KimiConfig kimiConfig = configuration.getKimi();
            if (V.isEmpty(kimiConfig) || V.isEmpty(kimiConfig.getApiKey()) ) {
                log.error("未配置 {} 模型key", model);
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.modelProvider.unsettingModelKey");
            }
            return true;
        }
        return false;
    }

}
