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
package com.diboot.core.serial.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Jackson 格式化输出 BigDecimal：去除末尾的0
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2024/1/22
 */
public class BigDecimal2StringSerializer extends StdSerializer<BigDecimal> {
    private static final long serialVersionUID = -8536893521899234130L;

    public static final BigDecimal2StringSerializer instance = new BigDecimal2StringSerializer();

    public BigDecimal2StringSerializer() {
        super(BigDecimal.class);
    }

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(bigDecimal != null) {
            String formatValue = bigDecimal.stripTrailingZeros().toPlainString();
            jsonGenerator.writeString(formatValue);
        }
    }

}
