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
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典缓存管理器
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/22
 */
@Slf4j
public class I18nCacheManager {

    private BaseCacheManager cacheManager;

    public I18nCacheManager(BaseCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * 移除缓存的语言环境数据
     * @param language
     */
    public void removeCachedItems(String language) {
        if(cacheManager == null) {
            return;
        }
        cacheManager.removeCacheObj(Cons.CACHE_NAME_I18N, language);
    }

    /**
     * 从缓存中获取语言环境数据
     * @param language
     * @return
     */
    public Map<String, String> getLanguageCached(String language) {
        if(cacheManager == null) {
            return null;
        }
        return cacheManager.getCacheObj(Cons.CACHE_NAME_I18N, language, Map.class);
    }

    /**
     * 缓存查询到的语言环境数据
     * @param language
     * @param languageValue
     */
    public void cacheLanguage(String language, Map<String, String> languageValue) {
        if(cacheManager == null) {
            return;
        }
        // 获取已经缓存的数据
        Map<String, String> languageCached = getLanguageCached(language);
        if(V.isEmpty(languageCached)) {
            languageCached = new HashMap<>(255);
        }
        languageCached.putAll(languageValue);
        cacheManager.putCacheObj(Cons.CACHE_NAME_I18N, language, languageCached);
        log.debug("语言环境 {} 的 {} 条数据已缓存", language, languageValue.size());
    }
}
