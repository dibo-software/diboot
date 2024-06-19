/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.ai;

import com.diboot.ai.client.AiClient;
import com.diboot.ai.common.AiMessage;
import com.diboot.ai.common.request.AiChatRequest;
import com.diboot.ai.common.request.AiEnum;
import com.diboot.ai.models.kimi.KimiEnum;
import com.diboot.ai.models.qwen.QwenEnum;
import com.diboot.ai.models.wenxin.WenXinEnum;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServletInitializer.class)
@Slf4j
public class ApplicationTest {

    @Autowired
    private AiClient aiClient;

    @Test
    public void testChat() throws Exception {
        AiChatRequest aiRequest = new AiChatRequest();
        aiRequest.setModel(QwenEnum.Model.ALI_QWEN_MAX.getCode());
        aiRequest.setMessages(
                Arrays.asList(new AiMessage().setRole(AiEnum.Role.USER.getCode()).setContent("Diboot开发框架的优势有哪些？"))
        );
        // 保持长链接
        CountDownLatch countDownLatch = new CountDownLatch(1);
        aiClient.executeStream(aiRequest, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                log.debug("---------> {}, {}, {}", id, type);
                log.debug(data);
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    @Test
    public void testWenXinChat() throws Exception {
        AiChatRequest aiRequest = new AiChatRequest();
        aiRequest.setModel(WenXinEnum.Model.YI_34B_CHAT.getCode());
        aiRequest.setMessages(
                Arrays.asList(new AiMessage().setRole(AiEnum.Role.USER.getCode()).setContent("Diboot开发框架的优势有哪些？"))
        );
        // 保持长链接
        CountDownLatch countDownLatch = new CountDownLatch(1);
        aiClient.executeStream(aiRequest, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                log.debug("---------> {}, {}, {}", id, type);
                log.debug(data);
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    @Test
    public void testKimiChat() throws Exception {
        AiChatRequest aiRequest = new AiChatRequest();
        aiRequest.setModel(KimiEnum.Model.MOONSHOT_V1_8K.getCode());
        aiRequest.setMessages(
                Arrays.asList(new AiMessage().setRole(AiEnum.Role.USER.getCode()).setContent("Diboot开发框架的优势有哪些？"))
        );
        // 保持长链接
        CountDownLatch countDownLatch = new CountDownLatch(1);
        aiClient.executeStream(aiRequest, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                log.debug("---------> {}, {}, {}", id, type);
                log.debug(data);
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}