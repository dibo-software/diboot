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
package com.diboot.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * diboot全局配置文件类
 * <p>
 * 优先级高于其他模块配置
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/26
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "diboot")
public class GlobalProperties {

    /**
     * 全局初始化SQL，默认false不自动安装SQL
     */
    private boolean initSql = false;

    /**
     * 是否启用国际化，默认false不启用
     */
    private boolean i18n = false;

}
