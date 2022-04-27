package com.diboot.core.cache;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 动态数据Redis缓存
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
public class DynamicRedisCacheManager implements BaseCacheManager{

    private RedisCacheManager redisCacheManager;

    public DynamicRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public <T> T getCacheObj(String cacheName, Object objKey, Class<T> tClass) {
        Cache cache = redisCacheManager.getCache(cacheName);
        return cache != null? cache.get(objKey, tClass) : null;
    }

    @Override
    public String getCacheString(String cacheName, Object objKey) {
        return getCacheObj(cacheName, objKey, String.class);
    }

    @Override
    public void putCacheObj(String cacheName, Object objKey, Object obj) {
        Cache cache = redisCacheManager.getCache(cacheName);
        cache.put(objKey, obj);
    }

    public void putCacheObj(String cacheName, Object objKey, Object obj, int expireMinutes) {
        //TODO 支持按cache的不同过期时间
        this.putCacheObj(cacheName, objKey, obj);
    }

    @Override
    public void removeCacheObj(String cacheName, Object objKey) {
        Cache cache = redisCacheManager.getCache(cacheName);
        cache.evict(objKey);
    }

    @Override
    public boolean isUninitializedCache(String cacheName) {
        return false;
    }

}