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
package com.diboot.core.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.entity.SystemConfig;
import com.diboot.core.service.SystemConfigService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.S;

import java.util.List;
import java.util.Set;

/**
 * <h3>系统配置类型</h3>
 * 用于配置枚举继承，便于提取
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/01/13
 */
public interface SystemConfigType {

    /**
     * 类型标签
     *
     * @return
     */
    String typeLabel();

    /**
     * 属性名称
     *
     * @return
     */
    String name();

    /**
     * 属性标签
     *
     * @return
     */
    String propLabel();

    /**
     * 默认值
     * <p>
     * 支持获取配置文件；例如：${my.name:} 读取配置文件my.name的值，默认值是空字符串；无冒号默认值为null
     *
     * @return
     */
    Object defaultValue();

    /**
     * 是否必填（必填属性不可为空白字符串）
     *
     * @return
     */
    boolean required();

    /**
     * 选项集
     *
     * @return
     */
    default Set<String> options() {
        return null;
    }

    /**
     * 构建默认值
     *
     * @return
     */
    default Object buildDefaultValue() {
        Object defaultValue = defaultValue();
        if (defaultValue instanceof String) {
            String defValue = (String) defaultValue;
            if (S.startsWith(defValue, "${") && S.endsWith(defValue, "}")) {
                defValue = defValue.replaceAll("^\\$\\{|}$", S.EMPTY);
                if (defValue.contains(Cons.SEPARATOR_COLON)) {
                    String[] split = defValue.split(Cons.SEPARATOR_COLON);
                    return S.defaultIfEmpty(PropertiesUtils.get(split[0]), split.length > 1 ? split[1] : S.EMPTY);
                } else {
                    return PropertiesUtils.get(defValue);
                }
            }
        }
        return defaultValue;
    }

    /**
     * 获取配置的值
     *
     * @return
     */
    default String getValue() {
        LambdaQueryWrapper<SystemConfig> queryWrapper = Wrappers.<SystemConfig>lambdaQuery()
                .eq(SystemConfig::getType, this.getClass().getSimpleName()).eq(SystemConfig::getProp, this.name());
        SystemConfig info = ContextHelper.getBean(SystemConfigService.class).getSingleEntity(queryWrapper);
        return S.getIfEmpty(info == null ? null : info.getValue(), () -> S.valueOf(buildDefaultValue()));
    }

    /**
     * 获取字符串列表（逗号切分）
     *
     * @return
     */
    default List<String> getList() {
        return S.splitToList(getValue());
    }

    /**
     * 获取布尔类型值
     *
     * @return
     */
    default boolean getValueBoolean() {
        return S.toBoolean(getValue());
    }

    /**
     * 获取整数类型值
     *
     * @return
     */
    default Integer getValueInteger() {
        return S.toInt(getValue());
    }

    /**
     * 获取枚举值
     *
     * @param clazz
     * @param <T>
     * @return
     */
    default <T extends Enum<T>> T getValueEnum(Class<T> clazz) {
        String value = getValue();
        return S.isBlank(value) ? null : Enum.valueOf(clazz, value.trim());
    }

}
