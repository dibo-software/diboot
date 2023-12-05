package com.diboot.core.deserializer;

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
