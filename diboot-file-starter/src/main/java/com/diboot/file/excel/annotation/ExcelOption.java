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
package com.diboot.file.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel下拉选项
 * <p>可自定选项与关联字典</p>
 *
 * @author wind
 * @version v2.3.0
 * @date 2021-05-29 21:30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExcelOption {

    /**
     * 选项列表
     *
     * @return options
     */
    String[] value() default {};

    /**
     * 关联字典类型
     *
     * @return 字典类型
     */
    String dict() default "";

    /**
     * 行数（默认10000）
     *
     * @return 行数
     */
    int rows() default 10_000;
}
