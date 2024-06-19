/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.holder;

import lombok.extern.slf4j.Slf4j;

/**
 * ThreadLocal 变量管理
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/30
 */
@Slf4j
public class ThreadLocalHolder {

    /**
     * 忽略查询拦截器
     */
    private static ThreadLocal<Boolean> ignoreInterceptorFlag = new ThreadLocal<>();

    /**
     * 设置变量 = true
     */
    public static void setIgnoreInterceptor() {
        ignoreInterceptorFlag.set(true);
        log.debug("设置 IgnoreInterceptor 变量 = true");
    }

    /**
     * 忽略拦截器
     * @return
     */
    public static boolean ignoreInterceptor() {
        Boolean ignoreQuery = ignoreInterceptorFlag.get();
        if(ignoreQuery == null) {
            return false;
        }
        ignoreInterceptorFlag.remove();
        log.debug("获取到 IgnoreInterceptor 变量并移除");
        return ignoreQuery;
    }

}
