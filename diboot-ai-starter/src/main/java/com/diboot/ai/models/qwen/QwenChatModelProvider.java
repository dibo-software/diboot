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

import com.diboot.ai.common.AiMessage;
import com.diboot.ai.common.request.AiChatRequest;
import com.diboot.ai.common.request.AiEnum;
import com.diboot.ai.common.request.AiRequest;
import com.diboot.ai.common.request.AiRequestConvert;
import com.diboot.ai.common.response.AiChatResponse;
import com.diboot.ai.common.response.AiResponse;
import com.diboot.ai.common.response.AiResponseConvert;
import com.diboot.ai.config.AiConfiguration;
import com.diboot.ai.models.AbstractModelProvider;
import com.diboot.ai.models.kimi.KimiConfig;
import com.diboot.ai.models.wenxin.WenXinConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
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
 * 阿里 通义千问 Provider
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/25
 */
@Slf4j
public class QwenChatModelProvider extends AbstractModelProvider implements AiRequestConvert<AiChatRequest, QwenChatRequest>,
        AiResponseConvert<AiChatResponse, QwenChatResponse> {

    public QwenChatModelProvider(AiConfiguration configuration) {
        super(configuration,
                Arrays.asList(
                        QwenEnum.Model.ALI_QWEN_TURBO.getCode(),
                        QwenEnum.Model.ALI_QWEN_PLUS.getCode(),
                        QwenEnum.Model.ALI_QWEN_MAX.getCode()
                )
        );
    }

    @Override
    public void executeStream(AiRequest aiRequest, EventSourceListener listener) {
        // 将通用参数 转化为 具体模型参数
        QwenChatRequest qwenChatRequest = convertRequest((AiChatRequest) aiRequest);
        // 构建请求对象
        QwenConfig qwenConfig = configuration.getQwen();
        Request request = new Request.Builder()
                .url(qwenConfig.getChatApi())
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + qwenConfig.getApiKey())
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .post(RequestBody.Companion.create(JSON.toJSONString(qwenChatRequest), okhttp3.MediaType.parse(MediaType.APPLICATION_JSON_VALUE)))
                .build();
        // 实例化EventSource，注册EventSource监听器，包装外部监听器，对响应数据进行处理
        factory.newEventSource(request, wrapEventSourceListener(listener, (result) -> JSON.parseObject(result, QwenChatResponse.class)));
    }

    @Override
    public boolean supports(String model) {
        if (supportModels.contains(model)) {
            // 检查配置是否完整
            QwenConfig qwenConfig = configuration.getQwen();
            if (V.isEmpty(qwenConfig) || V.isEmpty(qwenConfig.getApiKey()) ) {
                log.error("未配置 {} 模型key", model);
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.modelProvider.unsettingModelKey");
            }
            return true;
        }
        return false;
    }

    @Override
    public QwenChatRequest convertRequest(AiChatRequest source) {
        // 将通用消息体构建成模型消息体
        return new QwenChatRequest()
                .setModel(source.getModel())
                .setInput(new QwenChatRequest.Input().setMessages(source.getMessages()));
    }

    @Override
    public AiResponse convertResponse(QwenChatResponse response) {
        if (V.isEmpty(response.getOutput())) {
            return null;
        }
        String finishReason = response.getOutput().getFinishReason();
        List<QwenChoice> choices = response.getOutput().getChoices();
        if (V.notEmpty(finishReason)) {
            return new AiChatResponse()
                    .setChoices(Collections.singletonList(new QwenChoice()
                            .setFinishReason(finishReason)
                            .setMessage(new AiMessage().setRole(AiEnum.Role.SYSTEM.getCode())
                                    .setContent(response.getOutput().getText()))
                    ));
        }
//       入参 result_format = message
        else if (V.notEmpty(choices)) {
            return new AiChatResponse().setChoices(choices);
        }
        return null;
    }
}
