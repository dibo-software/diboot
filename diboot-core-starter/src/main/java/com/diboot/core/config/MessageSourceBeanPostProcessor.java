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
package com.diboot.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 自动添加国际化文件
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/6/13
 */
@Slf4j
public class MessageSourceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ResourceBundleMessageSource messageSource) {
            messageSource.addBasenames("messages", "core_messages", "file_messages", "iam_messages", "mobile_messages", "notification_messages", "tenant_messages", "scheduler_messages", "ai_messages");
            log.info("国际化资源文件添加完成，basenames = {}", messageSource.getBasenameSet());
        }
        return bean;
    }
}
