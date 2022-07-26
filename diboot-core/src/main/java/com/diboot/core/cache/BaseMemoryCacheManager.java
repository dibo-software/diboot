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
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;

import java.util.concurrent.Callable;

/**
 * 缓存manager父类
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
@Slf4j
public abstract class BaseMemoryCacheManager extends SimpleCacheManager implements BaseCacheManager{

    /**
     * 获取缓存对象
     * @param objKey
     * @param <T>
     * @return
     */
    @Override
    public <T> T getCacheObj(String cacheName, Object objKey, Class<T> tClass){
        Cache cache = getCache(cacheName);
        T value = cache != null? cache.get(objKey, tClass) : null;
        if(log.isTraceEnabled()){
            log.trace("从缓存读取: {}.{} = {}", cacheName, objKey, value);
        }
        return value;
    }

    /**
     * 获取缓存对象, 如果找不到, 则生成初始值, 放入缓存, 并返回
     *
     * @param cacheName    cache名称
     * @param objKey       查找的key
     * @param initSupplier 初始值提供者
     * @param <T>          缓存对象类型
     * @return 缓存对象
     */
    @Override
    public <T> T getCacheObj(String cacheName, Object objKey, Callable<T> initSupplier) {
        Cache cache = getCache(cacheName);
        T value = cache != null ? cache.get(objKey, initSupplier) : null;
        if (log.isTraceEnabled()) {
            log.trace("从缓存读取: {}.{} = {}", cacheName, objKey, value);
        }
        return value;
    }

    /**
     * 获取缓存对象
     * @param objKey
     * @return
     */
    @Override
    public String getCacheString(String cacheName, Object objKey){
        return getCacheObj(cacheName, objKey, String.class);
    }

    /**
     * 缓存对象
     * @param cacheName
     * @param objKey
     * @param obj
     */
    @Override
    public void putCacheObj(String cacheName, Object objKey, Object obj){
        Cache cache = getCache(cacheName);
        cache.put(objKey, obj);
        if(log.isDebugEnabled()){
            ConcurrentMapCache mapCache = (ConcurrentMapCache)cache;
            log.debug("缓存: {} 新增-> {} , 当前size={}", cacheName, objKey, mapCache.getNativeCache().size());
        }
    }

    /**
     * 删除缓存对象
     * @param cacheName
     * @param objKey
     */
    @Override
    public void removeCacheObj(String cacheName, Object objKey){
        Cache cache = getCache(cacheName);
        cache.evict(objKey);
        if(log.isDebugEnabled()){
            ConcurrentMapCache mapCache = (ConcurrentMapCache)cache;
            log.debug("缓存删除: {}.{} , 当前size={}", cacheName, objKey, mapCache.getNativeCache().size());
        }
    }

    /**
     * 尚未初始化的
     * @param cacheName
     * @return
     */
    @Override
    public boolean isUninitializedCache(String cacheName){
        ConcurrentMapCache cache = (ConcurrentMapCache)getCache(cacheName);
        return cache.getNativeCache().isEmpty();
    }

}
