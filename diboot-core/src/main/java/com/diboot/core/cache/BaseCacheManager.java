package com.diboot.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;

/**
 * 缓存manager父类
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
public interface BaseCacheManager {

    /**
     * 获取缓存对象
     * @param objKey
     * @param <T>
     * @return
     */
    <T> T getCacheObj(String cacheName, Object objKey, Class<T> tClass);

    /**
     * 获取缓存对象
     * @param objKey
     * @return
     */
    String getCacheString(String cacheName, Object objKey);

    /**
     * 缓存对象
     * @param cacheName
     * @param objKey
     * @param obj
     */
    void putCacheObj(String cacheName, Object objKey, Object obj);

    /**
     * 缓存对象 - 支持过期时间
     * @param cacheName
     * @param objKey
     * @param obj
     */
    default void putCacheObj(String cacheName, Object objKey, Object obj, int expiredMinutes){
        putCacheObj(cacheName, objKey, obj);
    }

    /**
     * 删除缓存对象
     * @param cacheName
     * @param objKey
     */
    void removeCacheObj(String cacheName, Object objKey);

    /**
     * 尚未初始化的
     * @param cacheName
     * @return
     */
    boolean isUninitializedCache(String cacheName);

}
