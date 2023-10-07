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

import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 序列号生成器的工厂类
 * @author mazc@dibo.ltd
 * @version v3.1.1
 * @date 2023/10/07
 */
@Slf4j
public class SerialNumberGeneratorFactory {

    private static final Map<String, SerialNumberGenerator> GENERATOR_CACHE_MAP = new LinkedHashMap<>();

    private static final List<LabelValue> DEFINITIONS = new ArrayList<>();

    /**
     * 根据id标识获取generator实例
     * @param generatorId 生成器标识
     * @return SerialNumberGenerator
     */
    public static SerialNumberGenerator getGenerator(String generatorId) {
        initIfRequired();
        SerialNumberGenerator generator = GENERATOR_CACHE_MAP.get(generatorId);
        if (generator == null) {
            log.warn("无法找到序列号生成器: {} 的实现类，请检查！", generatorId);
        }
        return generator;
    }

    /**
     * 获取所有的生成器定义
     * @return
     */
    public static List<LabelValue> getAllDefinitions() {
        initIfRequired();
        return DEFINITIONS;
    }

    private static void initIfRequired() {
        if (GENERATOR_CACHE_MAP.isEmpty()) {
            DEFINITIONS.clear();
            List<SerialNumberGenerator> generatorList = ContextHolder.getBeans(SerialNumberGenerator.class);
            if (V.notEmpty(generatorList)) {
                generatorList.forEach(generator -> {
                    Assert.notNull(generator.definition(), "definition接口返回值不可为空");
                    GENERATOR_CACHE_MAP.put(S.valueOf(generator.definition().getValue()), generator);
                    DEFINITIONS.add(generator.definition());
                });
            }
        }
    }

}
