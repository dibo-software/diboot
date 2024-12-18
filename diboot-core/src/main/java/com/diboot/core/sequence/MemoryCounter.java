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
package com.diboot.core.sequence;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存计数器
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/18
 */
public class MemoryCounter implements ICounter {

    protected final Map<String, String> cacheTime = new ConcurrentHashMap<>();
    protected final Map<String, AtomicLong> cache = new ConcurrentHashMap<>();

    @Override
    public synchronized void setValue(String key, String date, long value) {
        if (checkValidity(key, date)) return;
        cache.put(key, new AtomicLong(value));
        cacheTime.put(key, date);
    }

    @Override
    public boolean checkValidity(String key, String date) {
        return cacheTime.containsKey(key) && Objects.equals(date, cacheTime.get(key));
    }

    @Override
    public Long increment(String key) {
        return cache.get(key).incrementAndGet();
    }

}
