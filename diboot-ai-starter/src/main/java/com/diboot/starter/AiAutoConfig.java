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
package com.diboot.starter;

import com.diboot.ai.client.AiClient;
import com.diboot.ai.config.AiConfiguration;
import com.diboot.ai.config.AiProperties;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Diboot Ai 自动配置类
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/4/26
 */
@Order(961)
@Configuration
@EnableConfigurationProperties({AiProperties.class})
@ComponentScan(basePackages = {"com.diboot.ai"})
@MapperScan(basePackages = {"com.diboot.ai.mapper"})
public class AiAutoConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(AiAutoConfig.class);

    @Autowired
    private AiProperties aiProperties;

    public AiAutoConfig() {
        log.info("初始化 AI 组件自动配置");
    }

    /**
     * 创建ai 客户端
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AiClient aiClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(aiProperties.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(aiProperties.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(aiProperties.getWriteTimeout(), TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(aiProperties.getHttpLoggingLevel()))
                .build();
        // 构建 AiConfiguration
        AiConfiguration aiConfiguration = new AiConfiguration(okHttpClient);
        // 设置模型配置
        aiConfiguration.setQwen(aiProperties.getQwen());
        aiConfiguration.setKimi(aiProperties.getKimi());
        aiConfiguration.setWenxin(aiProperties.getWenxin());
        return new AiClient(aiConfiguration);
    }

}
