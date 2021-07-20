/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;

/**
 * RedisCache缓存定义
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/6/8  18:36
 * Copyright © diboot.com
 */
public class ShiroRedisCache<K, V> implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(ShiroRedisCache.class);

    private RedisTemplate redisTemplate;
    private String cacheName;

    public ShiroRedisCache(String cacheName, RedisTemplate redisTemplate) {
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(K k) throws CacheException {
        return (V)redisTemplate.opsForHash().get(cacheName, k.toString());
    }

    @Override
    public V put(K k , V v) throws CacheException {
        redisTemplate.opsForHash().put(cacheName, k.toString(), v);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        return (V)redisTemplate.opsForHash().delete(cacheName, k.toString());
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.delete(cacheName);
    }

    @Override
    public int size() {
        return redisTemplate.opsForHash().size(cacheName).intValue();
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.opsForHash().keys(cacheName);
    }

    @Override
    public Collection<V> values() {
        return redisTemplate.opsForHash().values(cacheName);
    }
}
