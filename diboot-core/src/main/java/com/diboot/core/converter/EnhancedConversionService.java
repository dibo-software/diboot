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
        addConverter(new LocalDate2DateConverter());
        addConverter(new LocalDateTime2DateConverter());
        addConverter(new SqlDate2LocalDateConverter());
        addConverter(new SqlDate2LocalDateTimeConverter());
        addConverter(new String2DateConverter());
        addConverter(new String2LocalDateConverter());
        addConverter(new String2LocalDateTimeConverter());
        addConverter(new String2BooleanConverter());
        addConverter(new Timestamp2LocalDateTimeConverter());
    }

}
