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

import java.util.function.Supplier;

/**
 * 计数器
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/18
 */
public interface ICounter {

    /**
     * 设置起始值
     *
     * @param key   KEY
     * @param date  日期
     * @param value 值的提供者
     */
    void setValue(String key, String date, Supplier<Long> value);

    /**
     * 检查有效性
     *
     * @param key  KEY
     * @param date 日期
     * @return true 有效，false 无效
     */
    boolean checkValidity(String key, String date);

    /**
     * 获取加1后的值
     *
     * @param key KEY
     * @return 新数值
     */
    Long increment(String key);

}
