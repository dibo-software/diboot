/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.mobile.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信相关属性
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/8/31  00:18
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "diboot.mobile")
public class MobileProperties {

    /**
     * 小程序的配置
     */
    private Config wxMiniapp;

    /**
     * 公众号的配置
     */
    private Config wxMp;

    @Setter
    @Getter
    public static class Config {

        /**
         * 设置appid
         */
        private String appid;

        /**
         * 设置Secret
         */
        private String secret;

        /**
         * 设置消息服务器配置的token
         */
        private String token;

        /**
         * 设置消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;

        /**
         * 授权state
         */
        private String state;
    }
}