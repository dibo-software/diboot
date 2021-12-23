package com.diboot.core.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 动态数据临时内存缓存
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
public class DynamicMemoryCacheManager extends BaseCacheManager{

    public DynamicMemoryCacheManager(String... cacheNames){
        List<Cache> caches = new ArrayList<>(cacheNames.length);
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
        }
        setCaches(caches);
        super.afterPropertiesSet();
    }

    /**
     * 默认的过期时间（分钟）
     */
    private long expiredMinutes = 60;
    /**
     * cache的时间戳缓存
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<Object, Long>> CACHE_TIMESTAMP_CACHE = null;

    public DynamicMemoryCacheManager(){
        this.CACHE_TIMESTAMP_CACHE = new ConcurrentHashMap<>();
        super.afterPropertiesSet();
    }
    public DynamicMemoryCacheManager(int expiredMinutes){
        this.expiredMinutes = expiredMinutes;
        this.CACHE_TIMESTAMP_CACHE = new ConcurrentHashMap<>();
        super.afterPropertiesSet();
    }

    public DynamicMemoryCacheManager(int expiredMinutes, String... cacheNames){
        this.expiredMinutes = expiredMinutes;
        this.CACHE_TIMESTAMP_CACHE = new ConcurrentHashMap<>();
        List<Cache> caches = new ArrayList<>(cacheNames.length);
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
        }
        setCaches(caches);
        super.afterPropertiesSet();
    }

    /**
     * 获取缓存对象
     * @param objKey
     * @param <T>
     * @return
     */
    public <T> T getCacheObj(String cacheName, Object objKey, Class<T> tClass) {
        Cache cache = getCache(cacheName);
        if(cache == null){
            return null;
        }
        // 已过期，则清空缓存返回null
        if(isExpired(cacheName, objKey)){
            cache.clear();
            return null;
        }
        return cache.get(objKey, tClass);
    }

    /**
     * 缓存对象
     * @param cacheName
     * @param objKey
     * @param obj
     */
    public void putCacheObj(String cacheName, Object objKey, Object obj) {
        super.putCacheObj(cacheName, objKey, obj);
        refreshCacheTimestamp(cacheName, objKey);
    }

    /**
     * 数据是否过期
     * @param cacheName 缓存名字
     * @param objKey 数据对象key
     * @return
     * @throws Exception
     */
    public boolean isExpired(String cacheName, Object objKey) {
        ConcurrentMap<Object, Long> timestampCache = CACHE_TIMESTAMP_CACHE.get(cacheName);
        if(timestampCache == null){
            return false;
        }
        Long cacheTimestamp = timestampCache.get(objKey);
        if(cacheTimestamp == null){
            return false;
        }
        long currentTimestamp = System.currentTimeMillis();
        return (currentTimestamp - cacheTimestamp) > (expiredMinutes*60000);
    }

    /**
     * 清空缓存
     * @param cacheName
     */
    public void clearCache(String cacheName) {
        Cache cache = getCache(cacheName);
        if(cache != null){
            cache.clear();
            // 清空时间戳
            ConcurrentMap<Object, Long> timestampCache = CACHE_TIMESTAMP_CACHE.get(cacheName);
            if(timestampCache != null){
                timestampCache.clear();
            }
        }
    }

    /**
     * 刷新缓存时间戳
     */
    private void refreshCacheTimestamp(String cacheName, Object objKey) {
        ConcurrentHashMap<Object, Long> timestampCache = CACHE_TIMESTAMP_CACHE.get(cacheName);
        if(timestampCache == null){
            timestampCache = new ConcurrentHashMap<>();
            CACHE_TIMESTAMP_CACHE.put(cacheName, timestampCache);
        }
        timestampCache.put(objKey, System.currentTimeMillis());
    }

}
