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

import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;

/**
 * jackson "field0010":"[\"2\",\"1\",\"3\"]" 转化成List<String>
 * <p>
 *     使用方式：字段上增加 @JsonDeserialize(using = StringListDeserializer.class)
 * </p>
 *
 * @author : uu
 * @version : v3
 * @Date 2023/12/05
 */
public class StringListDeserializer extends StdDeserializer<List<String>> {
    private static final long serialVersionUID = -4162970093906595310L;

    public StringListDeserializer() {
        super(List.class);
    }

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String stringList = p.readValueAs(String.class);
        if (V.notEmpty(stringList)) {
            return JSON.parseArray(stringList, new TypeReference<List<String>>() {
            });
        } else {
            return null;
        }
    }
}
