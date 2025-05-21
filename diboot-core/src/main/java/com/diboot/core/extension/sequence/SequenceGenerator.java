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
package com.diboot.core.extension.sequence;

import com.diboot.core.extension.AutoFillHandler;
import com.diboot.core.extension.sequence.counter.SeqCounter;
import com.diboot.core.util.S;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 序列生成器
 * <p>
 * 支持 流水号 接力、重置
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/18
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public abstract class SequenceGenerator implements AutoFillHandler {

    private final SeqCounter seqCounter;

    /**
     * 获取初始数值
     *
     * @return 初始数值
     */
    protected abstract long getInitValue();

    public synchronized Long incrementAndGet() {
        String key = this.getClass().getName();
        String date = getCurrentDate();
        if (!seqCounter.hasCounter(key, date)) {
            seqCounter.initCounter(key, date, this.getInitValue());
        }
        return seqCounter.increment(key);
    }

    protected abstract String getCurrentDate();

}
