package com.diboot.iam.annotation.process;

import java.util.HashMap;
import java.util.Map;

/**
 * 注解相关缓存
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/30
 */
public class BindPermissionCache {
    /**
     * url-permission 缓存
     */
    private static Map<String, String> URL_PERMISSION_CACHE = new HashMap<>();

    /**
     * 缓存url-permission
     * @param requestMethod
     * @param url
     * @param permission
     */
    public static void cacheUrl2PermissionCode(String requestMethod, String url, String permission){
        URL_PERMISSION_CACHE.put(requestMethod+":"+url, permission);
    }

    /**
     * 读取缓存permission
     * @param requestMethod
     * @param url
     * @return
     */
    public static String getPermissionCode(String requestMethod, String url){
        return URL_PERMISSION_CACHE.get(requestMethod+":"+url);
    }

}
