/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * Spring表单自动绑定到Java属性时的日期格式转换<br>
 * @see com.diboot.core.converter.String2DateConverter
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
@Deprecated
public class DateConverter implements Converter<String, Date> {
    private static final Logger log = LoggerFactory.getLogger(DateConverter.class);

    @Override
    public Date convert(String dateString) {
        return D.fuzzyConvert(dateString);
    }
}