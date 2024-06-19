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
 * 百度千帆 Provider
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/7
 */
@Slf4j
public class WenXinChatModelProvider extends AbstractModelProvider implements AiRequestConvert<AiChatRequest, WenXinChatRequest>,
        AiResponseConvert<AiChatResponse, WenXinChatResponse> {

    public WenXinChatModelProvider(AiConfiguration configuration) {
        super(configuration,
                Arrays.asList(
                        WenXinEnum.Model.YI_34B_CHAT.getCode(),
                        WenXinEnum.Model.ERNIE_4_0_8K.getCode()
                )
        );
    }

    @Override
    public void executeStream(AiRequest aiRequest, EventSourceListener listener) {
        // 将通用参数 转化为 具体模型参数
        WenXinChatRequest baiduChatRequest = convertRequest((AiChatRequest) aiRequest);
        WenXinConfig wenXinConfig = configuration.getWenxin();
        // 获取token
        String accessToken = WenXinToken.getAccessToken(configuration.getOkhttpClient(), wenXinConfig);
        // 构建请求对象
        Request request = new Request.Builder()
                .url(wenXinConfig.getChatApi().concat("?").concat(WenXinToken.TOKEN_KEY).concat("=").concat(accessToken))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .post(RequestBody.Companion.create(JSON.toJSONString(baiduChatRequest), okhttp3.MediaType.parse(MediaType.APPLICATION_JSON_VALUE)))
                .build();
        // 实例化EventSource，注册EventSource监听器，包装外部监听器，对响应数据进行处理
        factory.newEventSource(request, wrapEventSourceListener(listener, (result) -> JSON.parseObject(result, WenXinChatResponse.class)));
    }

    @Override
    public boolean supports(String model) {
        if (supportModels.contains(model)) {
            // 检查配置是否完整
            WenXinConfig wenXinConfig = configuration.getWenxin();
            if (V.isEmpty(wenXinConfig) || V.isEmpty(wenXinConfig.getApiKey()) || V.isEmpty(wenXinConfig.getSecretKey())) {
                log.error("未配置 {} 模型key 或 secret", model);
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.modelProvider.unsettingModelKeyOrSecret");
            }
            return true;
        }
        return false;
    }

    @Override
    public WenXinChatRequest convertRequest(AiChatRequest source) {
        // 将通用消息体构建成模型消息体
        return new WenXinChatRequest().setMessages(source.getMessages());
    }

    @Override
    public AiResponse convertResponse(WenXinChatResponse response) {
        return new AiChatResponse()
                .setPattern(AiChatResponse.ResultPattern.INCREASE)
                .setChoices(Collections.singletonList(new AiChoice()
                        .setFinishReason(response.getIsEnd() ? WenXinEnum.FinishReason.STOP.getCode() : null)
                        .setMessage(new AiMessage().setRole(AiEnum.Role.ASSISTANT.getCode())
                                .setContent(response.getResult()))
                ));
    }
}
