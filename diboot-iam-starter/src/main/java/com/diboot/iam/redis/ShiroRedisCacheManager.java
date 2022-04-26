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

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * ShiroRedisCacheManager
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/6/8  18:36
 * Copyright © diboot.com
 */
@Slf4j
public class ShiroRedisCacheManager extends AbstractCacheManager {

    private RedisTemplate redisTemplate;
    private int tokenExpireMinutes;

    public ShiroRedisCacheManager(RedisTemplate redisTemplate, int tokenExpireMinutes){
        this.redisTemplate = redisTemplate;
        this.tokenExpireMinutes = tokenExpireMinutes;
    }

    @Override
    protected Cache createCache(String cacheName) throws CacheException {
        log.debug("create redis cache: {}", cacheName);
        return new ShiroRedisCache(cacheName, redisTemplate, tokenExpireMinutes);
    }

}