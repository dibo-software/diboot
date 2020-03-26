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
package com.diboot.core.binding.parser;

import java.lang.annotation.Annotation;

/**
 * 字段名与注解的包装对象关系 <br>
 *
 * @author mazc@dibo.ltd<br>
 * @version 2.0<br>
 * @date 2019/04/04 <br>
 */
public class FieldAnnotation{
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private Class<?> fieldClass;
    /**
     * 注解
     */
    private Annotation annotation;

    public FieldAnnotation(String fieldName, Class fieldClass, Annotation annotation){
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
        this.annotation = annotation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Class getFieldClass(){
        return fieldClass;
    }
}