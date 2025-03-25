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
package com.diboot.core.extension;

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动赋值字段值处理类工厂类
 * @author JerryMa
 * @version v3.6.0
 * @date 2024/12/10
 */
@Slf4j
public class AutoFillHandlerFactory {

    private static final Map<String, AutoFillHandler> HANDLER_CACHE_MAP = new LinkedHashMap<>();

    private static final List<LabelValue> DEFINITIONS = new ArrayList<>();

    /**
     * 根据id标识获取handler实例
     * @param handlerId 处理器标识
     * @return AutoFillHandler
     */
    public static AutoFillHandler getFillHandler(String handlerId) {
        initIfRequired();
        AutoFillHandler fillHandler = HANDLER_CACHE_MAP.get(handlerId);
        if (fillHandler == null) {
            throw new InvalidUsageException("未找到自动赋值处理器 AutoFillHandler: {} 的实现类，请检查！", handlerId);
        }
        return fillHandler;
    }

    /**
     * 获取所有的填充器定义
     * @return
     */
    public static List<LabelValue> getAllDefinitions() {
        initIfRequired();
        return DEFINITIONS;
    }

    /**
     * 初次调用初始化缓存
     */
    private static void initIfRequired() {
        if (HANDLER_CACHE_MAP.isEmpty()) {
            DEFINITIONS.clear();
            List<AutoFillHandler> fillHandlerList = ContextHolder.getBeans(AutoFillHandler.class);
            if (V.notEmpty(fillHandlerList)) {
                fillHandlerList.forEach(handler -> {
                    HANDLER_CACHE_MAP.put(S.valueOf(handler.definition().getValue()), handler);
                    DEFINITIONS.add(handler.definition());
                });
            }
        }
    }

}
