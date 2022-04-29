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

import com.diboot.core.exception.InvalidUsageException;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 静态不变化的数据内存缓存manager
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
public class StaticMemoryCacheManager extends BaseMemoryCacheManager implements BaseCacheManager{

    public StaticMemoryCacheManager(String... cacheNames){
        List<Cache> caches = new ArrayList<>();
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
        }
        setCaches(caches);
        super.afterPropertiesSet();
    }

    @Override
    public void clearOutOfDateData(String cacheName) {
        throw new InvalidUsageException("StaticMemoryCacheManager 缓存不存在过期，不支持清理！");
    }
}