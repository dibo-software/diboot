/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.cache;

import com.diboot.core.cache.BaseCacheManager;
import com.diboot.core.config.Cons;
import com.diboot.core.util.V;
import com.diboot.iam.entity.SystemConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 系统缓存管理器
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/30
 */
@Slf4j
public class SystemConfigCacheManager {

    private BaseCacheManager cacheManager;

    public SystemConfigCacheManager(BaseCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * 移除缓存的指定类别数据
     *
     * @param category
     */
    public void removeCachedItems(String category) {
        if (cacheManager == null) {
            return;
        }
        cacheManager.removeCacheObj(Cons.CACHE_NAME_SYSTEM_CONFIG, handle(category));
    }

    /**
     * 从缓存中获取指定类别数据
     *
     * @param category
     * @return
     */
    public Map<String, SystemConfig> getCachedConfig(String category) {
        if (cacheManager == null) {
            return null;
        }
        return cacheManager.getCacheObj(Cons.CACHE_NAME_SYSTEM_CONFIG, handle(category), Map.class);
    }

    /**
     * 缓存配置数据
     *
     * @param category
     * @param configList
     */
    public void cacheConfig(String category, Collection<SystemConfig> configList) {
        if (cacheManager == null) {
            return;
        }
        Map<String, SystemConfig> cachedConfig = new HashMap<>(configList.size());
        for (SystemConfig config : configList) {
            cachedConfig.put(config.getPropKey(), config);
        }
        cacheManager.putCacheObj(Cons.CACHE_NAME_SYSTEM_CONFIG, handle(category), cachedConfig);
        log.debug("系统配置组 {} 的 {} 条数据已缓存", category, configList.size());
    }

    private String handle(String category) {
        return V.isEmpty(category) ? "__null__" : category;
    }
}
