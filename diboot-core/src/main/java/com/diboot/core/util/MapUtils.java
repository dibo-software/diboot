/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Map相关工具类
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/6/15
 * Copyright © diboot.com
 */
@Slf4j
public class MapUtils {

    /**
     * 忽略key大小写，兼容多库等场景
     * @param map
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getIgnoreCase(Map<String, T> map, String key) {
        if(map.containsKey(key)) {
            return map.get(key);
        }
        return map.get(key.toUpperCase());
    }

    /**
     * 构建ResultMap为实体
     * @param dataMap
     * @param model
     * @return
     * @param <T>
     */
    public static <T> void buildEntity(Map<String, Object> dataMap, T model){
        // 字段映射
        if(V.isEmpty(dataMap)){
            return;
        }
        BeanUtils.bindProperties(model, dataMap);
    }

    /**
     * 构建ResultMap为实体
     * @param dataMap
     * @param entityClass
     * @return
     * @param <T>
     */
    public static <T> T buildEntity(Map<String, Object> dataMap, Class<T> entityClass) {
        // 字段映射
        if(V.isEmpty(dataMap)){
            return null;
        }
        T entityInstance = null;
        try {
            entityInstance = entityClass.newInstance();
        }
        catch (Exception e){
            log.warn("实例化Entity {} 异常: {}", entityClass.getSimpleName(), e.getMessage());
        }
        BeanUtils.bindProperties(entityInstance, dataMap);
        return entityInstance;
    }

    /**
     * 构建ResultMap为实体列表
     * @param resultListMap
     * @param entityClass
     * @return
     * @param <T>
     */
    public static <T> List<T> buildEntityList(List<Map<String, Object>> resultListMap, Class<T> entityClass) {
        if(V.isEmpty(resultListMap)){
            return Collections.emptyList();
        }
        List<T> entityList = new ArrayList<>(resultListMap.size());
        for(Map<String, Object> resultMap : resultListMap){
            T entityInstance = null;
            try {
                entityInstance = entityClass.newInstance();
            }
            catch (Exception e){
                log.warn("实例化Entity {} 异常: {}", entityClass.getSimpleName(), e.getMessage());
            }
            if(entityInstance != null) {
                buildEntity(resultMap, entityInstance);
                entityList.add(entityInstance);
            }
        }
        return entityList;
    }

}
