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
package com.diboot.core.serial.serializer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Jackson 格式化输出 BigDecimal：去除末尾的0
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2024/1/22
 */
public class BigDecimal2StringSerializer extends ValueSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if(bigDecimal != null) {
            String formatValue = bigDecimal.stripTrailingZeros().toPlainString();
            gen.writeString(formatValue);
        }
    }

}
