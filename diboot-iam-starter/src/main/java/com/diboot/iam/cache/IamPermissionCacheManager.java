/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.vo.ApiUri;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.process.ApiPermission;
import com.diboot.iam.annotation.process.ApiPermissionExtractor;
import com.diboot.iam.annotation.process.ApiPermissionWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IAM缓存manager
 *
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
     *
     * @param openApi 是否只返回开放接口
     * @return
     */
    public static List<ApiPermissionWrapper> getApiPermissionVoList(boolean openApi) {
        List<ApiPermissionWrapper> list = ApiPermissionExtractor.extractAllApiPermissions();
        if (!openApi) return list;
        List<ApiPermissionWrapper> apiPermissionWrapperList = new ArrayList<>();
        for (ApiPermissionWrapper apiPermissionWrapper : list) {
            if (apiPermissionWrapper.isOpenApi()) {
                apiPermissionWrapperList.add(apiPermissionWrapper);
                continue;
            }
            List<ApiPermission> permissions = new ArrayList<>();
            for (ApiPermission permission : apiPermissionWrapper.getApiPermissionList()) {
                List<ApiUri> apiUriList = new ArrayList<>();
                for (ApiUri apiUri : permission.getApiUriList()) {
                    if (apiUri.isOpenApi()) apiUriList.add(apiUri);
                }
                if (apiUriList.isEmpty()) continue;
                permissions.add(new ApiPermission(permission.getCode()).setApiUriList(apiUriList));
            }
            if (permissions.isEmpty()) continue;
            apiPermissionWrapperList.add(BeanUtils.cloneBean(apiPermissionWrapper).setApiPermissionList(permissions));
        }
        return apiPermissionWrapperList;
    }

    /**
     * 缓存全部permissions
     */
    public static ApiPermissionWrapper getPermissionCodeWrapper(Class<?> controllerClass) {
        // 优先从缓存中读取
        ApiPermissionWrapper wrapper = CLASS_PERMISSIONCODE_CACHE.get(controllerClass.getName());
        if (wrapper != null) {
            return wrapper;
        }
        // 从controller中解析
        String name;
        // 提取类信息
        String codePrefix = null;
        // 是否开放
        boolean openApi = false;
        // 注解
        BindPermission bindPermission = AnnotationUtils.findAnnotation(controllerClass, BindPermission.class);
        if (bindPermission != null) {
            // 当前资源权限
            name = bindPermission.name();
            codePrefix = bindPermission.code();
            openApi = bindPermission.openApi();
            if (V.isEmpty(codePrefix)) {
                Class<?> entityClazz = BeanUtils.getGenericityClass(controllerClass, 0);
                if (entityClazz != null) {
                    codePrefix = entityClazz.getSimpleName();
                } else {
                    throw new InvalidUsageException("注解@BindPermission注解无法自动提取code：{} 类无泛型Entity参数，请手动指定code值！", controllerClass.getName());
                }
            }
        } else {
            name = S.substringBeforeLast(controllerClass.getSimpleName(), "Controller");
        }
        wrapper = new ApiPermissionWrapper(name, codePrefix).setOpenApi(openApi);
        CLASS_PERMISSIONCODE_CACHE.put(controllerClass.getName(), wrapper);
        return wrapper;
    }

}
