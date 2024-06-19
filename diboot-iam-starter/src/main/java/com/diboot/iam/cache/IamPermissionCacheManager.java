/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.cache;

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.process.ApiPermissionExtractor;
import com.diboot.iam.annotation.process.ApiPermissionWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IAM缓存manager
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/22
 * Copyright © diboot.com
 */
@Slf4j
public class IamPermissionCacheManager {

    /**
     * controller-权限码 缓存
     */
    private static Map<String, ApiPermissionWrapper> CLASS_PERMISSIONCODE_CACHE = new ConcurrentHashMap<>();

    /**
     * 返回全部接口权限码ApiPermission
     * @return
     */
    public static List<ApiPermissionWrapper> getApiPermissionVoList(){
        return ApiPermissionExtractor.extractAllApiPermissions();
    }

    /**
     * 缓存全部permissions
     */
    public static ApiPermissionWrapper getPermissionCodeWrapper(Class<?> controllerClass){
        // 优先从缓存中读取
        ApiPermissionWrapper wrapper = CLASS_PERMISSIONCODE_CACHE.get(controllerClass.getName());
        if(wrapper != null){
            return wrapper;
        }
        // 从controller中解析
        String name;
        // 提取类信息
        String codePrefix = null;
        // 注解
        BindPermission bindPermission = AnnotationUtils.findAnnotation(controllerClass, BindPermission.class);
        if(bindPermission != null){
            // 当前资源权限
            name = bindPermission.name();
            codePrefix = bindPermission.code();
            if(V.isEmpty(codePrefix)){
                Class<?> entityClazz = BeanUtils.getGenericityClass(controllerClass, 0);
                if(entityClazz != null){
                    codePrefix = entityClazz.getSimpleName();
                }
                else{
                    throw new InvalidUsageException("exception.invalidUsage.permissionCacheManager.getPermissionCodeWrapper.message", controllerClass.getName());
                }
            }
        }
        else{
            name = S.substringBeforeLast(controllerClass.getSimpleName(), "Controller");
        }
        wrapper = new ApiPermissionWrapper(name, codePrefix);
        CLASS_PERMISSIONCODE_CACHE.put(controllerClass.getName(), wrapper);
        return wrapper;
    }

}