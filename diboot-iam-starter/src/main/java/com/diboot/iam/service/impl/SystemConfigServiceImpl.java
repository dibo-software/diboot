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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.entity.SystemConfig;
import com.diboot.iam.mapper.SystemConfigMapper;
import com.diboot.iam.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置相关Service实现
 *
 * @author wind
 * @version v2.5.0
 * @date 2022-01-13
 */
@Slf4j
@Service
public class SystemConfigServiceImpl extends BaseServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Override
    public <T> T findConfigValue(String category, String propKey) {
        SystemConfig info = this.getSingleEntity(buildQueryWrapper(category).eq(SystemConfig::getPropKey, propKey));
        return info == null ? null : (T) value4Type(info);
    }

    @Override
    public Map<String, Object> getConfigMapByCategory(String category) {
        return getEntityList(buildQueryWrapper(category).select(SystemConfig::getPropKey, SystemConfig::getPropValue))
                .stream().collect(Collectors.toMap(SystemConfig::getPropKey, this::value4Type));
    }

    protected LambdaQueryWrapper<SystemConfig> buildQueryWrapper(String category) {
        return Wrappers.<SystemConfig>lambdaQuery().and(query -> {
            if (V.isEmpty(category)) {
                query.eq(SystemConfig::getCategory, S.EMPTY).or().isNull(SystemConfig::getCategory);
            } else {
                query.eq(SystemConfig::getCategory, category);
            }
        });
    }

    protected Object value4Type(SystemConfig systemConfig) {
        if (V.isEmail(systemConfig.getPropValue())) {
            return null;
        }
        if ("boolean".equals(systemConfig.getDataType())) {
            return V.isTrue(systemConfig.getPropValue());
        }
        if ("number".equals(systemConfig.getDataType())) {
            return new BigDecimal(systemConfig.getPropValue());
        }
        return systemConfig.getPropValue();
    }

}
