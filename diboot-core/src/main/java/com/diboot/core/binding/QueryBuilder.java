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
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.diboot.core.binding.data.CheckpointType;
import com.diboot.core.binding.data.DataAccessAnnoCache;
import com.diboot.core.binding.data.DataAccessCheckInterface;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.binding.query.dynamic.AnnoJoiner;
import com.diboot.core.binding.query.dynamic.DynamicJoinQueryWrapper;
import com.diboot.core.binding.query.dynamic.ExtQueryWrapper;
import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * QueryWrapper构建器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/07/27
 */
public class QueryBuilder {
    private static Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    /**
     * Entity或者DTO对象转换为QueryWrapper
     * @param dto
     * @param <DTO>
     * @return
     */
    public static <DTO> QueryWrapper toQueryWrapper(DTO dto){
        return dtoToWrapper(dto, null);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     * @param dto
     * @param fields 指定参与转换的属性值
     * @param <DTO>
     * @return
     */
    public static <DTO> QueryWrapper toQueryWrapper(DTO dto, Collection<String> fields){
        return dtoToWrapper(dto, fields);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     * @param dto
     * @param <DTO>
     * @return
     */
    public static <DTO> ExtQueryWrapper toDynamicJoinQueryWrapper(DTO dto){
        return toDynamicJoinQueryWrapper(dto, null);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     * @param dto
     * @param fields 指定参与转换的属性值
     * @param <DTO>
     * @return
     */
    public static <DTO> ExtQueryWrapper toDynamicJoinQueryWrapper(DTO dto, Collection<String> fields){
        QueryWrapper queryWrapper = dtoToWrapper(dto, fields);
        if(queryWrapper instanceof DynamicJoinQueryWrapper == false){
            return (ExtQueryWrapper)queryWrapper;
        }
        return (DynamicJoinQueryWrapper)queryWrapper;
    }

    /**
     * Entity或者DTO对象转换为LambdaQueryWrapper
     * @param dto
     * @return
     */
    public static <DTO> LambdaQueryWrapper<DTO> toLambdaQueryWrapper(DTO dto){
        return (LambdaQueryWrapper<DTO>) toQueryWrapper(dto).lambda();
    }

    /**
     * Entity或者DTO对象转换为LambdaQueryWrapper
     * @param dto
     * @param fields 指定参与转换的属性值
     * @return
     */
    public static <DTO> LambdaQueryWrapper<DTO> toLambdaQueryWrapper(DTO dto, Collection<String> fields){
        return (LambdaQueryWrapper<DTO>) toQueryWrapper(dto, fields).lambda();
    }

    /**
     * 转换具体实现
     * @param dto
     * @return
     */
    private static <DTO> QueryWrapper<DTO> dtoToWrapper(DTO dto, Collection<String> fields){
        QueryWrapper wrapper;
        // 转换
        LinkedHashMap<String, Object> fieldValuesMap = extractNotNullValues(dto, fields);
        if(V.isEmpty(fieldValuesMap)){
            wrapper = new QueryWrapper<>();
            // 附加数据访问条件
            attachDataAccessCondition(wrapper, dto.getClass());
            return wrapper;
        }
        // 只解析有值的
        fields = fieldValuesMap.keySet();
        // 是否有join联表查询
        boolean hasJoinTable = ParserCache.hasJoinTable(dto, fields);
        if(hasJoinTable){
            wrapper = new DynamicJoinQueryWrapper<>(dto.getClass(), fields);
        }
        else{
            wrapper = new ExtQueryWrapper<>();
        }
        // 构建QueryWrapper
        for(Map.Entry<String, Object> entry : fieldValuesMap.entrySet()){
            Field field = BeanUtils.extractField(dto.getClass(), entry.getKey());
            //单表场景，忽略注解 @TableField(exist = false) 的字段
            if(hasJoinTable == false){
                TableField tableField = field.getAnnotation(TableField.class);
                if(tableField != null && tableField.exist() == false){
                    continue;
                }
            }
            //忽略字段
            BindQuery query = field.getAnnotation(BindQuery.class);
            if(query != null && query.ignore()){
                continue;
            }
            Object value = entry.getValue();
            // 对比类型
            Comparison comparison = Comparison.EQ;
            // 转换条件
            String columnName = getColumnName(field);;
            if(query != null){
                comparison = query.comparison();
                AnnoJoiner annoJoiner = ParserCache.getAnnoJoiner(dto.getClass(), entry.getKey());
                if(annoJoiner != null && V.notEmpty(annoJoiner.getJoin())){
                    // 获取注解Table
                    columnName = annoJoiner.getAlias() + "." + annoJoiner.getColumnName();
                }
                else if(hasJoinTable){
                    columnName = "self."+columnName;
                }
            }
            else if(hasJoinTable){
                columnName = "self."+columnName;
            }
            // 构建对象
            switch (comparison) {
                case EQ:
                    wrapper.eq(columnName, value);
                    break;
                case IN:
                    if(value.getClass().isArray()){
                        Object[] valueArray = (Object[])value;
                        if(valueArray.length == 1){
                            wrapper.eq(columnName, valueArray[0]);
                        }
                        else if(valueArray.length >= 2){
                            wrapper.in(columnName, valueArray);
                        }
                    }
                    else{
                        wrapper.in(columnName, value);
                    }
                    break;
                case CONTAINS:
                    wrapper.like(columnName, value);
                    break;
                case LIKE:
                    wrapper.like(columnName, value);
                    break;
                case STARTSWITH:
                    wrapper.likeRight(columnName, value);
                    break;
                case GT:
                    wrapper.gt(columnName, value);
                    break;
                case BETWEEN_BEGIN:
                    wrapper.ge(columnName, value);
                    break;
                case GE:
                    wrapper.ge(columnName, value);
                    break;
                case LT:
                    wrapper.lt(columnName, value);
                    break;
                case BETWEEN_END:
                    wrapper.le(columnName, value);
                    break;
                case LE:
                    wrapper.le(columnName, value);
                    break;
                case BETWEEN:
                    if(value.getClass().isArray()){
                        Object[] valueArray = (Object[])value;
                        if(valueArray.length == 1){
                            wrapper.ge(columnName, valueArray[0]);
                        }
                        else if(valueArray.length >= 2){
                            wrapper.between(columnName, valueArray[0], valueArray[1]);
                        }
                    }
                    else if(value instanceof List){
                        List valueList = (List)value;
                        if(valueList.size() == 1){
                            wrapper.ge(columnName, valueList.get(0));
                        }
                        else if(valueList.size() >= 2){
                            wrapper.between(columnName, valueList.get(0), valueList.get(1));
                        }
                    }
                    // 支持逗号分隔的字符串
                    else if(value instanceof String && ((String) value).contains(",")){
                        Object[] valueArray = ((String) value).split(",");
                        wrapper.between(columnName, valueArray[0], valueArray[1]);
                    }
                    else{
                        wrapper.ge(columnName, value);
                    }
                    break;
                default:
            }
        }
        // 附加数据访问条件
        attachDataAccessCondition(wrapper, dto.getClass());
        return wrapper;
    }

    // 扩展接口
    private static DataAccessCheckInterface dataAccessCheckInstance;
    private static boolean dataAccessCheckInstanceChecked = false;
    /**
     * 附加数据访问权限条件
     * @param queryWrapper
     * @param dtoClass
     * @param <DTO>
     */
    public static <DTO> void attachDataAccessCondition(QueryWrapper<DTO> queryWrapper, Class<DTO> dtoClass){
        if(dataAccessCheckInstanceChecked == false){
            dataAccessCheckInstance = ContextHelper.getBean(DataAccessCheckInterface.class);
            dataAccessCheckInstanceChecked = true;
        }
        if(dataAccessCheckInstance != null && DataAccessAnnoCache.hasDataAccessCheckpoint(dtoClass)){
            NormalSegmentList segments = queryWrapper.getExpression().getNormal();
            for(CheckpointType type : CheckpointType.values()){
                String idCol = DataAccessAnnoCache.getDataPermissionColumn(dtoClass, type);
                if(V.isEmpty(idCol)){
                    continue;
                }
                List<Long> idValues = dataAccessCheckInstance.getAccessibleIds(type);
                if(V.isEmpty(idValues)){
                    continue;
                }
                // 联表查询，附加别名
                if(queryWrapper instanceof DynamicJoinQueryWrapper){
                    idCol = "self."+idCol;
                }
                // 检查是否已包含该条件，如是则warn并退出
                if(checkHasColumn(segments, idCol)){
                    log.warn("附加数据访问条件未生效，因查询条件已包含列: " + idCol);
                    continue;
                }
                if(idValues.size() == 1){
                    queryWrapper.eq(idCol, idValues.get(0));
                }
                else{
                    queryWrapper.in(idCol, idValues);
                }
            }
        }
    }

    /**
     * 获取数据表的列名（驼峰转下划线蛇形命名）
     * <br>
     * 列名取值优先级： @BindQuery.field > @TableField.value > field.name
     *
     * @param field
     * @return
     */
    public static String getColumnName(Field field){
        String columnName = null;
        if (field.isAnnotationPresent(BindQuery.class)) {
            columnName = field.getAnnotation(BindQuery.class).field();
            if(V.notEmpty(columnName)){
                columnName = S.toSnakeCase(columnName);
            }
        }
        else if (field.isAnnotationPresent(TableField.class)) {
            columnName = field.getAnnotation(TableField.class).value();
        }
        return V.notEmpty(columnName) ? columnName : S.toSnakeCase(field.getName());
    }

    /**
     * 提取非空字段及值
     * @param dto
     * @param fields
     * @param <DTO>
     * @return
     */
    private static <DTO> LinkedHashMap<String, Object> extractNotNullValues(DTO dto, Collection<String> fields){
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        Class<?> dtoClass = dto.getClass();

        // 转换
        List<Field> declaredFields = BeanUtils.extractAllFields(dtoClass);
        for (Field field : declaredFields) {
            // 非指定属性，非逻辑删除字段，跳过；
            if (fields != null && !fields.contains(field.getName())) {
                //Date 属性放过
                if (!V.equals(field.getType().getName(), "java.util.Date")) {
                    continue;
                }
            }
            //忽略static，以及final，transient
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            boolean isTransient = Modifier.isTransient(field.getModifiers());
            if (isStatic || isFinal || isTransient) {
                continue;
            }
            //打开私有访问 获取值
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(dto);
                if (V.isEmpty(value)) {
                    String prefix = V.equals("boolean", field.getType().getName()) ?  "is" : "get";
                    Method method = dtoClass.getMethod(prefix + S.capFirst(field.getName()));
                    value = method.invoke(dto);
                }
            } catch (IllegalAccessException e) {
                log.error("通过反射获取属性值出错：{}", e.getMessage());
            } catch (NoSuchMethodException e) {
                log.warn("通过反射获取属性方法不存在：{}", e.getMessage());
            } catch (InvocationTargetException e) {
                log.warn("通过反射执行属性方法出错：{}", e.getMessage());
            }
            // 忽略逻辑删除字段
            if(Cons.FieldName.deleted.name().equals(field.getName())
                    && "boolean".equals(field.getType().getName())
                    && (Boolean)value == false
            ){
                continue;
            }
            if (value != null) {
                resultMap.put(field.getName(), value);
            }
        }
        return resultMap;
    }

    /**
     * 检查是否包含列
     * @param segments
     * @param idCol
     * @return
     */
    public static boolean checkHasColumn(NormalSegmentList segments, String idCol){
        if(segments.size() > 0){
            Iterator<ISqlSegment> iterable = segments.iterator();
            while(iterable.hasNext()){
                ISqlSegment segment = iterable.next();
                if(segment.getSqlSegment().equalsIgnoreCase(idCol)){
                    return true;
                }
            }
        }
        return false;
    }
}