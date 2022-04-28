/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.cache;

import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 动态数据临时内存缓存
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
@Slf4j
public class DynamicMemoryCacheManager extends BaseMemoryCacheManager implements BaseCacheManager{

    public DynamicMemoryCacheManager(String... cacheNames){
        List<Cache> caches = new ArrayList<>(cacheNames.length);
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
        }
        setCaches(caches);
        super.afterPropertiesSet();
    }

    /**
     * cache的时间戳缓存
     */
    private ConcurrentHashMap<String, Integer> CACHE_EXPIREDMINUTES_CACHE = new ConcurrentHashMap<>();

    /**
     * cache的时间戳缓存
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<Object, Long>> CACHE_TIMESTAMP_CACHE = new ConcurrentHashMap<>();

    public DynamicMemoryCacheManager(){
        super.afterPropertiesSet();
    }

    /**
     * 统一过期时间
     * @param expiredMinutes
     * @param cacheNames
     */
    public DynamicMemoryCacheManager(int expiredMinutes, String... cacheNames){
        List<Cache> caches = new ArrayList<>(cacheNames.length);
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
            this.CACHE_EXPIREDMINUTES_CACHE.put(cacheName, expiredMinutes);
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
            cache.evict(objKey);
            if(log.isDebugEnabled()){
                log.debug("缓存已过期被清理: {}.{}", cacheName, objKey);
            }
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
     * 缓存对象
     * @param cacheName
     * @param objKey
     * @param obj
     */
    public void putCacheObj(String cacheName, Object objKey, Object obj, int expireMinutes) {
        this.putCacheObj(cacheName, objKey, obj);
        if(!this.CACHE_EXPIREDMINUTES_CACHE.containsKey(cacheName)){
            this.CACHE_EXPIREDMINUTES_CACHE.put(cacheName, expireMinutes);
            if(log.isDebugEnabled()){
                log.debug("设置缓存过期时间: {}={}", cacheName, expireMinutes);
            }
        }
    }

    @Override
    public synchronized void clearAllOutOfDateData() {
        for(String cacheName : this.getCacheNames()){
            Cache cache = getCache(cacheName);
            ConcurrentMap<Object, Object> cacheMap = (ConcurrentMap<Object, Object>)cache.getNativeCache();
            if(V.isEmpty(cacheMap)){
                continue;
            }
            for(Map.Entry<Object, Object> entry : cacheMap.entrySet()){
                // 已过期，则清空缓存返回null
                if(isExpired(cacheName, entry.getKey())){
                    cache.evict(entry.getKey());
                    if(log.isDebugEnabled()){
                        log.debug("统一清理已过期的缓存: {}.{}", cacheName, entry.getKey());
                    }
                }
            }
        }
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
        if(V.isEmpty(timestampCache)){
            return false;
        }
        Long cacheTimestamp = timestampCache.get(objKey);
        if(cacheTimestamp == null){
            return false;
        }
        long currentTimestamp = System.currentTimeMillis();
        int expiredMinutes = CACHE_EXPIREDMINUTES_CACHE.get(cacheName);
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
