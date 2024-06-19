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

import com.diboot.core.exception.InvalidUsageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 动态数据Redis缓存
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/17
 * Copyright © diboot.com
 */
@Slf4j
public class DynamicRedisCacheManager extends SimpleCacheManager implements BaseCacheManager {

    private RedisCacheManager redisCacheManager;

    public DynamicRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public DynamicRedisCacheManager(RedisTemplate redisTemplate, Map<String, Integer> cacheName2ExpiredMinutes) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisTemplate.getConnectionFactory());
        for(Map.Entry<String, Integer> entry : cacheName2ExpiredMinutes.entrySet()){
            // redis配置参数
            RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getStringSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()))
                    .entryTtl(Duration.ofMinutes(entry.getValue()));
            builder.withCacheConfiguration(entry.getKey(), cacheConfiguration);
        }
        // 初始化redisCacheManager
        redisCacheManager = builder.transactionAware().build();
        redisCacheManager.initializeCaches();
        super.afterPropertiesSet();
        log.info("redisCacheManager 初始化完成");
    }

    @Override
    public <T> T getCacheObj(String cacheName, Object objKey, Class<T> tClass) {
        Cache cache = redisCacheManager.getCache(cacheName);
        T cacheObj = cache != null? cache.get(objKey, tClass) : null;
        if (log.isTraceEnabled()) {
            log.trace("从缓存读取: {}.{} = {}", cacheName, objKey, cacheObj);
        }
        return cacheObj;
    }

    @Override
    public <T> T getCacheObj(String cacheName, Object objKey, Callable<T> initSupplier) {
        Cache cache = redisCacheManager.getCache(cacheName);
        T cacheObj = cache != null ? cache.get(objKey, initSupplier) : null;
        if (log.isTraceEnabled()) {
            log.trace("从缓存读取: {}.{} = {}", cacheName, objKey, cacheObj);
        }
        return cacheObj;
    }

    @Override
    public String getCacheString(String cacheName, Object objKey) {
        return getCacheObj(cacheName, objKey, String.class);
    }

    @Override
    public void putCacheObj(String cacheName, Object objKey, Object obj) {
        Cache cache = redisCacheManager.getCache(cacheName);
        if(cache == null) {
            throw new InvalidUsageException("exception.invalidUsage.cacheManager.nonCacheInit", cacheName);
        }
        if(log.isDebugEnabled()){
            log.debug("缓存: {} 新增-> {}", cacheName, objKey);
        }
        cache.put(objKey, obj);
    }

    @Override
    public void removeCacheObj(String cacheName, Object objKey) {
        Cache cache = redisCacheManager.getCache(cacheName);
        if(cache == null) {
            throw new InvalidUsageException("exception.invalidUsage.cacheManager.nonCacheInit", cacheName);
        }
        if(log.isDebugEnabled()){
            log.debug("缓存: {} 移除-> {}", cacheName, objKey);
        }
        cache.evict(objKey);
    }

    @Override
    public boolean isUninitializedCache(String cacheName) {
        return false;
    }

    @Override
    public void clearOutOfDateData(String cacheName) {
    }

}