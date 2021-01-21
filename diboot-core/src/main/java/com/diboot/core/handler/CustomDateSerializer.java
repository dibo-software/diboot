package com.diboot.core.handler;

import com.diboot.core.util.D;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * 自定义时间序列化
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/21  22:04
 */
public class CustomDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(D.convert2FormatString(date, D.FORMAT_DATETIME_Y4MDHMS));
    }
}
