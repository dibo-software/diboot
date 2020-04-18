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
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.dynamic.AnnoJoiner;
import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.SqlExecutor;
import com.diboot.core.util.V;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.core.annotation.AnnotationUtils;

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
public class ParserCache {
    /**
     * VO类-绑定注解缓存
     */
    private static Map<Class, BindAnnotationGroup> allVoBindAnnotationCacheMap = new ConcurrentHashMap<>();
    /**
     * 中间表是否包含is_deleted列 缓存
     */
    private static Map<String, Boolean> middleTableHasDeletedCacheMap = new ConcurrentHashMap<>();
    /**
     * entity类-表名的缓存
     */
    private static Map<String, String> entityClassTableCacheMap = new ConcurrentHashMap<>();
    /**
     * dto类-BindQuery注解的缓存
     */
    private static Map<String, List<AnnoJoiner>> dtoClassBindQueryCacheMap = new ConcurrentHashMap<>();

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
    public static boolean hasDeletedColumn(String middleTable){
        if(middleTableHasDeletedCacheMap.containsKey(middleTable)){
            return middleTableHasDeletedCacheMap.get(middleTable);
        }
        boolean hasColumn = SqlExecutor.validateQuery(buildCheckDeletedColSql(middleTable));
        middleTableHasDeletedCacheMap.put(middleTable, hasColumn);
        return hasColumn;
    }

    /**
     * 构建检测是否有删除字段的sql
     * @param table
     * @return
     */
    private static String buildCheckDeletedColSql(String table){
        return new SQL(){{
            SELECT(Cons.COLUMN_IS_DELETED);
            FROM(table);
            LIMIT(1);
        }}.toString();
    }

    /**
     * 获取entity对应的表名
     * @param entityClass
     * @return
     */
    public static String getEntityTableName(Class<?> entityClass){
        String entityClassName = entityClass.getName();
        String tableName = entityClassTableCacheMap.get(entityClassName);
        if(tableName == null){
            TableName tableNameAnno = AnnotationUtils.findAnnotation(entityClass, TableName.class);
            if(tableNameAnno != null){
                tableName = tableNameAnno.value();
            }
            else{
                tableName = S.toSnakeCase(entityClass.getSimpleName());
            }
            entityClassTableCacheMap.put(entityClassName, tableName);
        }
        return tableName;
    }

    /**
     * 当前DTO是否有Join绑定
     * @param dto dto对象
     * @param fieldNameSet 有值属性集合
     * @param <DTO>
     * @return
     */
    public static <DTO> boolean hasJoinTable(DTO dto, Set<String> fieldNameSet){
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
        List<Field> declaredFields = BeanUtils.extractAllFields(dtoClass);
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
            }
            annos.add(annoJoiner);
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

}
