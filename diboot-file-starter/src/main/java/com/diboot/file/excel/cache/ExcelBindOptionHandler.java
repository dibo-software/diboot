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
package com.diboot.file.excel.cache;

import com.alibaba.excel.util.ClassUtils;
import com.diboot.file.excel.annotation.ExcelOption;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 绑定选项注解的辅助类
 *
 * @author wind
 * @version v2.3.0
 * @date 2021-05-29 22:36
 */
public class ExcelBindOptionHandler {

    /**
     * Excel下拉选项注解缓存
     */
    private static final Map<Class<?>, SoftReference<Map<Integer, ExcelOption>>> MODEL_OPTION_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取选项注解Map
     *
     * @param clazz ExcelModel
     * @return 选项注解Map
     */
    public static Map<Integer, ExcelOption> getOptionsAnnotationMap(Class<?> clazz) {
        SoftReference<Map<Integer, ExcelOption>> fieldCacheSoftReference = MODEL_OPTION_CACHE.get(clazz);

        if (fieldCacheSoftReference != null && fieldCacheSoftReference.get() != null) {
            return fieldCacheSoftReference.get();
        }
        synchronized (clazz) {
            fieldCacheSoftReference = MODEL_OPTION_CACHE.get(clazz);
            if (fieldCacheSoftReference != null && fieldCacheSoftReference.get() != null) {
                return fieldCacheSoftReference.get();
            }
            // 获取ExcelModel字段排序后的Map
            Map<Integer, Field> sortedAllFiledMap = new TreeMap<>();
            ClassUtils.declaredFields(clazz, sortedAllFiledMap, null, false, null);

            Map<Integer, ExcelOption> map = new TreeMap<>();
            for (Map.Entry<Integer, Field> entry : sortedAllFiledMap.entrySet()) {
                ExcelOption option = entry.getValue().getAnnotation(ExcelOption.class);
                if (option == null) {
                    continue;
                }
                map.put(entry.getKey(), option);
            }
            MODEL_OPTION_CACHE.put(clazz, new SoftReference<>(map));
            return map;
        }
    }
}
