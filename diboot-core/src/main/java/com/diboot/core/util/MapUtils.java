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
package com.diboot.core.util;

import java.util.Map;

/**
 * Map相关工具类
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/6/15
 * Copyright © diboot.com
 */
public class MapUtils {

    /**
     * 忽略key大小写，兼容多库等场景
     * @param map
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getIgnoreCase(Map<String, T> map, String key) {
        if(map.containsKey(key)) {
            return map.get(key);
        }
        if(map.containsKey(key.toUpperCase())) {
            return map.get(key.toUpperCase());
        }
        return map.get(key.toLowerCase());
    }

}
