/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package diboot.core.test.util;

import com.diboot.core.cache.DynamicMemoryCacheManager;
import com.diboot.core.entity.Dictionary;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * V校验工具类测试
 * @author mazc@dibo.ltd
 * @version 1.0
 * @date 2019/06/02
 */
public class CacheTest {

    @Test
    public void testCache(){
        String CACHE_TMPL = "CODE_TMPL";
        Map<String, Dictionary> dictionaryMap = new HashMap<>();
        dictionaryMap.put("G", new Dictionary().setType("GENDER"));
        DynamicMemoryCacheManager cacheManager = new DynamicMemoryCacheManager(1, CACHE_TMPL);
        cacheManager.putCacheObj(CACHE_TMPL, "version", dictionaryMap);
        Map<String, Dictionary> dictionaryMapCache = cacheManager.getCacheObj(CACHE_TMPL, "version", Map.class);
        Assert.assertTrue(dictionaryMapCache.get("G").getType().equals("GENDER"));
        boolean isExpired = cacheManager.isExpired(CACHE_TMPL, "version");
        Assert.assertFalse(isExpired);
        try{
            Thread.sleep(60*1000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        dictionaryMapCache = cacheManager.getCacheObj(CACHE_TMPL, "version", Map.class);
        Assert.assertTrue(dictionaryMapCache == null);
        isExpired = cacheManager.isExpired(CACHE_TMPL, "version");
        Assert.assertTrue(isExpired);
    }

}
