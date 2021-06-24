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

import com.diboot.core.cache.StaticMemoryCacheManager;
import com.diboot.core.util.V;
import com.diboot.iam.annotation.process.ApiPermission;
import com.diboot.iam.annotation.process.ApiPermissionExtractor;
import com.diboot.iam.annotation.process.ApiPermissionWrapper;

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
public class IamCacheManager {

    /**
     * 接口相关定义缓存管理器
     */
    private static StaticMemoryCacheManager iamMemoryCacheManager;
    /**
     * controller-API 权限
     */
    private static final String CACHE_NAME_CONTROLLER_API = "CONTROLLER_API";
    /**
     * url-permission 缓存
     */
    private static Map<String, String> URL_PERMISSIONCODE_CACHE = new ConcurrentHashMap<>(8);

    private static StaticMemoryCacheManager getCacheManager(){
        if(iamMemoryCacheManager == null){
            iamMemoryCacheManager = new StaticMemoryCacheManager(
                    CACHE_NAME_CONTROLLER_API);
        }
        return iamMemoryCacheManager;
    }

    /**
     * 读取缓存permission
     * @param requestMethodAndUrl
     * @return
     */
    public static String getPermissionCode(String requestMethodAndUrl){
        return getUrlPermissionCacheMap().get(requestMethodAndUrl);
    }

    /**
     * 读取缓存permission
     * @param requestMethod
     * @param url
     * @return
     */
    public static String getPermissionCode(String requestMethod, String url){
        return getUrlPermissionCacheMap().get(requestMethod.toUpperCase()+":"+url);
    }

    /**
     * 返回全部ApiPermissionVO
     * @return
     */
    public static List<ApiPermissionWrapper> getApiPermissionVoList(){
        return ApiPermissionExtractor.extractAllApiPermissions();
    }

    /**
     * 获取接口权限wrapper
     * @param className
     * @return
     */
    public static ApiPermissionWrapper getApiPermissionWrapper(String className){
        initApiPermissionCache();
        return getCacheManager().getCacheObj(CACHE_NAME_CONTROLLER_API, className, ApiPermissionWrapper.class);
    }

    /**
     * 缓存全部permissions
     */
    private static Map<String, String> getUrlPermissionCacheMap(){
        if(V.notEmpty(URL_PERMISSIONCODE_CACHE)){
            return URL_PERMISSIONCODE_CACHE;
        }
        initApiPermissionCache();
        return URL_PERMISSIONCODE_CACHE;
    }

    /**
     * 初始化
     */
    private static void initApiPermissionCache() {
        StaticMemoryCacheManager cacheManager = getCacheManager();
        if (cacheManager.isUninitializedCache(CACHE_NAME_CONTROLLER_API) == false) {
            return;
        }
        List<ApiPermissionWrapper> permissions = ApiPermissionExtractor.extractAllApiPermissions();
        if(V.notEmpty(permissions)){
            for(ApiPermissionWrapper wrapper : permissions){
                if(wrapper.getChildren() != null){
                    for(ApiPermission apiPermission : wrapper.getChildren()){
                        // 缓存url-permission
                        URL_PERMISSIONCODE_CACHE.put(apiPermission.getApiMethod().toUpperCase()+":"+apiPermission.getApiUri(), apiPermission.getPermissionCode());
                        // 缓存class-api
                        cacheManager.putCacheObj(CACHE_NAME_CONTROLLER_API, wrapper.getClassName(), wrapper);
                    }
                }
            }
        }
    }

}