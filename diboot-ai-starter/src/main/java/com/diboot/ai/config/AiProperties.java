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

import com.diboot.ai.models.kimi.KimiConfig;
import com.diboot.ai.models.qwen.QwenConfig;
import com.diboot.ai.models.wenxin.WenXinConfig;
import lombok.Getter;
import lombok.Setter;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Ai 可扩展配置
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/25
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "diboot.ai")
public class AiProperties {

    /**
     * okhttp 客户端日志级别
     * <p>
     * NONE：不记录任何日志。
     * BASIC：记录请求类型、URL、响应状态码以及响应时间。
     * HEADERS：记录请求和响应的头部信息，以及BASIC级别的信息。
     * BODY：记录请求和响应的头部信息、body内容，以及BASIC级别的信息。注意，记录body内容可能会消耗资源，并且会读取body数据，这可能会影响请求的执行。
     */
    private HttpLoggingInterceptor.Level httpLoggingLevel = HttpLoggingInterceptor.Level.BASIC;

    // http 超时配置
    /**
     * 连接超时 默认100s
     */
    private Long connectTimeout = 100L;
    /**
     * 读超时 默认100s
     */
    private Long readTimeout = 100L;
    /**
     * 写超时 默认100s
     */
    private Long writeTimeout = 100L;

    /**
     * 阿里模型配置
     */
    @NestedConfigurationProperty
    private QwenConfig qwen;

    /**
     * Kimi模型配置
     */
    @NestedConfigurationProperty
    private KimiConfig kimi;

    /**
     * 百度模型配置
     */
    @NestedConfigurationProperty
    private WenXinConfig wenxin;

}
