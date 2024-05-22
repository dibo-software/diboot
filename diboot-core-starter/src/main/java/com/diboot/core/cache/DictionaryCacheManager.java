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
package com.diboot.core.cache;

import com.diboot.core.config.Cons;
import com.diboot.core.entity.Dictionary;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 字典缓存管理器
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/22
 */
@Slf4j
public class DictionaryCacheManager {

    private BaseCacheManager cacheManager;

    public DictionaryCacheManager(BaseCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * 移除缓存的字典选项数据
     * @param type
     */
    public void removeCachedItems(String type) {
        if(cacheManager == null) {
            return;
        }
        cacheManager.removeCacheObj(Cons.CACHE_NAME_DICTIONARY, type);
    }

    /**
     * 从缓存中获取字典选项数据
     * @param type
     * @return
     */
    public List<Dictionary> getCachedItems(String type) {
        if(cacheManager == null) {
            return null;
        }
        return cacheManager.getCacheObj(Cons.CACHE_NAME_DICTIONARY, type, List.class);
    }

    /**
     * 缓存新查询到的字典选项数据
     * @param type
     * @param dictList
     */
    public void cacheItems(String type, List<Dictionary> dictList) {
        if(cacheManager == null) {
            return;
        }
        cacheManager.putCacheObj(Cons.CACHE_NAME_DICTIONARY, type, dictList);
        log.debug("字典 {} 的选项数据已缓存", type);
    }
}
