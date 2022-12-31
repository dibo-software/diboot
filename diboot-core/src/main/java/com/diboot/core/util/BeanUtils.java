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
package com.diboot.core.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.config.Cons;
import com.diboot.core.converter.*;
import com.diboot.core.data.copy.AcceptAnnoCopier;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Status;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Bean相关处理工具类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
@SuppressWarnings({"unchecked", "rawtypes", "JavaDoc", "unused"})
public class BeanUtils {
    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    /**
     * 连接符号
     */
    private static final String CHANGE_FLAG = "->";

    /**
     * 忽略对比的字段
     */
    private static final Set<String> IGNORE_FIELDS = new HashSet<String>(){{
        add(Cons.FieldName.createTime.name());
    }};

    /**
     * Copy属性到另一个对象
     * @param source
     * @param target
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Object copyProperties(Object source, Object target){
        // 链式调用无法使用BeanCopier拷贝，换用BeanUtils
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        // 处理Accept注解标识的不同字段名拷贝
        AcceptAnnoCopier.copyAcceptProperties(source, target);
        return target;
    }

    /***
     * 将对象转换为另外的对象实例
     * @param source
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T convert(Object source, Class<T> clazz){
        if(source == null){
            return null;
        }
        T target = null;
        try{
            target = clazz.getConstructor().newInstance();
            copyProperties(source, target);
        }
        catch (Exception e){
            log.warn("对象转换异常, class="+clazz.getName());
        }
        return target;
    }

    /***
     * 将对象转换为另外的对象实例
     * @param sourceList
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> convertList(List<?> sourceList, Class<T> clazz) {
        if(V.isEmpty(sourceList)){
            return Collections.emptyList();
        }
        // 类型相同，直接跳过
        if (clazz.equals(sourceList.get(0).getClass())) {
            return (List<T>) sourceList;
        }
        // 不同，则转换
        List<T> resultList = new ArrayList<>(sourceList.size());
        try{
            for(Object source : sourceList){
                T target = clazz.getConstructor().newInstance();
                copyProperties(source, target);
                resultList.add(target);
            }
        }
        catch (Exception e){
            log.error("对象转换异常, class: {}, error: {}", clazz.getName(), e);
            return Collections.emptyList();
        }
        return resultList;
    }

    /***
     * 附加Map中的属性值到Model
     * @param model
     * @param propMap
     */
    public static void bindProperties(Object model, Map<String, Object> propMap){
        if (V.isAnyEmpty(model, propMap)) {
            return;
        }
        BeanWrapper beanWrapper = getBeanWrapper(model);
        for(Map.Entry<String, Object> entry : propMap.entrySet()){
            try{
                beanWrapper.setPropertyValue(entry.getKey(), entry.getValue());
            }
            catch (Exception e){
                log.warn("复制属性{}.{}异常: {}", model.getClass().getSimpleName(), entry.getKey(), e.getMessage());
            }
        }
    }

    /***
     * 获取对象的属性值
     * @param obj
     * @param field
     * @return
     */
    public static Object getProperty(Object obj, String field){
        if(obj instanceof Map){
            Map objMap = (Map)obj;
            return objMap.get(field);
        }
        try {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
            return wrapper.getPropertyValue(field);
        }
        catch (Exception e) {
            log.warn("获取对象属性值出错，返回null", e);
        }
        return null;
    }

    /***
     * 获取对象的属性值并转换为String
     * @param obj
     * @param field
     * @return
     */
    public static String getStringProperty(Object obj, String field){
        Object property = getProperty(obj, field);
        if(property == null){
            return null;
        }
        return String.valueOf(property);
    }

    /***
     * 设置属性值
     * @param obj
     */
    public static BeanWrapper getBeanWrapper(Object obj) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        if(wrapper.getConversionService() == null){
            ConversionService conversionService = ContextHelper.getBean(EnhancedConversionService.class);
            wrapper.setConversionService(conversionService);
        }
        return wrapper;
    }

    /***
     * 设置属性值
     * @param obj
     * @param field
     * @param value
     */
    public static void setProperty(Object obj, String field, Object value) {
        BeanWrapper wrapper = getBeanWrapper(obj);
        wrapper.setPropertyValue(field, value);
    }

    /**
     * 类型class对象-转换方法
     */
    private static final Map<Class<?>, Function<String, Object>> fieldConverterMap = new HashMap<Class<?>, Function<String, Object>>() {{
        put(Integer.class, Integer::parseInt);
        put(Long.class, Long::parseLong);
        put(Double.class, Double::parseDouble);
        put(BigDecimal.class, BigDecimal::new);
        put(Float.class, Float::parseFloat);
        put(Boolean.class, V::isTrue);
        put(Date.class, D::fuzzyConvert);
    }};

    /**
     * 转换为field对应的类型
     * @param value
     * @param field
     * @return
     */
    public static Object convertValueToFieldType(Object value, Field field){
        if(value == null){
            return null;
        }
        return convertValueToFieldType(value, field.getType());
    }

    /**
     * 转换为field对应的类型
     * @param value
     * @param fieldType
     * @return
     */
    public static Object convertValueToFieldType(Object value, Class<?> fieldType){
        if(value == null){
            return null;
        }
        if (value.getClass().equals(fieldType)) {
            return value;
        }
        String valueStr = S.valueOf(value);
        if (fieldConverterMap.containsKey(fieldType)) {
            return fieldConverterMap.get(fieldType).apply(valueStr);
        } else if (LocalDate.class.equals(fieldType) || LocalDateTime.class.equals(fieldType)) {
            Date dateVal = (value instanceof Date) ? (Date) value : D.fuzzyConvert(valueStr);
            if (dateVal == null) {
                return null;
            }
            ZonedDateTime zonedDateTime = dateVal.toInstant().atZone(ZoneId.systemDefault());
            return LocalDateTime.class.equals(fieldType) ? zonedDateTime.toLocalDateTime() : zonedDateTime.toLocalDate();
        } else if (Serializable.class.isAssignableFrom(fieldType)) {
            return JSON.parseObject(valueStr, fieldType);
        }
        return value;
    }

    /***
     * Key-Object对象Map
     * @param allLists
     * @param getterFns
     * @return
     */
    public static <T> Map<String, T> convertToStringKeyObjectMap(List<T> allLists, IGetter<T>... getterFns){
        String[] fields = convertGettersToFields(getterFns);
        return convertToStringKeyObjectMap(allLists, fields);
    }

    /***
     * Key-Object对象Map
     * @param allLists
     * @param fields
     * @return
     */
    public static <T> Map<String, T> convertToStringKeyObjectMap(List<T> allLists, String... fields){
        if(allLists == null || allLists.isEmpty()){
            return Collections.EMPTY_MAP;
        }
        Map<String, T> allListMap = new LinkedHashMap<>(allLists.size());
        ModelKeyGenerator keyGenerator = new ModelKeyGenerator(fields);
        // 转换为map
        try {
            for (T model : allLists) {
                String key = keyGenerator.generate(model);
                if (key != null) {
                    allListMap.put(key, model);
                }
                else{
                    log.warn(model.getClass().getName() + " 的属性 "+fields[0]+" 值存在 null，转换结果需要确认!");
                }
            }
        }
        catch(Exception e){
            log.warn("转换key-model异常", e);
        }
        return allListMap;
    }


    /***
     * Key-Object对象Map
     * @param allLists
     * @param getterFns
     * @return
     */
    public static <T> Map<String, List<T>> convertToStringKeyObjectListMap(List<T> allLists, IGetter<T>... getterFns){
        String[] fields = convertGettersToFields(getterFns);
        return convertToStringKeyObjectListMap(allLists, fields);
    }

    /***
     * Key-Object-List列表Map
     * @param allLists
     * @param fields
     * @param <T>
     * @return
     */
    public static <T> Map<String, List<T>> convertToStringKeyObjectListMap(List<T> allLists, String... fields){
        if (V.isEmpty(allLists)) {
            return Collections.emptyMap();
        }
        Map<String, List<T>> allListMap = new LinkedHashMap<>(allLists.size());
        ModelKeyGenerator keyGenerator = new ModelKeyGenerator(fields);
        // 转换为map
        try {
            for (T model : allLists) {
                String key = keyGenerator.generate(model);
                if(key != null){
                    List<T> list = allListMap.computeIfAbsent(key, k -> new ArrayList<>());
                    list.add(model);
                }
                else{
                    log.warn(model.getClass().getName() + " 的属性 "+fields[0]+" 值存在 null，转换结果需要确认!");
                }
            }
        } catch (Exception e){
            log.warn("转换key-model-list异常", e);
        }

        return allListMap;
    }

    /**
     * key生成器，按照传入的fields字段列表给指定对象生成key
     */
    private static class ModelKeyGenerator {
        private final String[] fields;
        /**
         * fields是否为空
         */
        private final boolean isFieldsEmpty;
        /**
         * fields是否只有一个元素
         */
        private final boolean isFieldsOnlyOne;

        public ModelKeyGenerator(String[] fields) {
            this.fields = fields;
            isFieldsEmpty = V.isEmpty(fields);
            isFieldsOnlyOne = !isFieldsEmpty && fields.length == 1;
        }
        /**
         * 从model中提取指定字段生成key，如果为指定字段则默认为id字段
         * 不建议大量数据循环时调用
         *
         * @param model  提取对象
         * @return model中字段生成的key
         */
        public String generate(Object model) {
            String key = null;
            if (isFieldsEmpty) {
                //未指定字段，以id为key
                return getStringProperty(model, Cons.FieldName.id.name());
            }
            // 指定了一个字段，以该字段为key，类型同该字段
            else if (isFieldsOnlyOne) {
                return getStringProperty(model, fields[0]);
            } else {
                // 指定了多个字段，以字段S.join的结果为key，类型为String
                List<Object> list = new ArrayList(fields.length);
                for (String field : fields) {
                    list.add(getProperty(model, field));
                }
                return S.join(list);
            }
        }
    }



    /***
     * 构建上下级关联的树形结构的model（上级parentId、子节点children），根节点=0
     * @param allNodes 所有节点对象
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes){
        return buildTree(allNodes, 0L);
    }

    /***
     * 构建指定根节点的上下级关联的树形结构（主键id，上级属性parentId、子节点属性children）
     * @param allNodes 所有节点对象
     * @param rootNodeId 跟节点ID
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId){
        return buildTree(allNodes, rootNodeId, Cons.FieldName.id.name(), Cons.FieldName.parentId.name(), Cons.FieldName.children.name());
    }

    /***
     * 构建指定根节点的上下级关联的树形结构（主键指定，上级属性parentId、子节点属性children）
     * @param allNodes 所有节点对象
     * @param rootNodeId 根节点ID
     * @param idFieldName 主键属性名
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId, String idFieldName){
        return buildTree(allNodes, rootNodeId, idFieldName, Cons.FieldName.parentId.name(), Cons.FieldName.children.name());
    }

    /***
     * 构建指定根节点的上下级关联的树形结构（指定主键属性，上级属性、子节点属性名）
     * @param allNodes 所有节点对象
     * @param rootNodeId 根节点ID
     * @param idFieldName 主键属性名
     * @param parentIdFieldName 父节点属性名
     * @param childrenFieldName 子节点集合属性名
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId, String idFieldName, String parentIdFieldName, String childrenFieldName){
        if(V.isEmpty(allNodes)){
            return null;
        }
        Map<Object, List<T>> parentId2ListMap = new HashMap<>();
        // 提取所有的top level对象
        for (T node : allNodes) {
            Object parentId = getProperty(node, parentIdFieldName);
            Object nodeId = getProperty(node, idFieldName);
            if(V.equals(nodeId, parentId)){
                throw new BusinessException(Status.WARN_PERFORMANCE_ISSUE, "parentId关联自身，请检查！" + node.getClass().getSimpleName()+":"+nodeId);
            }
            parentId2ListMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }
        List<T> topLevelModels = parentId2ListMap.get(rootNodeId);
        if (V.isEmpty(topLevelModels)) {
            return Collections.emptyList();
        }
        buildTreeChildren(topLevelModels, parentId2ListMap, idFieldName, childrenFieldName);
        return topLevelModels;
    }

    /**
     * 递归构建树节点的子节点
     *
     * @param topLevelModels
     * @param parentId2ListMap
     * @param idFieldName
     * @param childrenFieldName
     * @param <T>
     */
    public static <T> void buildTreeChildren(List<T> topLevelModels, Map<Object, List<T>> parentId2ListMap, String idFieldName, String childrenFieldName) {
        for (T item : topLevelModels) {
            Object nodeId = getProperty(item, idFieldName);
            if (!parentId2ListMap.containsKey(nodeId)) {
                continue;
            }
            List<T> children = parentId2ListMap.get(nodeId);
            setProperty(item, childrenFieldName, children);
            buildTreeChildren(children, parentId2ListMap, idFieldName, childrenFieldName);
        }
    }

    /***
     * 提取两个model的差异值
     * @param oldModel
     * @param newModel
     * @return
     */
    public static String extractDiff(BaseEntity oldModel, BaseEntity newModel){
        return extractDiff(oldModel, newModel, null);
    }

    /**
     * 提取两个model的差异值，只对比指定字段
     * 使用默认的忽略字段
     *
     * @param oldModel
     * @param newModel
     * @param fields   对比字段
     * @return
     */
    public static String extractDiff(BaseEntity oldModel, BaseEntity newModel, Set<String> fields) {
        return extractDiff(oldModel, newModel, fields, IGNORE_FIELDS);
    }

    /***
     * 提取两个model的差异值，只对比指定字段
     * @param oldModel
     * @param newModel
     * @param fields 对比字段
     * @param ignoreFields 不对比的字段
     * @return
     */
    public static String extractDiff(BaseEntity oldModel, BaseEntity newModel, Set<String> fields, Set<String> ignoreFields) {
        if(newModel == null || oldModel == null){
            log.warn("调用错误，Model不能为空！");
            return null;
        }
        Map<String, Object> oldMap = oldModel.toMap();
        Map<String, Object> newMap = newModel.toMap();
        Map<String, Object> result = new HashMap<>(oldMap.size()+newMap.size());
        for(Map.Entry<String, Object> entry : oldMap.entrySet()) {
            String key = entry.getKey();
            if (ignoreFields.contains(key)) {
                continue;
            }
            String oldValue = S.defaultValueOf(entry.getValue());
            Object newValueObj = newMap.get(key);
            String newValue = S.defaultValueOf(newValueObj);
            // 设置变更的值
            boolean checkThisField = fields == null || fields.contains(key);
            if (checkThisField && !oldValue.equals(newValue)) {
                result.put(key, S.join(oldValue, CHANGE_FLAG, newValue));
            }
            // 从新的map中移除该key
            if (newValueObj != null) {
                newMap.remove(key);
            }
        }
        if(!newMap.isEmpty()){
            for(Map.Entry<String, Object> entry : newMap.entrySet()) {
                String key = entry.getKey();
                if (ignoreFields.contains(key)) {
                    continue;
                }
                String newValue = S.defaultValueOf(entry.getValue());
                // 设置变更的值
                if (fields == null || fields.contains(key)) {
                    result.put(key, S.join("", CHANGE_FLAG, newValue));
                }
            }
        }
        // 转换结果为String
        return JSON.toJSONString(result);
    }

    /**
     * 从list对象列表中提取指定属性值到新的List
     * @param objectList 对象list
     * @param getterFn get方法
     * @param <T>
     * @return
     */
    public static <E,T> List collectToList(List<E> objectList, IGetter<T> getterFn){
        if(V.isEmpty(objectList)){
           return Collections.emptyList();
        }
        String getterPropName = convertToFieldName(getterFn);
        return collectToList(objectList, getterPropName);
    }

    /**
     * 从list对象列表中提取Id主键值到新的List
     * @param objectList 对象list
     * @param <E>
     * @return
     */
    public static <E> List collectIdToList(List<E> objectList){
        if(V.isEmpty(objectList)){
            return Collections.emptyList();
        }
        return collectToList(objectList, Cons.FieldName.id.name());
    }

    /***
     * 从list对象列表中提取指定属性值到新的List
     * @param objectList
     * @param getterPropName
     * @param <E>
     * @return
     */
    public static <E> List collectToList(List<E> objectList, String getterPropName){
        if(V.isEmpty(objectList)){
            return Collections.emptyList();
        }
        List fieldValueList = new ArrayList(objectList.size());
        try{
            for(E object : objectList){
                Object fieldValue = getProperty(object, getterPropName);
                // E类型中的提取的字段值不需要进行重复判断，如果一定要查重，那应该使用Set代替List
                if (fieldValue != null) {
                    if (fieldValue instanceof Collection) {
                        fieldValueList.addAll((Collection) fieldValue);
                    } else {
                        fieldValueList.add(fieldValue);
                    }
                }
            }
        }
        catch (Exception e){
            log.warn("提取属性值异常, getterPropName="+getterPropName, e);
        }
        return fieldValueList;
    }

    /***
     * 从list对象列表中提取指定属性值到新的List
     * @param objectList
     * @param getterPropName
     * @param hasNullFlags 是否有null值标记参数
     * @param <E>
     * @return
     */
    @Deprecated
    public static <E> List collectToList(List<E> objectList, String getterPropName, boolean[] hasNullFlags){
        if(V.isEmpty(objectList)){
            return Collections.emptyList();
        }
        List fieldValueList = new ArrayList(objectList.size());
        try{
            for(E object : objectList){
                Object fieldValue = getProperty(object, getterPropName);
                if(fieldValue == null){
                    hasNullFlags[0] = true;
                }
                // E类型中的提取的字段值不需要进行重复判断，如果一定要查重，那应该使用Set代替List
                fieldValueList.add(fieldValue);
            }
        }
        catch (Exception e){
            log.warn("提取属性值异常, getterPropName="+getterPropName, e);
        }
        return fieldValueList;
    }

    /**
     * 绑定map中的属性值到list
     * @param setFieldFn
     * @param getFieldFun
     * @param fromList
     * @param valueMatchMap
     * @param <T1>
     */
    public static <T1,T2,R,E> void bindPropValueOfList(ISetter<T1,R> setFieldFn, List<E> fromList, IGetter<T2> getFieldFun, Map valueMatchMap){
        if(V.isEmpty(fromList)){
            return;
        }
        // function转换为字段名
        String setterFieldName = convertToFieldName(setFieldFn), getterFieldName = convertToFieldName(getFieldFun);
        bindPropValueOfList(setterFieldName, fromList, getterFieldName, valueMatchMap);
    }

    /***
     * 从对象集合提取某个属性值到list中
     * @param setterFieldName
     * @param fromList
     * @param getterFieldName
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindPropValueOfList(String setterFieldName, List<E> fromList, String getterFieldName, Map valueMatchMap){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        try{
            for(E object : fromList){
                Object fieldValue = getProperty(object, getterFieldName);
                // 该obj的字段值为空，在Map中也必然不存在对应值
                if (V.isEmpty(fieldValue)) {
                    continue;
                }
                Object value;
                if(valueMatchMap.containsKey(fieldValue)){
                    value = valueMatchMap.get(fieldValue);
                }
                else{
                    // 可能是类型不匹配，转为String尝试
                    String fieldValueStr = String.valueOf(fieldValue);
                    // 获取到当前的value
                    value = valueMatchMap.get(fieldValueStr);
                }
                // 赋值
                setProperty(object, setterFieldName, value);
            }
        }
        catch (Exception e){
            log.warn("设置属性值异常, setterFieldName="+setterFieldName, e);
        }
    }

    /**
     * 克隆对象
     * @param ent
     * @param <T>
     * @return
     */
    public static <T> T cloneBean(T ent){
        // 克隆对象
        try{
            T cloneObj = (T)org.springframework.beans.BeanUtils.instantiateClass(ent.getClass());
            copyProperties(ent ,cloneObj);
            return cloneObj;
        }
        catch (Exception e){
            log.warn("Clone Object "+ent.getClass().getSimpleName()+" error", e);
            return ent;
        }
    }

    /***
     * 转换方法引用为属性名
     * @param fn
     * @return
     */
    public static <T> String convertToFieldName(IGetter<T> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return PropertyNamer.methodToProperty(lambda.getImplMethodName());
    }

    /***
     * 转换方法引用为属性名
     * @param fn
     * @return
     */
    public static <T,R> String convertToFieldName(ISetter<T,R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return PropertyNamer.methodToProperty(lambda.getImplMethodName());
    }

     /**
     * 获取类所有属性（包含父类中属性）
     * @param clazz
     * @return
     */
    public static List<Field> extractAllFields(Class clazz){
        return extractClassFields(clazz, null);
    }

    /**
     * 获取类所有属性（包含父类中属性）
     * @param clazz
     * @return
     */
    public static List<Field> extractFields(Class<?> clazz, Class<? extends Annotation> annotation){
        return extractClassFields(clazz, annotation);
    }

    /**
     * 获取类的指定属性（包含父类中属性）
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field extractField(Class<?> clazz, String fieldName) {
        return ReflectionUtils.findField(clazz, fieldName);
    }

    /**
     * 获取数据表的列名（驼峰转下划线蛇形命名）
     * <br>
     * 列名取值优先级： @TableField.value > field.name
     *
     * @param field
     * @return
     */
    public static String getColumnName(Field field) {
        String columnName = null;
        if (field.isAnnotationPresent(TableField.class)) {
            columnName = field.getAnnotation(TableField.class).value();
        }
        else if(field.isAnnotationPresent(TableId.class)) {
            columnName = field.getAnnotation(TableId.class).value();
        }
        return S.getIfEmpty(columnName, () -> S.toSnakeCase(field.getName()));
    }

    /**
     * 获取目标类
     * @param instance
     * @return
     */
    public static Class<?> getTargetClass(Object instance) {
        return (instance instanceof Class) ? (Class<?>) instance : AopProxyUtils.ultimateTargetClass(instance);
    }

    /**
     * 从实例中获取目标对象的泛型定义类class
     * @param instance 对象实例
     * @param index
     * @return
     */
    public static Class getGenericityClass(Object instance, int index){
        Class hostClass = getTargetClass(instance);
        ResolvableType resolvableType = ResolvableType.forClass(hostClass).getSuperType();
        ResolvableType[] types = resolvableType.getGenerics();
        if(V.isEmpty(types) || index >= types.length){
            types = resolvableType.getSuperType().getGenerics();
        }
        if(V.notEmpty(types) && types.length > index){
            return types[index].resolve();
        }
        log.debug("无法从 {} 类定义中获取泛型类{}", hostClass.getName(), index);
        return null;
    }

    /**
     * 转换labelValueList为Map
     * <p>
     * 需确保Label唯一
     *
     * @param labelValueList
     * @return
     */
    public static Map<String, Object> convertLabelValueList2Map(List<LabelValue> labelValueList) {
        if (V.notEmpty(labelValueList)) {
            return labelValueList.stream().collect(Collectors.toMap(LabelValue::getLabel, LabelValue::getValue));
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 根据指定Key对list去重
     * @param list
     * @param getterFn
     * @param <T>
     * @return 去重后的list
     */
    public static <T> List<T> distinctByKey(List<T> list, Function<? super T, ?> getterFn){
        return list.stream().filter(distinctPredicate(getterFn)).collect(Collectors.toList());
    }

    /**
     * 去重的辅助方法
     * @param getterFn
     * @param <T>
     * @return
     */
    private static <T> Predicate<T> distinctPredicate(Function<? super T, ?> getterFn) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(getterFn.apply(t));
    }

    /***
     * 获取类对应的Lambda
     * @param fn
     * @return
     */
    public static SerializedLambda getSerializedLambda(Serializable fn){
        SerializedLambda lambda = null;
        try{
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            lambda = (SerializedLambda) method.invoke(fn);
        }
        catch (Exception e){
            log.error("获取SerializedLambda异常, class="+fn.getClass().getSimpleName(), e);
        }
        return lambda;
    }

    /**
     * 转换Getter数组为字段名数组
     * @param getterFns
     * @param <T>
     * @return
     */
    @SafeVarargs
    private static <T> String[] convertGettersToFields(IGetter<T>... getterFns){
        if (V.isEmpty(getterFns)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        int length = getterFns.length;
        String[] fields = new String[length];
        for (int i = 0; i < length; i++) {
            fields[i] = convertToFieldName(getterFns[i]);
        }
        return fields;
    }

    /**
     * 清除属性值值
     *
     * @param object        对象
     * @param fieldNameList 属性名称列表
     */
    public static void clearFieldValue(Object object, List<String> fieldNameList) {
        if (fieldNameList == null) {
            return;
        }
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
        for (String fieldName : fieldNameList) {
            wrapper.setPropertyValue(fieldName, null);
        }
    }

    /**
     * 转换集合中的string类型id值为指定类型
     * @param values
     * @param fieldType
     * @return
     */
    public static Collection convertIdValuesToType(Collection<?> values, Class fieldType) {
        if(V.isEmpty(values)) {
            return values;
        }
        if(V.equals(values.iterator().next().getClass(), fieldType)) {
            return values;
        }
        Collection formatValues = new ArrayList(values.size());
        for(Object value : values) {
            formatValues.add(convertIdValueToType(value, fieldType));
        }
        return formatValues;
    }

    /**
     * 转换string类型id值为指定类型
     * @param value
     * @param fieldType
     * @return
     */
    public static Object convertIdValueToType(Object value, Class fieldType) {
        if(V.isEmpty(value)) {
            return null;
        }
        if(Long.class.equals(fieldType)) {
            return Long.parseLong(S.valueOf(value));
        }
        if(Integer.class.equals(fieldType)) {
            return Integer.parseInt(S.valueOf(value));
        }
        return value;
    }

    /**
     * 初始化fields
     * @param beanClazz
     * @return
     */
    private static List<Field> extractClassFields(Class<?> beanClazz, Class<? extends Annotation> annotation){
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();
        loopFindFields(beanClazz, annotation, fieldList, fieldNameSet);
        return fieldList;
    }

    /**
     * 循环向上查找fields
     * @param beanClazz
     * @param annotation
     * @param fieldList
     * @param fieldNameSet
     */
    private static void loopFindFields(Class<?> beanClazz, Class<? extends Annotation> annotation, List<Field> fieldList, Set<String> fieldNameSet){
        if(beanClazz == null) {
            return;
        }
        Field[] fields = beanClazz.getDeclaredFields();
        if (V.notEmpty(fields)) {
            for (Field field : fields) {
                // 被重写属性，以子类的为准
                if (!fieldNameSet.add(field.getName())) {
                    continue;
                }
                if (annotation == null || field.getAnnotation(annotation) != null) {
                    fieldList.add(field);
                }
            }
        }
        loopFindFields(beanClazz.getSuperclass(), annotation, fieldList, fieldNameSet);
    }

}
