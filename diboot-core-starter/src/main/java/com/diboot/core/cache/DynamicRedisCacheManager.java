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

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 动态数据Redis缓存
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/17
 * Copyright © diboot.com
 */
@Slf4j
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
        // 暂不支持redis按cache设置不同过期时间
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

    @Override
    public void clearOutOfDateData(String cacheName) {
    }

}