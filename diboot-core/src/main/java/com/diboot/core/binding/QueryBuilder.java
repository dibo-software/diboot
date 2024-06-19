/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.binding;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.diboot.core.binding.helper.WrapperHelper;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.binding.query.Strategy;
import com.diboot.core.binding.query.dynamic.AnnoJoiner;
import com.diboot.core.binding.query.dynamic.DynamicJoinQueryWrapper;
import com.diboot.core.binding.query.dynamic.ExtQueryWrapper;
import com.diboot.core.data.protect.DataEncryptHandler;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.type.NullType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * QueryWrapper构建器
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/07/27
 */
@SuppressWarnings({"unchecked", "rawtypes", "JavaDoc"})
public class QueryBuilder {
    private static Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param <DTO>
     * @return
     */
    public static <DTO> QueryWrapper toQueryWrapper(DTO dto) {
        return dtoToWrapper(dto, null, null);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param pagination 分页
     * @param <DTO>
     * @return
     */
    public static <DTO> QueryWrapper toQueryWrapper(DTO dto, Pagination pagination) {
        return dtoToWrapper(dto, null, pagination);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param fields 指定参与转换的属性值
     * @param <DTO>
     * @return
     */
    public static <DTO> QueryWrapper toQueryWrapper(DTO dto, Collection<String> fields) {
        return dtoToWrapper(dto, fields, null);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param fields     指定参与转换的属性值
     * @param pagination 分页
     * @param <DTO>
     * @return
     */
    public static <DTO> QueryWrapper toQueryWrapper(DTO dto, Collection<String> fields, Pagination pagination) {
        return dtoToWrapper(dto, fields, pagination);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param <DTO>
     * @return
     */
    public static <DTO> ExtQueryWrapper toDynamicJoinQueryWrapper(DTO dto) {
        return toDynamicJoinQueryWrapper(dto, null, null);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param pagination 分页
     * @param <DTO>
     * @return
     */
    public static <DTO> ExtQueryWrapper toDynamicJoinQueryWrapper(DTO dto, Pagination pagination) {
        return toDynamicJoinQueryWrapper(dto, null, pagination);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param <DTO>
     * @return
     */
    public static <DTO> ExtQueryWrapper toDynamicJoinQueryWrapper(DTO dto, Collection<String> fields) {
        return toDynamicJoinQueryWrapper(dto, fields, null);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     *
     * @param dto
     * @param fields 指定参与转换的属性值
     * @param <DTO>
     * @return
     */
    public static <DTO> ExtQueryWrapper toDynamicJoinQueryWrapper(DTO dto, Collection<String> fields, Pagination pagination) {
        QueryWrapper queryWrapper = dtoToWrapper(dto, fields, pagination);
        if (!(queryWrapper instanceof DynamicJoinQueryWrapper)) {
            return (ExtQueryWrapper) queryWrapper;
        }
        return (DynamicJoinQueryWrapper) queryWrapper;
    }

    /**
     * 转换具体实现
     *
     * @param dto
     * @return
     */
    private static <DTO> QueryWrapper<?> dtoToWrapper(DTO dto, Collection<String> fields, Pagination pagination) {
        QueryWrapper<?> wrapper;
        // 转换
        LinkedHashMap<String, FieldAndValue> fieldValuesMap = extractNotNullValues(dto, fields, pagination);
        if (V.isEmpty(fieldValuesMap)) {
            return new QueryWrapper<>();
        }
        // 只解析有值的
        fields = fieldValuesMap.keySet();
        // 是否有join联表查询
        boolean hasJoinTable = ParserCache.hasJoinTable(dto, fields);
        if (hasJoinTable) {
            wrapper = new DynamicJoinQueryWrapper<>(dto.getClass(), fields);
        } else {
            wrapper = new ExtQueryWrapper<>();
        }
        // 构建 ColumnName
        List<AnnoJoiner> annoJoinerList = ParserCache.getBindQueryAnnos(dto.getClass());
        BiFunction<BindQuery, Field, String> buildColumnName = (bindQuery, field) -> {
            if (bindQuery != null) {
                String key = field.getName() + bindQuery;
                for (AnnoJoiner annoJoiner : annoJoinerList) {
                    if (key.equals(annoJoiner.getKey())) {
                        if (V.notEmpty(annoJoiner.getJoin())) {
                            // 获取注解Table
                            return annoJoiner.getAlias() + "." + annoJoiner.getColumnName();
                        } else {
                            return (hasJoinTable ? "self." : "") + annoJoiner.getColumnName();
                        }
                    }
                }
            }
            return (hasJoinTable ? "self." : "") + BeanUtils.getColumnName(field);
        };
        // 忽略空字符串"",空集合等
        BiPredicate<Object, BindQuery> ignoreEmpty = (value, bindQuery) -> bindQuery != null &&
                (Strategy.IGNORE_EMPTY.equals(bindQuery.strategy()) && value instanceof String && S.isEmpty((String) value) // 忽略空字符串""
                        || Comparison.IN.equals(bindQuery.comparison()) && V.isEmpty(value)); // 忽略空集合
        // 获取Class类型
        Function<BindQuery, Class<?>> getClass = bindQuery -> bindQuery == null || bindQuery.entity() == NullType.class ? dto.getClass() : bindQuery.entity();
        // 获取属性名类型
        BiFunction<BindQuery, String, String> getFieldName = (bindQuery, defFieldName) -> bindQuery == null || S.isEmpty(bindQuery.column()) ? defFieldName : bindQuery.column();
        // 保护字段处理器
        DataEncryptHandler protectFieldHandler = ContextHolder.getBean(DataEncryptHandler.class);
        // 构建QueryWrapper
        for (Map.Entry<String, FieldAndValue> entry : fieldValuesMap.entrySet()) {
            FieldAndValue fieldAndValue = entry.getValue();
            Field field = fieldAndValue.getField();
            //忽略字段
            BindQuery query = field.getAnnotation(BindQuery.class);
            if(query == null) {// 不存在BindQuery注解
                TableField tableField = field.getAnnotation(TableField.class);
                //忽略注解 @TableField(exist = false) 的字段
                if (tableField != null && !tableField.exist()) {
                    continue;
                }
            }
            else if(query.ignore()){
                continue;
            }
            BindQuery.List queryList = field.getAnnotation(BindQuery.List.class);
            Object value = fieldAndValue.getValue();
            // 构建Query
            if (queryList != null) {
                List<BindQuery> bindQueryList = Arrays.stream(queryList.value()).filter(e -> !ignoreEmpty.test(value, e)).collect(Collectors.toList());
                wrapper.and(V.notEmpty(bindQueryList), queryWrapper -> {
                    Class<?> clazz = getClass.apply(query);
                    List<String> encryptFields = ParserCache.getProtectFieldList(clazz);
                    for (BindQuery bindQuery : bindQueryList) {
                        String columnName = buildColumnName.apply(bindQuery, field);
                        if (protectFieldHandler != null) {
                            String fieldName = getFieldName.apply(query, entry.getKey());
                            if (encryptFields.contains(fieldName)) {
                                log.debug("查询条件中包含加密字段 {}:{}，将加密后匹配密文", fieldName, value);
                                buildQuery(queryWrapper.or(), bindQuery, columnName, protectFieldHandler.encrypt(value.toString()));
                                continue;
                            }
                        }
                        buildQuery(queryWrapper.or(), bindQuery, columnName, value);
                    }
                });
            } else {
                if (query == null && V.isEmpty(value)) {
                    continue;
                }
                if (ignoreEmpty.test(value, query)) {
                    continue;
                }
                String columnName = buildColumnName.apply(query, field);
                if (protectFieldHandler != null) {
                    Class<?> clazz = getClass.apply(query);
                    String fieldName = getFieldName.apply(query, entry.getKey());
                    if (ParserCache.getProtectFieldList(clazz).contains(fieldName)) {
                        log.debug("查询条件中包含加密字段 {}:{}，将加密后匹配密文", fieldName, value);
                        buildQuery(wrapper, query, columnName, protectFieldHandler.encrypt(value.toString()));
                        continue;
                    }
                }
                buildQuery(wrapper, query, columnName, value);
            }
        }
        return wrapper;
    }

    /**
     * 建立条件
     *
     * @param wrapper    条件包装器
     * @param bindQuery  注解
     * @param columnName 列名
     * @param value      值
     */
    private static void buildQuery(QueryWrapper<?> wrapper, BindQuery bindQuery, String columnName, Object value) {
        Comparison comparison = bindQuery != null ? bindQuery.comparison() : Comparison.EQ;
        if (value == null) {
            if (bindQuery != null && bindQuery.strategy().equals(Strategy.INCLUDE_NULL) && comparison.equals(Comparison.EQ)) {
                wrapper.isNull(columnName);
            }
            return;
        }
        // 根据匹配方式构建查询
        WrapperHelper.buildQueryCriteria(wrapper, comparison, columnName, value);
    }

    /**
     * 提取非空字段及值
     *
     * @param dto
     * @param fields
     * @param <DTO>
     * @return
     */
    private static <DTO> LinkedHashMap<String, FieldAndValue> extractNotNullValues(DTO dto, Collection<String> fields, Pagination pagination) {
        Class<?> dtoClass = dto.getClass();
        // 转换
        List<Field> declaredFields = BeanUtils.extractAllFields(dtoClass, true);
        List<String> extractOrderFieldNames = extractOrderFieldNames(pagination);
        // 结果map：<字段名,字段对象和值>
        LinkedHashMap<String, FieldAndValue> resultMap = new LinkedHashMap<>(declaredFields.size());
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            // 非指定属性，非逻辑删除字段，跳过；
            if (V.notContains(fields, fieldName)) {
                //Date 属性放过
                if (!V.equals(field.getType(), Date.class) && !V.equals(field.getType(), LocalDate.class) && !V.equals(field.getType(), LocalDateTime.class)) {
                    continue;
                }
            }
            BindQuery bindQuery = field.getAnnotation(BindQuery.class);
            // 忽略指定ignore的字段
            if (bindQuery != null && bindQuery.ignore()) {
                continue;
            }
            // 有默认值的boolean类型，提示
            if (field.getType().getName().equals("boolean")) {
                TableLogic tableLogic = field.getAnnotation(TableLogic.class);
                // 非逻辑删除才提示
                if (tableLogic == null) {
                    TableField tableField = field.getAnnotation(TableField.class);
                    if(tableField == null || tableField.exist()) {
                        log.warn("{}.{} 字段类型为 boolean，其默认值将参与构建查询条件，可能导致结果与预期不符，建议调整为 Boolean 类型 或 指定 @BindQuery(ignore=true)", dtoClass.getSimpleName(), field.getName());
                    }
                }
            }
            Object value = null;
            try {
                String prefix = V.equals(boolean.class, field.getType()) ? "is" : "get";
                Method method = dtoClass.getMethod(prefix + S.capFirst(fieldName));
                value = method.invoke(dto);
            } catch (IllegalAccessException e) {
                log.error("通过反射获取属性值出错：{}", e.getMessage());
            } catch (NoSuchMethodException e) {
                log.debug("通过反射获取属性方法不存在：{}", e.getMessage());
            } catch (InvocationTargetException e) {
                log.warn("通过反射执行属性方法出错：{}", e.getMessage());
            }
            // 忽略逻辑删除字段，含有逻辑删除字段，并且值为false，则忽略
            if (field.isAnnotationPresent(TableLogic.class) && V.equals(false, value)) {
                continue;
            }

            Strategy strategy = bindQuery != null ? bindQuery.strategy() : Strategy.IGNORE_EMPTY;
            boolean collectThisField = false;
            // INCLUDE_NULL策略，包含null也收集
            if (strategy.equals(Strategy.INCLUDE_NULL)) {
                collectThisField = true;
            } else if (strategy.equals(Strategy.IGNORE_EMPTY) && V.notEmpty(value)) {
                collectThisField = true;
            } else if (strategy.equals(Strategy.INCLUDE_EMPTY) && value != null) {
                collectThisField = true;
            } else if (extractOrderFieldNames.contains(fieldName)) {
                collectThisField = true;
            }
            if (collectThisField) {
                resultMap.put(fieldName, new FieldAndValue(field, value));
            }
        }
        return resultMap;
    }

    /**
     * 保存字段Field对象和字段值
     */
    private static class FieldAndValue {
        private final Field field;
        private final Object value;

        public FieldAndValue(Field field, Object value) {
            this.field = field;
            this.value = value;
        }

        public Field getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * 检查是否包含列
     *
     * @param segments
     * @param idCol
     * @return
     */
    public static boolean checkHasColumn(NormalSegmentList segments, String idCol) {
        if (segments.size() > 0) {
            for (ISqlSegment segment : segments) {
                if (segment.getSqlSegment().equalsIgnoreCase(idCol)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否为排序字段
     *
     * @param pagination
     * @return
     */
    private static List<String> extractOrderFieldNames(Pagination pagination) {
        if (pagination == null || V.isEmpty(pagination.getOrderBy())) {
            return Collections.emptyList();
        }
        // 解析排序
        // orderBy=name:DESC,age:ASC,birthdate
        String[] orderByFields = S.split(pagination.getOrderBy());
        List<String> orderFields = new ArrayList<>(orderByFields.length);
        for (String field : orderByFields) {
            if (field.contains(":")) {
                field = S.substringBefore(field, ":");
            }
            orderFields.add(field);
        }
        return orderFields;
    }

}
