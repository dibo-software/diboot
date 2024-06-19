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
package com.diboot.ai.config;

import com.diboot.ai.models.ModelProvider;
import com.diboot.ai.models.kimi.KimiChatModelProvider;
import com.diboot.ai.models.kimi.KimiConfig;
import com.diboot.ai.models.qwen.QwenChatModelProvider;
import com.diboot.ai.models.qwen.QwenConfig;
import com.diboot.ai.models.wenxin.WenXinChatModelProvider;
import com.diboot.ai.models.wenxin.WenXinConfig;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ai配置文件类
 *
 * @author : uu
 * @version : v1.0
 * @Date 2024/4/25
 */
@Getter
@Setter
public class AiConfiguration {
    // configurator扩展配置
    private final static String AI_CONFIGURATOR = "META-INF/aiconfigurator.factories";

    // ====== 模型配置 start=====
    /**
     * 阿里云 通义千问配置
     */
    private QwenConfig qwen;
    /**
     * 百度 千帆模型配置
     */
    private WenXinConfig wenxin;
    /**
     * Kimi 配置
     */
    private KimiConfig kimi;

    // ====== 模型配置 end=====

    // ====== Http客户端配置 start =======
    /**
     * okhttp 客户端
     */
    private OkHttpClient okhttpClient;

    /**
     * okhttp 客户端日志级别
     * <p>
     * NONE：不记录任何日志。
     * BASIC：记录请求类型、URL、响应状态码以及响应时间。
     * HEADERS：记录请求和响应的头部信息，以及BASIC级别的信息。
     * BODY：记录请求和响应的头部信息、body内容，以及BASIC级别的信息。注意，记录body内容可能会消耗资源，并且会读取body数据，这可能会影响请求的执行。
     */
    private HttpLoggingInterceptor.Level httpLoggingLevel = HttpLoggingInterceptor.Level.BASIC;

    // ====== 客户端配置 end =======

    /**
     * 模型拦截器
     */
    private List<ModelProvider> modelProviders = new ArrayList<>();

    /**
     * 配置增强
     */
    private List<AiConfigurator> aiConfigurators;

    public AiConfiguration(OkHttpClient okHttpClient) {
        this.okhttpClient = okHttpClient;
        initConfigurators();
        initDefaultModelProvider();
        configure();
    }

    /**
     * 初始化Configurator
     */
    private void initConfigurators() {
        SpringFactoriesLoader springFactoriesLoader = SpringFactoriesLoader.forResourceLocation(AI_CONFIGURATOR);
        this.aiConfigurators = springFactoriesLoader.load(AiConfigurator.class);
        // 排序
        aiConfigurators.sort(Comparator.comparingInt(AiConfigurator::getPriority));
    }

    /**
     * 初始化默认的模型供应
     */
    private void initDefaultModelProvider() {
        modelProviders.add(new QwenChatModelProvider(this));
        modelProviders.add(new WenXinChatModelProvider(this));
        modelProviders.add(new KimiChatModelProvider(this));
    }

    /**
     * 初始化配置器
     */
    private void configure() {
        // 执行扩展配置
        aiConfigurators.forEach(aiConfigurator -> aiConfigurator.configure(this));
    }
}
