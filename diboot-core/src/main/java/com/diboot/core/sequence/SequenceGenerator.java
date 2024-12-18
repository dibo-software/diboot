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

import com.diboot.core.util.D;
import com.diboot.core.util.S;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 序列生成
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
public abstract class SequenceGenerator {

    private final ICounter counter;

    protected ResetRule rule = ResetRule.NONE;

    /**
     * 获取初始数值
     *
     * @return 初始数值
     */
    protected abstract long getInitValue();

    public Long incrementAndGet() {
        String key = S.substringAfterLast(this.toString(), ".");
        String date = getDate();
        if (!counter.checkValidity(key, date))
            counter.setValue(key, date, getInitValue());
        return counter.increment(key);
    }

    protected String getDate() {
        return switch (rule) {
            case YEAR -> D.now("yyyy-01-01");
            case MONTH -> D.now("yyyy-MM-01");
            case DAY -> D.now("yyyy-MM-dd");
            case NONE -> null;
        };
    }

    /**
     * 构建序列值
     *
     * @param formData 表单数据
     * @return 序列值
     */
    public abstract Object buildFillValue(Map<String, Object> formData);

    /**
     * 重置规则
     */
    public enum ResetRule {
        YEAR,
        MONTH,
        DAY,
        /**
         * 不重置
         */
        NONE;
    }
}
