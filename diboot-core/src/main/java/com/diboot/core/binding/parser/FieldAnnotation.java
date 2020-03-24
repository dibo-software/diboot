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