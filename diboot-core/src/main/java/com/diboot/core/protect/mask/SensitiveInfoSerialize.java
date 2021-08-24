/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.protect.mask;

import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.protect.annotation.ProtectField;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 敏感信息序列化
 * <p>
 * 对保护字段进行脱敏处理
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/19
 */
@Slf4j
public class SensitiveInfoSerialize<E> extends JsonSerializer<E> implements ContextualSerializer {

    /**
     * 脱敏策略
     */
    private IMaskStrategy maskStrategy;

    public SensitiveInfoSerialize() {
    }

    public SensitiveInfoSerialize(Class<? extends IMaskStrategy> clazz) {
        this.maskStrategy = ParserCache.getMaskStrategy(clazz);
    }

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof List) {
            gen.writeObject(((List<String>) value).stream().map(e -> maskStrategy.mask(e)).collect(Collectors.toList()));
        } else {
            gen.writeObject(maskStrategy.mask((String) value));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (null == property) {
            return prov.findNullValueSerializer(null);
        }
        Class<?> rawClass = property.getType().getRawClass();
        if (rawClass == String.class || (rawClass == List.class && property.getType().getContentType().getRawClass() == String.class)) {
            ProtectField protect = property.getAnnotation(ProtectField.class);
            if (null == protect) {
                protect = property.getContextAnnotation(ProtectField.class);
            }
            if (null != protect) {
                return new SensitiveInfoSerialize(protect.mask());
            }
        } else {
            log.error("`@ProtectField` 只支持 String 与 List<String> 类型脱敏！");
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
