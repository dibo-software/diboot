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
package com.diboot.core.binding.helper;

import com.diboot.core.util.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * 虚拟线程执行器（JDK21及以上版本自动启用）
 * @author diboot@dibo.ltd
 * @version v3.7.0
 * @date 2025/5/29
 */
@Slf4j
public class VirtualThreadExecutor {

    private static Boolean isSupportVirtualThread = null;

    // 获取兼容的线程执行器
    public static SimpleAsyncTaskExecutor getVirtualThreadExecutor() {
        if (isSupportVirtualThread()) {
            return createVirtualThreadExecutor();
        }
        return null;
    }

    /**
     * 检测JDK版本是否支持虚拟线程
     */
    private static boolean isSupportVirtualThread() {
        if (isSupportVirtualThread == null) {
            try {
                isSupportVirtualThread = Runtime.version().feature() >= 21;
            }
            catch (Exception e) {
                isSupportVirtualThread = false;
            }
        }
        if (isSupportVirtualThread) {
            log.info("当前环境支持虚拟线程，将启用虚拟线程执行关联绑定。");
        }
        return isSupportVirtualThread;
    }

    /**
     * 创建虚拟线程执行器（JDK 21+）
     */
    private static SimpleAsyncTaskExecutor createVirtualThreadExecutor() {
        try {
            return ContextHolder.getBean(SimpleAsyncTaskExecutor.class);
        }
        catch (Exception e) {
            log.debug("当前环境JDK版本不支持虚拟线程");
            return null;
        }
    }

}
