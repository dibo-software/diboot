/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.file.excel.annotation;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.*;

/**
 * Excel 颜色
 */
@Documented
@Target(ElementType.FIELD)
@Repeatable(ExcelColor.List.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColor {

    /**
     * 正则匹配
     */
    String regex();

    /**
     * 字体颜色
     */
    IndexedColors fontColor() default IndexedColors.AUTOMATIC;

    /**
     * 背景颜色
     */
    IndexedColors backgroundColor() default IndexedColors.AUTOMATIC;

    /**
     * 在同一个字段上支持多个{@link ExcelColor}，由上到下依次填充
     *
     * @author wind
     * @since v2.4.0
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        ExcelColor[] value();

    }
}
