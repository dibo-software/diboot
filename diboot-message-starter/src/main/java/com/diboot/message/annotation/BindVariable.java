package com.diboot.message.annotation;

import java.lang.annotation.*;

/**
 * 绑定的变量
 *
 * @author : uu
 * @version v1.0
 * @Date 2021/2/18  19:39
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindVariable {

    /**
     * 绑定变量名称
     *
     * @return
     */
    String name();
}
