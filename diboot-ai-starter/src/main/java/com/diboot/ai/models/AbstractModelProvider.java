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

import com.diboot.ai.config.AiConfiguration;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型抽象提供
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/25
 */
public abstract class AbstractModelProvider implements ModelProvider {
    protected static final String BEARER_TOKEN_PREFIX = "Bearer ";

    // 当前拦截器支持的模型
    protected List<String> supportModels = new ArrayList<>();
    protected final AiConfiguration configuration;
    protected final EventSource.Factory factory;

    public AbstractModelProvider(AiConfiguration configuration, List<String> supportModels) {
        this.supportModels.addAll(supportModels);
        this.configuration = configuration;
        // 实例化EventSource工厂，使用 factory.newEventSource()创建EventSource，需要传递一个用于处理服务器发送事件的实例，并定义处理事件的回调逻辑
        this.factory = EventSources.createFactory(configuration.getOkhttpClient());
    }
}
