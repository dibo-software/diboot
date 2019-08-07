package com.diboot.shiro.authz.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;

/**
 * TODO redis缓存管理：暂时不提供
 * @author : wee
 * @version : v2.0
 * @Date 2019-08-05  16:15
 */
@Slf4j
public class RedisCacheManager implements CacheManager, Destroyable {

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        log.error("【缓存】<== 尚未实现redis缓存，暂时不可用，请选择内存缓存");
        return null;
    }

    @Override
    public void destroy() throws Exception {
        //清除redis内容
    }
}
