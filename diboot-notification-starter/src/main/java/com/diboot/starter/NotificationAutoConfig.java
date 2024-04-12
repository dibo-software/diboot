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
package com.diboot.starter;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.diboot.notification.channel.AliyunSmsChannel;
import com.diboot.notification.channel.SimpleEmailChannel;
import com.diboot.notification.channel.SystemMessageChannel;
import com.diboot.notification.entity.BaseUserVariables;
import com.diboot.notification.service.MessageService;
import com.diboot.notification.service.impl.MessageServiceImpl;
import com.diboot.notification.config.NotificationProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;

/**
 * 组件初始化
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/28
 * @Copyright © diboot.com
 */
@Slf4j
@Order(941)
@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
@ComponentScan(basePackages = {"com.diboot.notification"})
@MapperScan(basePackages = {"com.diboot.notification.mapper"})
public class NotificationAutoConfig {

    @Autowired
    private NotificationProperties notificationProperties;

    public NotificationAutoConfig() {
        log.info("初始化 notification 组件自动配置");
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("com.aliyun.dysmsapi20170525.Client")
    public static class MessageServiceConfig {
        @Bean
        @ConditionalOnMissingBean
        public MessageService messageService() {
            return new MessageServiceImpl(
                    Arrays.asList(new SimpleEmailChannel(), new SystemMessageChannel()),
                    Arrays.asList(BaseUserVariables.class)
            );
        }
    }
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Client.class)
    @EnableConfigurationProperties(NotificationProperties.class)
    public static class MessageServiceSMSConfig {

        @Autowired
        private NotificationProperties notificationProperties;

        @Bean
        @ConditionalOnMissingBean
        public MessageService messageService() {
            return new MessageServiceImpl(
                    Arrays.asList(new SimpleEmailChannel(), new SystemMessageChannel(), new AliyunSmsChannel()),
                    Arrays.asList(BaseUserVariables.class)
            );
        }

        @Bean
        @ConditionalOnMissingBean
        public Client smsClient() throws Exception {
            Config config = new Config()
                    .setAccessKeyId(notificationProperties.getSms().getAccessKeyId())
                    .setAccessKeySecret(notificationProperties.getSms().getAccessKeySecret())
                    .setEndpoint(notificationProperties.getSms().getEndpoint());
            return new Client(config);
        }
    }

}
