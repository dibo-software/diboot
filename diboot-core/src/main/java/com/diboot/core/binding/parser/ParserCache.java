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
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.dynamic.AnnoJoiner;
import com.diboot.core.data.annotation.ProtectField;
import com.diboot.core.data.encrypt.IEncryptStrategy;
import com.diboot.core.data.mask.IMaskStrategy;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import javax.lang.model.type.NullType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  对象中的绑定注解 缓存管理类
 * @author mazc@dibo.ltd<br>
 * @version 2.0<br>
 * @date 2019/04/03 <br>
 */
@Slf4j
public class ParserCache {
    /**
     * VO类-绑定注解缓存
     */
    private static final Map<Class, BindAnnotationGroup> allVoBindAnnotationCacheMap = new ConcurrentHashMap<>();
    /**
     * dto类-BindQuery注解的缓存
     */
    private static final Map<String, List<AnnoJoiner>> dtoClassBindQueryCacheMap = new ConcurrentHashMap<>();
    /**
     * 加密字段缓存
     */
    private static final Map<String, Map<String, IEncryptStrategy>> FIELD_ENCRYPTOR_MAP = new ConcurrentHashMap<>();
    /**
     * 加密器对象缓存
     */
    private static final Map<String, IEncryptStrategy> ENCRYPTOR_MAP = new ConcurrentHashMap<>();
    /**
     * 脱敏策略对象缓存
     */
    private static final Map<String, IMaskStrategy> MASK_STRATEGY_MAP = new ConcurrentHashMap<>();

    /**
     * 获取指定class对应的Bind相关注解
     * @param voClass
     * @return
     */
    public static BindAnnotationGroup getBindAnnotationGroup(Class voClass){
        BindAnnotationGroup group = allVoBindAnnotationCacheMap.get(voClass);
        if(group == null){
            // 获取注解并缓存
            group = new BindAnnotationGroup();
            // 获取当前VO的注解
            List<Field> fields = BeanUtils.extractAllFields(voClass);
            if(fields != null){
                for (Field field : fields) {
                    //遍历属性
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    if (V.isEmpty(annotations)) {
                        continue;
                    }
                    for (Annotation annotation : annotations) {
                        Class<?> setterObjClazz = field.getType();
                        if(setterObjClazz.equals(java.util.List.class) || setterObjClazz.equals(java.util.Collections.class)){
                            // 如果是集合，获取其泛型参数class
                            Type genericType = field.getGenericType();
                            if(genericType instanceof ParameterizedType){
                                ParameterizedType pt = (ParameterizedType) genericType;
                                setterObjClazz = (Class<?>)pt.getActualTypeArguments()[0];
                            }
                        }
                        group.addBindAnnotation(field.getName(), setterObjClazz, annotation);
                    }
                }
            }
            allVoBindAnnotationCacheMap.put(voClass, group);
        }
        // 返回归类后的注解对象
        return group;
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
            if(tableNameAnno != null){
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
                if(V.notEmpty(anno.getJoin()) && fieldNameSet != null && fieldNameSet.contains(anno.getFieldName())){
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
        List<AnnoJoiner> annos = null;
        List<Field> declaredFields = BeanUtils.extractFields(dtoClass, BindQuery.class);
        int index = 1;
        Map<String, String> joinOn2Alias = new HashMap<>();
        for (Field field : declaredFields) {
            BindQuery query = field.getAnnotation(BindQuery.class);
            if(query == null || query.ignore()){
                continue;
            }
            if(annos == null){
                annos = new ArrayList<>();
            }
            AnnoJoiner annoJoiner = new AnnoJoiner(field, query);
            // 关联对象，设置别名
            if(V.notEmpty(annoJoiner.getJoin())){
                String key = annoJoiner.getJoin() + ":" + annoJoiner.getCondition();
                String alias = joinOn2Alias.get(key);
                if(alias == null){
                    alias = "r"+index;
                    annoJoiner.setAlias(alias);
                    index++;
                    joinOn2Alias.put(key, alias);
                }
                else{
                    annoJoiner.setAlias(alias);
                }
                annoJoiner.parse();
            }
            annos.add(annoJoiner);
        }
        if(annos == null){
            annos = Collections.emptyList();
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
     * 获取注解joiner
     * @param dtoClass
     * @param key
     * @return
     */
    public static AnnoJoiner getAnnoJoiner(Class<?> dtoClass, String key) {
        List<AnnoJoiner> annoList = getBindQueryAnnos(dtoClass);
        if(V.notEmpty(annoList)){
            for(AnnoJoiner anno : annoList){
                if(key.equals(anno.getFieldName())){
                    return anno;
                }
            }
        }
        return null;
    }

    /**
     * 获取加密器对象
     *
     * @param clazz 加密器类型
     * @return 加密器对象
     */
    @NonNull
    public static IEncryptStrategy getEncryptor(@NonNull Class<? extends IEncryptStrategy> clazz) {
        return ENCRYPTOR_MAP.computeIfAbsent(clazz.getName(), k -> {
            IEncryptStrategy encryptor = ContextHelper.getBean(clazz);
            if (encryptor == null) {
                try {
                    encryptor = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("{} 初始化失败", clazz, e);
                }
            }
            return Objects.requireNonNull(encryptor);
        });
    }

    /**
     * 获取该类保护字段加密器Map
     *
     * @param clazz 类型
     * @return 非null，Map<字段名，加密器>
     */
    @NonNull
    public static Map<String, IEncryptStrategy> getFieldEncryptorMap(@NonNull Class<?> clazz) {
        return FIELD_ENCRYPTOR_MAP.computeIfAbsent(clazz.getName(), k -> {
            Map<String, IEncryptStrategy> fieldEncryptorMap = new HashMap<>();
            for (Field field : BeanUtils.extractFields(clazz, ProtectField.class)) {
                if (!field.getType().isAssignableFrom(String.class)) {
                    log.error("`@ProtectField` 仅支持 String 类型字段。");
                    continue;
                }
                ProtectField protect = field.getAnnotation(ProtectField.class);
                IEncryptStrategy encryptor = getEncryptor(protect.encryptor());
                fieldEncryptorMap.put(field.getName(), encryptor);
            }
            for (Field field : BeanUtils.extractFields(clazz, BindQuery.class)) {
                if (!field.getType().isAssignableFrom(String.class)) {
                    continue;
                }
                BindQuery query = field.getAnnotation(BindQuery.class);
                if (query != null && V.notEmpty(query.field()) && query.entity() != NullType.class) {
                    IEncryptStrategy encryptor = getFieldEncryptorMap(query.entity()).get(query.field());
                    if (encryptor != null) {
                        fieldEncryptorMap.put(field.getName(), encryptor);
                    }
                }
            }
            return fieldEncryptorMap.isEmpty() ? Collections.emptyMap() : fieldEncryptorMap;
        });
    }

    /**
     * 获取脱敏策略对象
     *
     * @param clazz 脱敏策略类型
     * @return 脱敏策略对象
     */
    @NonNull
    public static IMaskStrategy getMaskStrategy(@NonNull Class<? extends IMaskStrategy> clazz) {
        return MASK_STRATEGY_MAP.computeIfAbsent(clazz.getName(), k -> {
            IMaskStrategy maskStrategy = ContextHelper.getBean(clazz);
            if (maskStrategy == null) {
                try {
                    maskStrategy = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("{} 初始化失败", clazz, e);
                }
            }
            return Objects.requireNonNull(maskStrategy);
        });
    }
}
