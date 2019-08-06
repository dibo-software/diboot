package com.diboot.shiro.authz.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * TODO redis缓存处理，等候完善
 * @author : wee
 * @version : v2.0
 * @Date 2019-08-05  16:20
 */
public class RedisCache<K, V> implements Cache<K, V> {
    @Override
    public V get(K k) throws CacheException {
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        return null;
    }

    @Override
    public void clear() throws CacheException {
        //TODO 根据模糊key获取redis中所有的权限，然后统一清空
    }

    @Override
    public int size() {
        //TODO 模糊key获取redis所有当前系统中的权限
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
