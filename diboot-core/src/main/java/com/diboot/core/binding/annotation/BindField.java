package com.diboot.core.binding.annotation;

import java.lang.annotation.*;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindField {
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
     * JOIN连接条件
     * @return
     */
    String condition();
}