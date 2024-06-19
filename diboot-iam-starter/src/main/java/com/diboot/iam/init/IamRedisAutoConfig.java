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
package com.diboot.iam.init;

import com.diboot.core.cache.BaseCacheManager;
import com.diboot.core.cache.DynamicRedisCacheManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.redis.ShiroRedisCacheManager;
import com.diboot.iam.config.IamProperties;
import com.diboot.starter.IamAutoConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Shiro の Redis 缓存自动配置
 *
 * @author wind
 * @version v2.3.0
 * @date 2021/7/20
 * Copyright © diboot.com
 */
@Slf4j
@Order(911)
@Configuration
@ConditionalOnBean(RedisTemplate.class)
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnResource(resources = "org/springframework/data/redis")
@AutoConfigureBefore({IamAutoConfig.class})
public class IamRedisAutoConfig {
    @Autowired
    private IamProperties iamProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public IamRedisAutoConfig() {
        log.info("初始化 IAM 组件 redis 自动配置");
    }

    /**
     * 启用RedisCacheManager定义
     * @return
     */
     @Bean(name = "shiroCacheManager")
     @ConditionalOnMissingBean(CacheManager.class)
     public CacheManager shiroCacheManager(RedisTemplate<String, Object> redisTemplate) {
         log.info("初始化shiro缓存: ShiroRedisCacheManager");
        return new ShiroRedisCacheManager(redisTemplate, iamProperties.getTokenExpiresMinutes());
     }

    /**
     * 验证码的缓存管理
     * @return
     */
    @Bean(name = "iamCacheManager")
    @ConditionalOnMissingBean
    public BaseCacheManager iamCacheManager(){
        log.info("初始化 IAM Redis缓存: DynamicRedisCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<String, Integer>(){{
                put(Cons.CACHE_TOKEN_USERINFO, iamProperties.getTokenExpiresMinutes());
                put(Cons.CACHE_CAPTCHA, 5);
        }};
        return new DynamicRedisCacheManager(redisTemplate, cacheName2ExpireMap);
    }

}