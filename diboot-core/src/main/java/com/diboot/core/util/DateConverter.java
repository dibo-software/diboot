package com.diboot.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * Spring表单自动绑定到Java属性时的日期格式转换
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
public class DateConverter implements Converter<String, Date> {
    private static final Logger log = LoggerFactory.getLogger(DateConverter.class);

    @Override
    public Date convert(String dateString) {
        return D.fuzzyConvert(dateString);
    }
}