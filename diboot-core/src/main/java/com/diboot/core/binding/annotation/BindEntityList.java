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
public @interface BindEntityList {
    /***
     * 对应的entity类
     * @return
     */
    Class entity();

    /***
     * JOIN连接条件
     * @return
     */
    String condition();
}