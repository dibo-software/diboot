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
package com.diboot.core.serial.deserializer;

import com.diboot.core.util.D;
import com.diboot.core.util.V;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * jackson yyyy-MM-dd格式参数转换为LocalDateTime
 * @author : uu
 * @version : v1.0
 * @Date 2023/9/18  15:05
 */
public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    private static final long serialVersionUID = 8758976191733673106L;

    public LocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String dateString = p.readValueAs(String.class);
        if(V.isEmpty(dateString)) {
            return null;
        }
        dateString = D.formatDateTimeString(dateString);
        if(dateString.length() <= D.FORMAT_DATE_Y4MD.length()) {
            return LocalDate.parse(dateString, D.FORMATTER_DATE_Y4MD).atStartOfDay();
        }
        return LocalDateTime.parse(dateString, D.FORMATTER_DATETIME_Y4MDHMS);
    }
}
