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

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.SqlExecutor;
import com.diboot.core.util.V;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  VO对象中的绑定注解 缓存管理类
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
        boolean hasColumn = SqlExecutor.validateQuery("SELECT is_deleted FROM "+middleTable);
        middleTableHasDeletedCacheMap.put(middleTable, hasColumn);
        return hasColumn;
    }
}
