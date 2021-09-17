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
package com.diboot.iam.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * 异步任务执行相关配置
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/9/17
 * Copyright © diboot.com
 */
@Configuration
@Component
@EnableAsync
public class IamTaskExecutorConfig implements AsyncConfigurer {

    /**
     * 异步执行器
     * @return
     */
    @Bean("iamAsyncExecutor")
    @Override
    public Executor getAsyncExecutor(){
        return new SimpleAsyncTaskExecutor();
    }

}
