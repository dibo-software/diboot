/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.annotation.process;

/**
 * 上下文抽象
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
public interface Context<T> {

    /**
     * 获取上下文
     */
    T get();

    /**
     * 设置上下文
     */
    void set(T context);

    /**
     * 清除上下文
     */
    void remove();
}