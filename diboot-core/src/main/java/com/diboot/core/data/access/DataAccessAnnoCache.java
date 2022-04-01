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

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据访问权限的注解缓存
 *
 * @author Mazc@dibo.ltd
 * @version v2.1
 * @date 2020/04/24
 */
@Slf4j
public class DataAccessAnnoCache {
    /**
     * 注解缓存
     */
    private static final Map<String, Map<String, String>> DATA_PERMISSION_ANNO_CACHE = new ConcurrentHashMap<>();

    /**
     * 是否有检查点注解
     *
     * @param entityClass
     * @return
     */
    public static boolean hasDataAccessCheckpoint(Class<?> entityClass) {
        return !DATA_PERMISSION_ANNO_CACHE.computeIfAbsent(entityClass.getName(), k -> initClassCheckpoint(entityClass)).isEmpty();
    }

    /**
     * 获取数据权限的用户类型列名
     *
     * @param entityClass
     * @return
     */
    public static Map<String, String> getDataPermissionMap(Class<?> entityClass) {
        return DATA_PERMISSION_ANNO_CACHE.computeIfAbsent(entityClass.getName(), k -> initClassCheckpoint(entityClass));
    }

    /**
     * 初始化entityDto的检查点缓存
     *
     * @param entityClass
     * @return
     */
    private static Map<String, String> initClassCheckpoint(Class<?> entityClass) {
        List<Field> fieldList = BeanUtils.extractFields(entityClass, DataAccessCheckpoint.class);
        if (V.notEmpty(fieldList)) {
            Map<String, String> results = new HashMap<>();
            for (Field fld : fieldList) {
                results.put(fld.getName(), BeanUtils.getColumnName(fld));
            }
            return results;
        }
        else {
            return Collections.emptyMap();
        }
    }

}
