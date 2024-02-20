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
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.config.Cons;
import com.diboot.core.data.copy.Accept;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
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
@Slf4j
public class WrapperHelper {

    /**
     * 建立条件
     *
     * @param wrapper    条件包装器
     * @param comparison 匹配方式
     * @param columnName 列名
     * @param value      值
     */
    public static void buildQueryCriteria(QueryWrapper<?> wrapper, Comparison comparison, String columnName, Object value) {
        switch (comparison) {
            case EQ:
                wrapper.eq(columnName, value);
                break;
            case IN:
                if (value.getClass().isArray()) {
                    Object[] valueArray = (Object[]) value;
                    if (valueArray.length == 1) {
                        wrapper.eq(columnName, valueArray[0]);
                    } else if (valueArray.length >= 2) {
                        wrapper.in(columnName, valueArray);
                    }
                } else if (value instanceof Collection) {
                    wrapper.in(!((Collection) value).isEmpty(), columnName, (Collection<?>) value);
                } else {
                    log.warn("字段类型错误：IN仅支持List及数组.");
                }
                break;
            case NOT_IN:
                if (value.getClass().isArray()) {
                    Object[] valueArray = (Object[]) value;
                    if (valueArray.length == 1) {
                        wrapper.ne(columnName, valueArray[0]);
                    } else if (valueArray.length >= 2) {
                        wrapper.notIn(columnName, valueArray);
                    }
                } else if (value instanceof Collection) {
                    wrapper.notIn(!((Collection) value).isEmpty(), columnName, (Collection<?>) value);
                } else {
                    log.warn("字段类型错误：NOT_IN仅支持List及数组.");
                }
                break;
            case CONTAINS:
                boolean isString = S.contains(JSON.toJSONString(value), "\"");
                BiConsumer<QueryWrapper<?>,String> basicTypeProtection = (query, val) -> {
                    query.or().likeRight(columnName, "[" + val + ",");
                    query.or().like(columnName, "," + val + ",");
                    query.or().likeLeft(columnName, "," + val + "]");
                    query.or().eq(columnName, "[" + val + "]");
                };
                if (value instanceof Collection) {
                    wrapper.and(query -> {
                        for (Object val : (Collection<?>) value) {
                            if (isString) {
                                if(!S.valueOf(val).contains("\"")) {
                                    val = "\"" + val + "\"";
                                }
                                query.or().like(columnName, val);
                            } else {
                                basicTypeProtection.accept(query, S.valueOf(val));
                            }
                        }
                    });
                } else if (value.getClass().isArray()) {
                    wrapper.and(query -> {
                        for (Object val : (Object[]) value) {
                            if (isString) {
                                if(!S.valueOf(val).contains("\"")) {
                                    val = "\"" + val + "\"";
                                }
                                query.or().like(columnName, val);
                            } else {
                                basicTypeProtection.accept(query, S.valueOf(val));
                            }
                        }
                    });
                } else {
                    if (isString) {
                        wrapper.like(columnName, value);
                    } else {
                        wrapper.and(query -> basicTypeProtection.accept(query, S.valueOf(value)));
                    }
                }
                break;
            case LIKE:
                wrapper.like(columnName, value);
                break;
            case STARTSWITH:
                wrapper.likeRight(columnName, value);
                break;
            case ENDSWITH:
                wrapper.likeLeft(columnName, value);
                break;
            case GT:
                wrapper.gt(columnName, value);
                break;
            case BETWEEN_BEGIN:
            case GE:
                wrapper.ge(columnName, value);
                break;
            case LT:
                wrapper.lt(columnName, value);
                break;
            case BETWEEN_END:
            case LE:
                wrapper.le(columnName, value);
                break;
            case BETWEEN:
                if (value.getClass().isArray()) {
                    Object[] valueArray = (Object[]) value;
                    if (valueArray.length == 1) {
                        wrapper.ge(columnName, valueArray[0]);
                    } else if (valueArray.length >= 2) {
                        wrapper.between(columnName, valueArray[0], valueArray[1]);
                    }
                } else if (value instanceof List) {
                    List<?> valueList = (List<?>) value;
                    if (valueList.size() == 1) {
                        wrapper.ge(columnName, valueList.get(0));
                    } else if (valueList.size() >= 2) {
                        wrapper.between(columnName, valueList.get(0), valueList.get(1));
                    }
                }
                // 支持逗号分隔的字符串
                else if (value instanceof String && ((String) value).contains(Cons.SEPARATOR_COMMA)) {
                    Object[] valueArray = ((String) value).split(Cons.SEPARATOR_COMMA);
                    wrapper.between(columnName, valueArray[0], valueArray[1]);
                } else {
                    wrapper.ge(columnName, value);
                }
                break;
            // 不等于
            case NOT_EQ:
                wrapper.ne(columnName, value);
                break;
            case IS_NULL:
                wrapper.isNull(columnName);
                break;
            case IS_NOT_NULL:
                wrapper.isNotNull(columnName);
                break;
            default:
                break;
        }
    }


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
        if (query.getSqlSelect() != null) {
            return;
        }
        if(query instanceof Query) {
            ((Query)query).select(entityClass, WrapperHelper.buildSelectPredicate(voClass));
        }
        else if(query instanceof ChainQuery) {
            Wrapper<?> queryWrapper = ((ChainQuery<?>)query).getWrapper();
            ((Query)queryWrapper).select(entityClass, WrapperHelper.buildSelectPredicate(voClass));
        }
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
