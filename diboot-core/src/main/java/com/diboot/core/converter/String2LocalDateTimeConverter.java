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

import com.diboot.core.util.D;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * String - LocalDateTime 转换器
 * @author JerryMa
 * @version v2.7.0
 * @date 2022/7/26
 * Copyright © diboot.com
 */
public class String2LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String dateString) {
        dateString = D.formatDateString(dateString);
        if(dateString.length() <= D.FORMAT_DATE_Y4MD.length()) {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(D.FORMAT_DATE_Y4MD)).atStartOfDay();
        }
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(D.FORMAT_DATETIME_Y4MDHMS));
    }

}
