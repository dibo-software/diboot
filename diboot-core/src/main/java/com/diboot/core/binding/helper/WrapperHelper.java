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
package com.diboot.core.binding.helper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.config.Cons;
import com.diboot.core.data.copy.Accept;
import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wrapper帮助类
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/01/05
 */
public class WrapperHelper {

    /**
     * 构建排序
     *
     * @param query        查询器
     * @param orderBy      排序条件
     * @param field2column 字段转列名函数
     */
    public static void buildOrderBy(QueryWrapper<?> query, String orderBy, Function<String, String> field2column) {
        // 解析排序
        if (V.notEmpty(orderBy)) {
            for (String field : S.split(orderBy, Cons.SEPARATOR_COMMA)) {
                V.securityCheck(field);
                String[] fieldAndOrder = field.split(Cons.SEPARATOR_COLON);
                String columnName = field2column.apply(fieldAndOrder[0]);
                if (fieldAndOrder.length > 1 && Cons.ORDER_DESC.equalsIgnoreCase(fieldAndOrder[1])) {
                    query.orderByDesc(columnName);
                } else {
                    query.orderByAsc(columnName);
                }
            }
        }
    }

    /**
     * 基于VO提取最小集select字段
     *
     * @param query
     * @param entityClass
     * @param voClass
     */
    public static void optimizeSelect(Wrapper<?> query, Class<?> entityClass, Class<?> voClass) {
        if (!(query instanceof Query) || query.getSqlSelect() != null) {
            return;
        }
        ((Query) query).select(entityClass, buildSelectPredicate(voClass));
    }

    /**
     * 构建select字段筛选器
     *
     * @param voClass
     * @return
     */
    public static Predicate<TableFieldInfo> buildSelectPredicate(Class<?> voClass) {
        List<String> fieldList = BindingCacheManager.getFields(voClass).stream().flatMap(field -> {
            Accept accept = field.getAnnotation(Accept.class);
            return accept == null ? Stream.of(field.getName()) : accept.override() ? Stream.of(accept.name()) : Stream.of(field.getName(), accept.name());
        }).collect(Collectors.toList());
        return fieldInfo -> fieldList.contains(fieldInfo.getField().getName()) && !fieldInfo.isLogicDelete();
    }

}
