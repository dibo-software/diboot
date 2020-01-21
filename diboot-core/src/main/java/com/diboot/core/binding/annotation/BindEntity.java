package com.diboot.core.binding.annotation;

import java.lang.annotation.*;

/**
 * 绑定Entity 注解定义
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindEntity {
    /***
     * 对应的service类
     * @return
     */
    Class entity();

    /***
     * JOIN连接条件
     * @return
     */
    String condition();
}