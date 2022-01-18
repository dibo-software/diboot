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
package com.diboot.core.data.access;

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据访问权限的注解缓存
 * @author Mazc@dibo.ltd
 * @version v2.1
 * @date 2020/04/24
 */
@Slf4j
public class DataAccessAnnoCache {
    /**
     * 注解缓存
     */
    private static final Map<String, String[]> DATA_PERMISSION_ANNO_CACHE = new ConcurrentHashMap<>();

    /**
     * 是否有检查点注解
     * @param entityClass
     * @return
     */
    public static boolean hasDataAccessCheckpoint(Class<?> entityClass){
        initClassCheckpoint(entityClass);
        String[] columns = DATA_PERMISSION_ANNO_CACHE.get(entityClass.getName());
        if(V.isEmpty(columns)){
            return false;
        }
        for(String type : columns){
            if(V.notEmpty(type)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取数据权限的用户类型列名
     * @param entityClass
     * @return
     */
    public static String getDataPermissionColumn(Class<?> entityClass, CheckpointType type){
        initClassCheckpoint(entityClass);
        int typeIndex = type.index();
        String key = entityClass.getName();
        String[] columns = DATA_PERMISSION_ANNO_CACHE.get(key);
        if(columns != null && (columns.length-1) >= typeIndex){
            return columns[typeIndex];
        }
        return null;
    }

    /**
     * 初始化entityDto的检查点缓存
     * @param entityClass
     */
    private static void initClassCheckpoint(Class<?> entityClass){
        String key = entityClass.getName();
        if(!DATA_PERMISSION_ANNO_CACHE.containsKey(key)){
            String[] results = {"", "", "", "", "", ""};
            List<Field> fieldList = BeanUtils.extractFields(entityClass, DataAccessCheckpoint.class);
            if(V.notEmpty(fieldList)){
                for(Field fld : fieldList){
                    DataAccessCheckpoint checkpoint = fld.getAnnotation(DataAccessCheckpoint.class);
                    if(V.notEmpty(results[checkpoint.type().index()])){
                        throw new InvalidUsageException(entityClass.getSimpleName() + "中DataPermissionCheckpoint同类型注解重复！");
                    }
                    results[checkpoint.type().index()] = BeanUtils.getColumnName(fld);
                }
            }
            DATA_PERMISSION_ANNO_CACHE.put(key, results);
        }
    }

}
