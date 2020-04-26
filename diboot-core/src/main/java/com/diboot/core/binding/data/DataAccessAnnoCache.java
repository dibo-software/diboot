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
package com.diboot.core.binding.data;

import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
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
    private static Map<String, String> DATA_PERMISSION_ANNO_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取数据权限的用户类型列名
     * @param entityDto
     * @return
     */
    public static String getDataPermissionColumn(Class<?> entityDto, CheckpointType type){
        int typeIndex = type.index();
        String key = entityDto.getName();
        if(!DATA_PERMISSION_ANNO_CACHE.containsKey(key)){
            String[] results = {"", "", "", "", ""};
            List<Field> fieldList = BeanUtils.extractFields(entityDto, DataAccessCheckpoint.class);
            if(V.notEmpty(fieldList)){
                for(Field fld : fieldList){
                    DataAccessCheckpoint checkpoint = fld.getAnnotation(DataAccessCheckpoint.class);
                    if(V.notEmpty(results[checkpoint.type().index()])){
                        throw new BusinessException(Status.FAIL_VALIDATION, entityDto.getSimpleName() + "中DataPermissionCheckpoint同类型注解重复！");
                    }
                    results[checkpoint.type().index()] = QueryBuilder.getColumnName(fld);
                }
            }
            DATA_PERMISSION_ANNO_CACHE.put(key, S.join(results));
        }
        String[] columns = S.split(DATA_PERMISSION_ANNO_CACHE.get(key));
        if(columns != null && (columns.length-1) >= typeIndex){
            return columns[typeIndex];
        }
        return null;
    }

}
