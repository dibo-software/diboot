/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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

import com.diboot.tenant.config.TenantProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 多租户组件自动初始化
 *
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/15
 */
@Order(971)
@Slf4j
@Configuration
@EnableConfigurationProperties({TenantProperties.class})
@ComponentScan(basePackages = {"com.diboot.tenant"})
@MapperScan(basePackages = {"com.diboot.tenant.mapper"})
@AutoConfigureAfter({CoreAutoConfig.class})
public class TenantAutoConfig {

    public TenantAutoConfig() {
        log.info("初始化 tenant 组件自动配置");
    }

}
