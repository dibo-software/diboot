/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.config;

import com.diboot.core.exception.BusinessException;

/**
 * <h3>系统配置测试</h3>
 * 用于配置枚举类继承，实现测试方法
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/01/13
 */
public interface SystemConfigTest<T> {

    /**
     * <h3>测试</h3>
     * 可使用{@link BusinessException} 抛出运行时异常提示
     *
     * @param data 测试数据
     */
    void test(T data);

}
