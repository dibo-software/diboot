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
package com.diboot.iam.config;

import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.S;
import com.diboot.iam.service.SystemConfigService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        String value = ContextHelper.getBean(SystemConfigService.class).findConfigValue(this.getClass().getSimpleName(), this.name());
        return value == null ? S.valueOf(buildDefaultValue()) : value;
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
    default boolean getBoolean() {
        return S.toBoolean(getValue());
    }

    /**
     * 获取整数类型值
     *
     * @return
     */
    default Integer getInteger() {
        return S.toInt(getValue());
    }

    /**
     * 获取枚举值
     *
     * @param clazz
     * @param <T>
     * @return
     */
    default <T extends Enum<T>> T getEnum(Class<T> clazz) {
        String value = getValue();
        return S.isBlank(value) ? null : Enum.valueOf(clazz, value.trim());
    }

    /**
     * 构建指定类型的配置值映射
     *
     * @param typeClass 配置类型class
     * @param <E>       配置类型
     * @return 指定类型配置值映射
     */
    static <E extends Enum<? extends SystemConfigType>> Values<E> values(Class<E> typeClass) {
        return new Values<>(typeClass);
    }

    /**
     * 单类型配置值映射
     * <p>
     * 用于获取同类型获取配置值减少查询
     */
    class Values<E extends Enum<? extends SystemConfigType>> {
        /**
         * 类型 Class
         */
        private final String type;
        /**
         * 值映射
         */
        private final Map<String, String> valueMap;

        private Values(Class<E> typeClass) {
            this.type = S.substringBefore(S.substringAfterLast(typeClass.getName(), "."), "$");
            this.valueMap = ContextHelper.getBean(SystemConfigService.class).getConfigMapByType(type);
        }

        /**
         * 获取值
         *
         * @param prop
         * @return
         */
        public String get(E prop) {
            return valueMap.computeIfAbsent(prop.name(), k -> S.valueOf(((SystemConfigType) prop).buildDefaultValue()));
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
        public <T extends Enum<T>> T getEnum(Class<T> clazz, E prop) {
            String value = get(prop);
            return S.isBlank(value) ? null : Enum.valueOf(clazz, value.trim());
        }

        /**
         * 获取配置 Map
         *
         * @return 属性值映射
         */
        public Map<String, String> getMap() {
            ContextHelper.getBean(SystemConfigService.class).getConfigItemsMap()
                    .getOrDefault(type, Collections.emptyList()).forEach(e -> get((E) e));
            return valueMap;
        }

    }

}
