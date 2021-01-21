package com.diboot.core.handler;

import com.diboot.core.util.D;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * 自定义时间反序列化配置
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/21  22:04
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return D.fuzzyConvert(jsonParser.getText());
    }
}
