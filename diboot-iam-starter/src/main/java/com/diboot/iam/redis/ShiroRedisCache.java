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

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private int tokenExpireMinutes;

    public ShiroRedisCache(String cacheName, RedisTemplate redisTemplate, int tokenExpireMinutes) {
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
        this.tokenExpireMinutes = tokenExpireMinutes;
    }

    private String getKey(String key){
        return this.cacheName + ":" + key;
    }

    @Override
    public V get(K k) throws CacheException {
        String key = this.getKey(k.toString());
        log.debug("get key : {}", key);
        return (V)redisTemplate.opsForValue().get(key);
    }

    @Override
    public V put(K k , V v) throws CacheException {
        if (k == null || v == null) {
            return null;
        }
        String key = this.getKey(k.toString());
        log.debug("put key : {}, value: {}", key, v);
        redisTemplate.opsForValue().set(key, v, tokenExpireMinutes, TimeUnit.MINUTES);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        if (k == null) {
            return null;
        }
        String key = this.getKey(k.toString());
        V value = get(k);
        log.debug("remove key : {}", key);
        redisTemplate.delete(key);
        return value;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.delete(this.keys());
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.keys(getKey("*"));
    }

    @Override
    public Collection<V> values() {
        Set<K> keys = keys();
        Set<V> values = new HashSet<>(keys.size());
        for (K key: keys) {
            V value = (V)redisTemplate.opsForValue().get(key);
            if(value != null){
                values.add(value);
            }
        }
        return values;
    }
}