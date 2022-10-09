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
package com.diboot.core.binding.parser;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diboot.core.binding.annotation.*;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.dynamic.AnnoJoiner;
import com.diboot.core.data.annotation.ProtectField;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 *  对象中的绑定注解 缓存管理类
 * @author mazc@dibo.ltd<br>
 * @version 2.0<br>
 * @date 2019/04/03 <br>
 */
@SuppressWarnings({"rawtypes", "JavaDoc"})
@Slf4j
public class ParserCache {
    /**
     * VO类-绑定注解缓存
     */
    private static final Map<Class<?>, BindAnnotationGroup> allVoBindAnnotationCacheMap = new ConcurrentHashMap<>();
    /**
     * dto类-BindQuery注解的缓存
     */
    private static final Map<String, List<AnnoJoiner>> dtoClassBindQueryCacheMap = new ConcurrentHashMap<>();
    /**
     * 保护字段缓存
     */
    private static final Map<String, List<String>> PROTECT_FIELD_MAP = new ConcurrentHashMap<>();

    /**
     * 用于查询注解是否是Bind相关注解
     */
    private static final Set<Class<?>> BIND_ANNOTATION_SET = new HashSet<>(Arrays.asList(
            BindDict.class, BindField.class, BindFieldList.class, BindEntity.class, BindEntityList.class, BindCount.class
    ));

    /**
     * 获取指定class对应的Bind相关注解
     * @param voClass
     * @return
     */
    public static BindAnnotationGroup getBindAnnotationGroup(Class<?> voClass){
        return allVoBindAnnotationCacheMap.computeIfAbsent(voClass, k -> {
            // 获取注解并缓存
            BindAnnotationGroup group = new BindAnnotationGroup();
            // 获取当前VO的所有字段
            List<Field> fields = BeanUtils.extractAllFields(voClass);
            if(V.notEmpty(fields)){
                //遍历属性
                for (Field field : fields) {
                    // 获取当前字段所有注解
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    if (V.isEmpty(annotations)) {
                        continue;
                    }
                    // 字段名称
                    String fieldName = field.getName();
                    // 字段类型
                    Class<?> setterObjClazz = field.getType();
                    // 如果是集合，获取其泛型参数class
                    if(setterObjClazz.equals(java.util.List.class) || setterObjClazz.equals(java.util.Collection.class)){
                        Type genericType = field.getGenericType();
                        if(genericType instanceof ParameterizedType){
                            ParameterizedType pt = (ParameterizedType) genericType;
                            setterObjClazz = (Class<?>)pt.getActualTypeArguments()[0];
                        }
                    }
                    for (Annotation annotation : annotations) {
                        // 是bind相关注解则添加
                        if(BIND_ANNOTATION_SET.contains(annotation.annotationType())) {
                            group.addBindAnnotation(fieldName, setterObjClazz, annotation);
                        }
                    }
                }
            }
            // 返回归类后的注解对象
            return group;
        });
    }

    /**
     * 是否有is_deleted列
     * @return
     */
    public static String getDeletedColumn(String table){
        PropInfo propInfo = BindingCacheManager.getPropInfoByTable(table);
        if(propInfo != null){
            return propInfo.getDeletedColumn();
        }
        log.debug("未能识别到逻辑删除字段, table={}", table);
        return null;
    }

    /**
     * 获取entity对应的表名
     * @param entityClass
     * @return
     */
    public static String getEntityTableName(Class<?> entityClass){
        EntityInfoCache entityInfoCache = BindingCacheManager.getEntityInfoByClass(entityClass);
        if(entityInfoCache != null){
            return entityInfoCache.getTableName();
        }
        else{
            TableName tableNameAnno = AnnotationUtils.findAnnotation(entityClass, TableName.class);
            if(tableNameAnno != null && V.notEmpty(tableNameAnno.value())){
                return tableNameAnno.value();
            }
            else{
                return S.toSnakeCase(entityClass.getSimpleName());
            }
        }
    }

    /**
     * 根据类的entity类名获取EntityClass（已废弃）
     * 请调用{@link BindingCacheManager#getEntityClassBySimpleName(String)}}
     * @return
     */
    @Deprecated
    public static Class<?> getEntityClassByClassName(String className){
        return BindingCacheManager.getEntityClassBySimpleName(className);
    }

    /**
     * 根据entity类获取mapper实例
     * @return
     */
    public static BaseMapper getMapperInstance(Class<?> entityClass){
        BaseMapper mapper = BindingCacheManager.getMapperByClass(entityClass);
        if(mapper == null){
            throw new InvalidUsageException("未找到 "+entityClass.getName()+" 的Mapper定义！");
        }
        return mapper;
    }

    /**
     * 当前DTO是否有Join绑定
     * @param dto dto对象
     * @param fieldNameSet 有值属性集合
     * @param <DTO>
     * @return
     */
    public static <DTO> boolean hasJoinTable(DTO dto, Collection<String> fieldNameSet){
        List<AnnoJoiner> annoList = getBindQueryAnnos(dto.getClass());
        if(V.notEmpty(annoList)){
            for(AnnoJoiner anno : annoList){
                if(V.notEmpty(anno.getJoin()) && V.contains(fieldNameSet, anno.getFieldName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取dto类中定义的BindQuery注解
     * @param dtoClass
     * @return
     */
    public static List<AnnoJoiner> getBindQueryAnnos(Class<?> dtoClass){
        String dtoClassName = dtoClass.getName();
        if(dtoClassBindQueryCacheMap.containsKey(dtoClassName)){
            return dtoClassBindQueryCacheMap.get(dtoClassName);
        }
        // 初始化
        List<AnnoJoiner> annos = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(1);
        Map<String, String> joinOn2Alias = new HashMap<>(8);
        // 构建AnnoJoiner
        BiConsumer<Field, BindQuery> buildAnnoJoiner = (field, query) -> {
            AnnoJoiner annoJoiner = new AnnoJoiner(field, query);
            // 关联对象，设置别名
            if (V.notEmpty(annoJoiner.getJoin())) {
                String key = annoJoiner.getJoin() + ":" + annoJoiner.getCondition();
                String alias = joinOn2Alias.get(key);
                if (alias == null) {
                    alias = "r" + index.getAndIncrement();
                    annoJoiner.setAlias(alias);
                    joinOn2Alias.put(key, alias);
                } else {
                    annoJoiner.setAlias(alias);
                }
                annoJoiner.parse();
            }
            annos.add(annoJoiner);
        };
        for (Field field : BeanUtils.extractFields(dtoClass, BindQuery.class)) {
            BindQuery query = field.getAnnotation(BindQuery.class);
            // 不可能为null
            if (query.ignore()) {
                continue;
            }
            buildAnnoJoiner.accept(field, query);
        }
        for (Field field : BeanUtils.extractFields(dtoClass, BindQuery.List.class)) {
            BindQuery.List queryList = field.getAnnotation(BindQuery.List.class);
            for (BindQuery bindQuery : queryList.value()) {
                buildAnnoJoiner.accept(field, bindQuery);
            }
        }
        dtoClassBindQueryCacheMap.put(dtoClassName, annos);
        return annos;
    }

    /**
     * 获取注解joiner
     * @param dtoClass
     * @param fieldNames
     * @return
     */
    public static List<AnnoJoiner> getAnnoJoiners(Class<?> dtoClass, Collection<String> fieldNames) {
        List<AnnoJoiner> annoList = getBindQueryAnnos(dtoClass);
        // 不过滤  返回全部
        if(fieldNames == null){
            return annoList;
        }
        // 过滤
        if(V.notEmpty(annoList)){
            List<AnnoJoiner> matchedAnnoList = new ArrayList<>();
            for(AnnoJoiner anno : annoList){
                if(fieldNames.contains(anno.getFieldName())){
                    matchedAnnoList.add(anno);
                }
            }
            return matchedAnnoList;
        }
        return Collections.emptyList();
    }

    /**
     * 获取该类保护字段属性名列表
     *
     * @param clazz 类型
     * @return 属性名列表
     */
    @NonNull
    public static List<String> getProtectFieldList(@NonNull Class<?> clazz) {
        return PROTECT_FIELD_MAP.computeIfAbsent(clazz.getName(), k -> {
            List<String> protectFieldList = new ArrayList<>(4);
            for (Field field : BeanUtils.extractFields(clazz, ProtectField.class)) {
                if (!field.getType().isAssignableFrom(String.class)) {
                    log.error("`@ProtectField` 仅支持 String 类型字段。");
                    continue;
                }
                protectFieldList.add(field.getName());
            }
            return protectFieldList.isEmpty() ? Collections.emptyList() : protectFieldList;
        });
    }
}
