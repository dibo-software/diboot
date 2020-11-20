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
package com.diboot.iam.annotation.process;

import com.diboot.core.util.V;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注解相关缓存
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/30
 */
public class ApiPermissionCache {
    /**
     * url-permission 缓存
     */
    private static Map<String, String> URL_PERMISSIONCODE_CACHE = new HashMap<>();
    /**
     * api-permission-wrapper 缓存
     */
    private static List<ApiPermissionWrapper> API_PERMISSION_WRAPPER_LIST = null;

    /**
     * 缓存全部permissions
     */
    public static void initCacheAllApiPermissions(){
        if(API_PERMISSION_WRAPPER_LIST == null){
            API_PERMISSION_WRAPPER_LIST = ApiPermissionExtractor.extractAllApiPermissions();
            if(V.notEmpty(API_PERMISSION_WRAPPER_LIST)){
                for(ApiPermissionWrapper wrapper : API_PERMISSION_WRAPPER_LIST){
                    if(wrapper.getChildren() != null){
                        for(ApiPermission apiPermission : wrapper.getChildren()){
                            // 缓存url-permission
                            URL_PERMISSIONCODE_CACHE.put(apiPermission.getApiMethod().toUpperCase()+":"+apiPermission.getApiUri(), apiPermission.getPermissionCode());
                        }
                    }
                }
            }
        }
    }

    /**
     * 读取缓存permission
     * @param requestMethodAndUrl
     * @return
     */
    public static String getPermissionCode(String requestMethodAndUrl){
        initCacheAllApiPermissions();
        return URL_PERMISSIONCODE_CACHE.get(requestMethodAndUrl);
    }

    /**
     * 读取缓存permission
     * @param requestMethod
     * @param url
     * @return
     */
    public static String getPermissionCode(String requestMethod, String url){
        initCacheAllApiPermissions();
        return URL_PERMISSIONCODE_CACHE.get(requestMethod.toUpperCase()+":"+url);
    }

    /**
     * 返回全部ApiPermissionVO
     * @return
     */
    public static List<ApiPermissionWrapper> getApiPermissionVoList(){
        initCacheAllApiPermissions();
        return API_PERMISSION_WRAPPER_LIST;
    }

}
