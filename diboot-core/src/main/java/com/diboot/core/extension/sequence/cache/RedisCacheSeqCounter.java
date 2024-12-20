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
package com.diboot.core.extension.sequence.cache;

import com.diboot.core.extension.sequence.SeqCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis 计数器
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/18
 */
@RequiredArgsConstructor
public class RedisCacheSeqCounter implements SeqCounter {

    protected final RedisTemplate<String, Object> redisTemplate;

    @Override
    public synchronized void setValue(String key, String date, Long value) {
        if (checkValidity(key, date)) return;
        redisTemplate.opsForValue().set(key + ":value", value, 7, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(key + ":date", date,7, TimeUnit.DAYS);
    }

    @Override
    public synchronized boolean checkValidity(String key, String date) {
        String k = key + ":date";
        return redisTemplate.hasKey(k) && Objects.equals(date, redisTemplate.opsForValue().get(k));
    }

    @Override
    public synchronized Long increment(String key) {
        return redisTemplate.opsForValue().increment(key + ":value");
    }

}
