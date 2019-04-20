package com.diboot.core.binding.parser;

import java.lang.annotation.Annotation;

/**
 * 字段名与注解的包装对象关系 <br>
 *
 * @author Mazhicheng<br>
 * @version 1.0<br>
 * @date 2019/04/04 <br>
 */
public class FieldAnnotation{
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 注解
     */
    private Annotation annotation;

    public FieldAnnotation(String fieldName, Annotation annotation){
        this.fieldName = fieldName;
        this.annotation = annotation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}