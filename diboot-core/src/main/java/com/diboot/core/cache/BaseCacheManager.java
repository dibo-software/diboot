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

import java.util.concurrent.Callable;

/**
 * 缓存manager父类
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
public interface BaseCacheManager {

    /**
     * 获取缓存对象
     * @param objKey
     * @param <T>
     * @return
     */
    <T> T getCacheObj(String cacheName, Object objKey, Class<T> tClass);

    /**
     * 获取缓存对象, 如果找不到, 则生成初始值, 放入缓存, 并返回
     *
     * @param cacheName    cache名称
     * @param objKey       查找的key
     * @param initSupplier 初始值提供者
     * @param <T>          缓存对象类型
     * @return 缓存对象
     */
    <T> T getCacheObj(String cacheName, Object objKey, Callable<T> initSupplier);

    /**
     * 获取缓存对象
     * @param objKey
     * @return
     */
    String getCacheString(String cacheName, Object objKey);

    /**
     * 缓存对象
     * @param cacheName
     * @param objKey
     * @param obj
     */
    void putCacheObj(String cacheName, Object objKey, Object obj);

    /**
     * 删除缓存对象
     * @param cacheName
     * @param objKey
     */
    void removeCacheObj(String cacheName, Object objKey);

    /**
     * 尚未初始化的
     * @param cacheName
     * @return
     */
    boolean isUninitializedCache(String cacheName);

    /**
     * 清理所有过期的数据：系统空闲时调用
     */
    void clearOutOfDateData(String cacheName);

}
