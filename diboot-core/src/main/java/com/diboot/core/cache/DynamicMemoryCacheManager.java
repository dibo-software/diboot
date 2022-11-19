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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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

    /**
     * cache的清理时间缓存
     */
    private final ConcurrentHashMap<String, String> CACHE_CLEANDATE_CACHE = new ConcurrentHashMap<>();

    /**
     * cache的过期时间缓存
     */
    private final ConcurrentHashMap<String, Integer> CACHE_EXPIREDMINUTES_CACHE = new ConcurrentHashMap<>();

    /**
     * cache的时间戳缓存
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<Object, Long>> CACHE_TIMESTAMP_CACHE = new ConcurrentHashMap<>();

    public DynamicMemoryCacheManager(){
        super.afterPropertiesSet();
    }

    /**
     * 指定cacheNames，无过期时间
     * @param cacheNames
     */
    public DynamicMemoryCacheManager(String... cacheNames){
        List<Cache> caches = new ArrayList<>(cacheNames.length);
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
        }
        setCaches(caches);
        super.afterPropertiesSet();
    }

    /**
     * 指定cacheNames，统一过期时间
     * @param expiredMinutes
     * @param cacheNames
     */
    public DynamicMemoryCacheManager(int expiredMinutes, String... cacheNames){
        List<Cache> caches = new ArrayList<>(cacheNames.length);
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
            this.CACHE_EXPIREDMINUTES_CACHE.put(cacheName, expiredMinutes);
            this.CACHE_CLEANDATE_CACHE.put(cacheName, "");
        }
        setCaches(caches);
        super.afterPropertiesSet();
    }

    /**
     * 指定cacheNames，附带不同的过期时间
     * @param cacheName2ExpiredMinutes
     */
    public DynamicMemoryCacheManager(Map<String, Integer> cacheName2ExpiredMinutes){
        List<Cache> caches = new ArrayList<>(cacheName2ExpiredMinutes.size());
        for(Map.Entry<String, Integer> cacheEntry : cacheName2ExpiredMinutes.entrySet()){
            caches.add(new ConcurrentMapCache(cacheEntry.getKey()));
            this.CACHE_EXPIREDMINUTES_CACHE.put(cacheEntry.getKey(), cacheEntry.getValue());
            this.CACHE_CLEANDATE_CACHE.put(cacheEntry.getKey(), "");
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
        clearOutOfDateDataIfNeeded(cacheName);
    }

    @Override
    public synchronized void clearOutOfDateData(String cacheName) {
        Cache cache = getCache(cacheName);
        ConcurrentMap<Object, Object> cacheMap = (ConcurrentMap<Object, Object>)cache.getNativeCache();
        if(V.isEmpty(cacheMap)){
            log.debug("暂无缓存数据: {}", cacheName);
            return;
        }
        int count = 0;
        for(Map.Entry<Object, Object> entry : cacheMap.entrySet()){
            // 已过期，则清空缓存返回null
            if(isExpired(cacheName, entry.getKey())){
                cache.evict(entry.getKey());
                count++;
                log.debug("清理已过期的缓存: {}.{}", cacheName, entry.getKey());
            }
        }
        log.debug("清理完成已过期缓存数据: {} 共 {} 条", cacheName, count);
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

    private void clearOutOfDateDataIfNeeded(String cacheName){
        boolean needed = true;
        String today = LocalDate.now().toString();
        if(CACHE_CLEANDATE_CACHE.containsKey(cacheName)){
            needed = V.notEquals(today, CACHE_CLEANDATE_CACHE.get(cacheName));
        }
        if(needed){
            log.debug("新的执行周期清理过期的本地缓存: {}", cacheName);
            clearOutOfDateData(cacheName);
            CACHE_CLEANDATE_CACHE.put(cacheName, today);
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
