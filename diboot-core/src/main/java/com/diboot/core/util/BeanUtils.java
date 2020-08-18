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


import com.diboot.core.binding.copy.AcceptAnnoCopier;
import com.diboot.core.config.Cons;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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
    public static <T> List<T> convertList(List sourceList, Class<T> clazz){
        if(V.isEmpty(sourceList)){
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<>();
        // 类型相同，直接跳过
        if(clazz.getName().equals(sourceList.get(0).getClass().getName())){
            return sourceList;
        }
        // 不同，则转换
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
        if(V.isEmpty(propMap)){
            return;
        }
        List<Field> fields = extractAllFields(model.getClass());
        Map<String, Field> fieldNameMaps = convertToStringKeyObjectMap(fields, "name");
        for(Map.Entry<String, Object> entry : propMap.entrySet()){
            Field field = fieldNameMaps.get(entry.getKey());
            if(field != null){
                try{
                    Object value = convertValueToFieldType(entry.getValue(), field);
                    setProperty(model, entry.getKey(), value);
                }
                catch (Exception e){
                    log.warn("复制属性{}.{}异常: {}", model.getClass().getSimpleName(), entry.getKey(), e.getMessage());
                }
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
     * @param field
     * @param value
     */
    public static void setProperty(Object obj, String field, Object value) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        wrapper.setPropertyValue(field, value);
    }

    /**
     * 转换为field对应的类型
     * @param value
     * @param field
     * @return
     */
    public static Object convertValueToFieldType(Object value, Field field){
        String type = field.getGenericType().getTypeName();
        if(value.getClass().getName().equals(type)){
            return value;
        }
        if(Integer.class.getName().equals(type)){
            return Integer.parseInt(S.valueOf(value));
        }
        else if(Long.class.getName().equals(type)){
            return Long.parseLong(S.valueOf(value));
        }
        else if(Double.class.getName().equals(type)){
            return Double.parseDouble(S.valueOf(value));
        }
        else if(BigDecimal.class.getName().equals(type)){
            return new BigDecimal(S.valueOf(value));
        }
        else if(Float.class.getName().equals(type)){
            return Float.parseFloat(S.valueOf(value));
        }
        else if(Boolean.class.getName().equals(type)){
            return V.isTrue(S.valueOf(value));
        }
        else if(type.contains(Date.class.getSimpleName())){
            return D.fuzzyConvert(S.valueOf(value));
        }
        return value;
    }

    /***
     * Key-Object对象Map
     * @param allLists
     * @return
     */
    public static <T> Map<String, T> convertToStringKeyObjectMap(List<T> allLists, String... fields){
        if(allLists == null || allLists.isEmpty()){
            return Collections.EMPTY_MAP;
        }
        Map<String, T> allListMap = new LinkedHashMap<>(allLists.size());
        // 转换为map
        try{
            for(T model : allLists){
                String key = null;
                if(V.isEmpty(fields)){
                    //未指定字段，以id为key
                    key = getStringProperty(model, Cons.FieldName.id.name());
                }
                // 指定了一个字段，以该字段为key，类型同该字段
                else if(fields.length == 1){
                    key = getStringProperty(model, fields[0]);
                }
                else{ // 指定了多个字段，以字段S.join的结果为key，类型为String
                    List list = new ArrayList();
                    for(String fld : fields){
                        list.add(getProperty(model, fld));
                    }
                    key = S.join(list);
                }
                if(key != null){
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
     * Key-Object-List列表Map
     * @param allLists
     * @param fields
     * @param <T>
     * @return
     */
    public static <T> Map<String, List<T>> convertToStringKeyObjectListMap(List<T> allLists, String... fields){
        if (allLists == null || allLists.isEmpty()){
            return null;
        }
        Map<String, List<T>> allListMap = new LinkedHashMap<>(allLists.size());
        // 转换为map
        try {
            for (T model : allLists){
                String key = null;
                if(V.isEmpty(fields)){
                    //未指定字段，以id为key
                    key = getStringProperty(model, Cons.FieldName.id.name());
                }
                // 指定了一个字段，以该字段为key，类型同该字段
                else if(fields.length == 1){
                    key = getStringProperty(model, fields[0]);
                }
                else{ // 指定了多个字段，以字段S.join的结果为key，类型为String
                    List list = new ArrayList();
                    for(String fld : fields){
                        list.add(getProperty(model, fld));
                    }
                    key = S.join(list);
                }
                if(key != null){
                    List<T> list = allListMap.get(key);
                    if (list == null){
                        list = new ArrayList<T>();
                        allListMap.put(key, list);
                    }
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

    /***
     * 构建上下级关联的树形结构的model（上级parentId、子节点children），根节点=0
     * @param allNodes 所有节点对象
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes){
        return buildTree(allNodes, 0);
    }

    /***
     * 构建指定根节点的上下级关联的树形结构（上级parentId、子节点children）
     * @param allNodes 所有节点对象
     * @param rootNodeId 跟节点ID
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId){
        return buildTree(allNodes, rootNodeId, Cons.FieldName.parentId.name(), Cons.FieldName.children.name());
    }

    /***
     * 构建指定根节点的上下级关联的树形结构（上级parentId、子节点children）
     * @param allNodes 所有节点对象
     * @param rootNodeId 跟节点ID
     * @param parentIdFieldName 父节点属性名
     * @param childrenFieldName 子节点集合属性名
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId, String parentIdFieldName, String childrenFieldName){
        if(V.isEmpty(allNodes)){
            return null;
        }
        // 提取所有的top level对象
        List<T> topLevelModels = new ArrayList();
        for(T node : allNodes){
            Object parentId = getProperty(node, parentIdFieldName);
            if(parentId == null || V.fuzzyEqual(parentId, rootNodeId)){
                topLevelModels.add(node);
            }
            Object nodeId = getProperty(node, Cons.FieldName.id.name());
            if(V.equals(nodeId, parentId)){
                throw new BusinessException(Status.WARN_PERFORMANCE_ISSUE, "parentId关联自身，请检查！" + node.getClass().getSimpleName()+":"+nodeId);
            }
        }
        if(V.isEmpty(topLevelModels)){
            return topLevelModels;
        }
        // 遍历第一级节点，并挂载 children 子节点
        for(T node : allNodes) {
            Object nodeId = getProperty(node, Cons.FieldName.id.name());
            List<T> children = buildTreeChildren(nodeId, allNodes, parentIdFieldName, childrenFieldName);
            setProperty(node, childrenFieldName, children);
        }
        return topLevelModels;
    }

    /**
     * 递归构建树节点的子节点
     * @param parentId
     * @param nodeList
     * @param parentIdFieldName 父节点属性名
     * @param childrenFieldName 子节点集合属性名
     * @return
     */
    private static <T> List<T> buildTreeChildren(Object parentId, List<T> nodeList, String parentIdFieldName, String childrenFieldName) {
        List<T> children = null;
        for(T node : nodeList) {
            Object nodeParentId = getProperty(node, parentIdFieldName);
            if(nodeParentId != null && V.equals(nodeParentId, parentId)) {
                if(children == null){
                    children = new ArrayList<>();
                }
                children.add(node);
            }
        }
        if(children != null){
            for(T child : children) {
                Object nodeId = getProperty(child, Cons.FieldName.id.name());
                List<T> childNodeChildren = buildTreeChildren(nodeId, nodeList, parentIdFieldName, childrenFieldName);
                if(childNodeChildren == null) {
                    childNodeChildren = new ArrayList<>();
                }
                setProperty(child, childrenFieldName, childNodeChildren);
            }
        }
        return children;
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

    /***
     * 提取两个model的差异值，只对比指定字段
     * @param oldModel
     * @param newModel
     * @return
     */
    public static String extractDiff(BaseEntity oldModel, BaseEntity newModel, Set<String> fields){
        if(newModel == null || oldModel == null){
            log.warn("调用错误，Model不能为空！");
            return null;
        }
        Map<String, Object> oldMap = oldModel.toMap();
        Map<String, Object> newMap = newModel.toMap();
        Map<String, Object> result = new HashMap<>(oldMap.size()+newMap.size());
        for(Map.Entry<String, Object> entry : oldMap.entrySet()){
            if(IGNORE_FIELDS.contains(entry.getKey())){
                continue;
            }
            String oldValue = entry.getValue()!=null ? String.valueOf(entry.getValue()) : "";
            Object newValueObj = newMap.get(entry.getKey());
            String newValue = newValueObj!=null? String.valueOf(newValueObj) : "";
            // 设置变更的值
            boolean checkThisField = fields == null || fields.contains(entry.getKey());
            if(checkThisField && !oldValue.equals(newValue)){
                result.put(entry.getKey(), S.join(oldValue, CHANGE_FLAG, newValue));
            }
            // 从新的map中移除该key
            if(newValueObj!=null){
                newMap.remove(entry.getKey());
            }
        }
        if(!newMap.isEmpty()){
            for(Map.Entry<String, Object> entry : newMap.entrySet()){
                if(IGNORE_FIELDS.contains(entry.getKey())){
                    continue;
                }
                Object newValueObj = entry.getValue();
                String newValue = newValueObj!=null? String.valueOf(newValueObj) : "";
                // 设置变更的值
                if(fields==null || fields.contains(entry.getKey())){
                    result.put(entry.getKey(), S.join("", CHANGE_FLAG, newValue));
                }
            }
        }
        oldMap = null;
        newMap = null;
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
        List fieldValueList = new ArrayList();
        try{
            for(E object : objectList){
                Object fieldValue = getProperty(object, getterPropName);
                if(fieldValue != null && !fieldValueList.contains(fieldValue)){
                    fieldValueList.add(fieldValue);
                }
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
                Object value = null;
                if(valueMatchMap.containsKey(fieldValue)){
                    value = valueMatchMap.get(fieldValue);
                }
                else{
                    // 获取到当前的属性值
                    String fieldValueStr = getStringProperty(object, getterFieldName);
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
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            if(V.notEmpty(fields)){ //被重写属性，以子类override的为准
                Arrays.stream(fields).forEach((field)->{
                    if(!fieldNameSet.contains(field.getName())){
                        fieldList.add(field);
                        fieldNameSet.add(field.getName());
                    }
                });
            }
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }


    /**
     * 获取类所有属性（包含父类中属性）
     * @param clazz
     * @return
     */
    public static List<Field> extractFields(Class<?> clazz, Class<? extends Annotation> annotation){
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            if(V.notEmpty(fields)){ //被重写属性，以子类override的为准
                Arrays.stream(fields).forEach((field)->{
                    if(!fieldNameSet.contains(field.getName()) && field.getAnnotation(annotation) != null){
                        fieldList.add(field);
                        fieldNameSet.add(field.getName());
                    }
                });
            }
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 获取类的指定属性（包含父类中属性）
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field extractField(Class clazz, String fieldName){
        return ReflectionUtils.findField(clazz, fieldName);
    }

    /**
     * 获取目标类
     * @param instance
     * @return
     */
    public static Class getTargetClass(Object instance){
        Class targetClass = (instance instanceof Class)? (Class)instance : AopUtils.getTargetClass(instance);
        return targetClass;
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
     * 转换keyValue集合为Map
     * @param keyValueList
     * @return
     */
    public static Map<String, Object> convertKeyValueList2Map(List<KeyValue> keyValueList) {
        if(V.notEmpty(keyValueList)){
            return keyValueList.stream().collect(Collectors.toMap(KeyValue::getK, KeyValue::getV));
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

}
