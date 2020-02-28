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
     * @param apiPermissionWrappers
     */
    public static void cacheAllApiPermissions(List<ApiPermissionWrapper> apiPermissionWrappers){
        API_PERMISSION_WRAPPER_LIST = apiPermissionWrappers;
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

    /**
     * 读取缓存permission
     * @param requestMethod
     * @param url
     * @return
     */
    public static String getPermissionCode(String requestMethod, String url){
        return URL_PERMISSIONCODE_CACHE.get(requestMethod.toUpperCase()+":"+url);
    }

    /**
     * 返回全部ApiPermissionVO
     * @return
     */
    public static List<ApiPermissionWrapper> getApiPermissionVoList(){
        return API_PERMISSION_WRAPPER_LIST;
    }

}
