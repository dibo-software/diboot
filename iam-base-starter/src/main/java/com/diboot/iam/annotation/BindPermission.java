package com.diboot.iam.annotation;

import java.lang.annotation.*;

/**
 * 绑定IAM权限 - 添加在Controller上的注解，可以支持：
 * 1. 自动将此注解code值作为前缀添加至Mapping方法上的Shiro注解的value中
 * 2. 注解自动入库，自动更新
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface BindPermission {
    /**
     * 名称
     * 设置当前权限名称
     * @return
     */
    String name();
    /**
     * 当前权限编码，默认为Entity类名
     * @return
     */
    String code() default  "";
}