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
package com.diboot.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 定时任务相关的配置参数
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/28
 * @Copyright © diboot.com
 */
@Getter @Setter
@ConfigurationProperties(prefix = "diboot.notification")
public class NotificationProperties {

    private Sms sms;

    /**
     * 短信配置
     */
    @Getter@Setter
    public static class Sms {

        /**
         * keyId
         */
        private String accessKeyId;

        /**
         * secret
         */
        private String accessKeySecret;
        /**
         * 服务地址
         */
        private String endpoint;
    }

}