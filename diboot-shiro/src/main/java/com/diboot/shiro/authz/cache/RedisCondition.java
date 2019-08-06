package com.diboot.shiro.authz.cache;

import com.diboot.shiro.authz.properties.AuthCacheProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * redis条件类：用与创建redis缓存对象
 * @author : wee
 * @version : v2.0
 * @Date 2019-08-05  14:35
 */
public class RedisCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        //获取配置信息
        Boolean enableCached = context.getEnvironment().getProperty(AuthCacheProperties.CACHE_PREFIX + ".permission-caching-enabled", Boolean.class);
        AuthCacheProperties.CacheWay cacheWay = context.getEnvironment().getProperty(AuthCacheProperties.CACHE_PREFIX + ".cache-way", AuthCacheProperties.CacheWay.class);
        return enableCached && AuthCacheProperties.CacheWay.REDIS.equals(cacheWay);
    }
}
