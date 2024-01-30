/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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

import org.apache.poi.ss.usermodel.DataValidation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Excel 单元格验证 （单元格下拉选项）
 * <p>
 * 可自定义选项或关联字典
 *
 * @author wind
 * @version v2.3.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExcelOption {

    /**
     * 此属性为{@link #options}的别名
     * <p>
     * 当不指定其他属性值时使用而不是指定{@link #options} &mdash; 例如：{@code @ExcelOption({"选项1","选项2"})}
     *
     * @see #options
     */
    @AliasFor("options")
    String[] value() default {};

    /**
     * 下拉选项列表
     * <p>
     * {@link #value}是此属性的别名（并与之互斥）。
     * <p>
     * 优先级: dict &gt; options
     */
    @AliasFor("value")
    String[] options() default {};

    /**
     * 关联字典类型
     * <p>
     * 优先级: dict &gt; options
     */
    String dict() default "";

    /**
     * 默认填充行数
     * <p>
     * 当 rows <= 0 时为个整列
     * <p>
     * 默认值 1000
     */
    int rows() default 1000;

    /**
     * 错误框样式
     * @see DataValidation.ErrorStyle
     */
    int errorStyle() default DataValidation.ErrorStyle.INFO;

}
