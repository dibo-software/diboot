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
package com.diboot.core.entity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.SystemConfigType;
import com.diboot.core.service.SystemConfigService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单类型配置值映射
 * <p>
 * 用于获取同类型获取配置值减少查询
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/01/13
 */
public class SystemConfigValues<E extends Enum<? extends SystemConfigType>> {
    /**
     * 值映射
     */
    private final Map<String, String> valueMap;

    private SystemConfigValues(List<SystemConfig> configInfoList) {
        this.valueMap = configInfoList.stream().collect(Collectors.toMap(SystemConfig::getProp, SystemConfig::getValue));
    }

    /**
     * 构建指定类型的配置值映射
     *
     * @param typeClass 配置类型class
     * @param <E>       配置类型
     * @return 指定类型配置值映射
     */
    public static <E extends Enum<? extends SystemConfigType>> SystemConfigValues<E> build(Class<E> typeClass) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getProp, SystemConfig::getValue).eq(SystemConfig::getType, typeClass.getSimpleName());
        return new SystemConfigValues<>(ContextHelper.getBean(SystemConfigService.class).getEntityList(queryWrapper));
    }

    /**
     * 获取值
     *
     * @param prop
     * @return
     */
    public String get(E prop) {
        return S.getIfEmpty(valueMap.get(prop.name()), () -> S.valueOf(((SystemConfigType) prop).buildDefaultValue()));
    }

    /**
     * 获取字符串列表（逗号切分）
     *
     * @param prop
     * @return
     */
    public List<String> getList(E prop) {
        return S.splitToList(get(prop));
    }

    /**
     * 获取布尔类型值
     *
     * @param prop
     * @return
     */
    public boolean geBoolean(E prop) {
        return S.toBoolean(get(prop));
    }

    /**
     * 获取整数类型值
     *
     * @param prop
     * @return
     */
    public Integer getInteger(E prop) {
        return S.toInt(get(prop));
    }

    /**
     * 获取指定类型枚举值
     *
     * @param clazz
     * @param prop
     * @param <T>
     * @return 枚举值
     */
    public <T extends Enum<T>> T getValueEnum(Class<T> clazz, E prop) {
        String value = get(prop);
        return S.isBlank(value) ? null : Enum.valueOf(clazz, value.trim());
    }

    @Override
    public String toString() {
        return valueMap.toString();
    }

}
