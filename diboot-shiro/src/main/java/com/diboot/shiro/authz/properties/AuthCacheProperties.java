package com.diboot.shiro.authz.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-29  15:59
 */
@Data
@ConfigurationProperties(AuthCacheProperties.CACHE_PREFIX)
public class AuthCacheProperties {

    public final static String CACHE_PREFIX = "diboot.shiro.cache";

    /**
     * 是否开启权限缓存：默认false
     */
    private boolean permissionCachingEnabled = false;

    /**
     * 缓存方式：默认内存缓存
     */
    private CacheWay cacheWay = CacheWay.MEMORY;

    /**
     * 缓存方式
     * <p>
     *  当前提供本地缓存
     * </p>
     */
    @Getter
    @AllArgsConstructor
    public enum CacheWay {
        /**
         * 内存缓存
         */
        MEMORY,
        /**
         * redis缓存： TODO 尚未实现，暂不可用
         */
        @Deprecated
        REDIS;
    }

}
