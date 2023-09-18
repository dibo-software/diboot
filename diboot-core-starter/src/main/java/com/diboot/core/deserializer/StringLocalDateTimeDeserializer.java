package com.diboot.core.deserializer;

import com.diboot.core.util.D;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author : uu
 * @version : v1.0
 * @Date 2023/9/18  15:05
 */
public class StringLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    private static final DateTimeFormatter dateFormatterY4m2d = DateTimeFormatter.ofPattern(D.FORMAT_DATE_Y4MD);
    private static final DateTimeFormatter dateFormatterY4M2dhms = DateTimeFormatter.ofPattern(D.FORMAT_DATETIME_Y4MDHMS);

    public StringLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String dateString = p.readValueAs(String.class);
        dateString = D.formatDateString(dateString);
        if (dateString.length() <= D.FORMAT_DATE_Y4MD.length()) {
            return LocalDate.parse(dateString, dateFormatterY4m2d).atStartOfDay();
        }
        return LocalDateTime.parse(dateString, dateFormatterY4M2dhms);
    }
}
