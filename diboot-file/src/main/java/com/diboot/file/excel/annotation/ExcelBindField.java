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
 * 绑定字段 （1-1）
 * @author mazc@dibo.ltd
 * @version v2.1
 * @date 2019/1/21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExcelBindField {
    /***
     * 绑定的Entity类
     * @return
     */
    Class entity();

    /***
     * 绑定字段
     * @return
     */
    String field();

    /***
     * 设置ID至哪个字段，默认当前字段
     * @return
     */
    String setIdField() default "";

    /**
     * 重复策略
     * @return
     */
    DuplicateStrategy duplicate() default DuplicateStrategy.WARN;

    /**
     * 空值策略
     * @return
     */
    EmptyStrategy empty() default EmptyStrategy.WARN;

}