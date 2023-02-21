/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.service;

import com.diboot.core.service.BaseService;
import com.diboot.iam.entity.SystemConfig;

import java.util.Map;

/**
 * 系统配置相关Service
 *
 * @author wind
 * @version v2.5.0
 * @date 2022-01-13
 */
public interface SystemConfigService extends BaseService<SystemConfig> {

    /**
     * 查找配置值
     *
     * @param category 类型
     * @param propKey  属性名
     * @return 值
     */
    <T> T findConfigValue(String category, String propKey);

    /**
     * 根据类型获取配置映射
     *
     * @param category 类型
     * @return 配置值映射
     */
    Map<String, Object> getConfigMapByCategory(String category);

}
