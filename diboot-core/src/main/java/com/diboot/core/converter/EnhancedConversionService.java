package com.diboot.core.converter;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

/**
 * 扩展的转换service
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/11
 * Copyright © diboot.com
 */
@Component
public class EnhancedConversionService extends DefaultConversionService {

    public EnhancedConversionService(){
        super();
        addConverter(new Date2LocalDateConverter());
        addConverter(new Date2LocalDateTimeConverter());
        addConverter(new String2DateConverter());
        addConverter(new String2BooleanConverter());
        addConverter(new Timestamp2LocalDateTimeConverter());
    }

}
