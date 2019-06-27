package com.diboot.shiro.authz.annotation;

import org.apache.shiro.authz.annotation.*;

import java.lang.annotation.*;

/**
 * 权限包装：目前只包装{@link RequiresPermissions},可以根据需要包装{@link RequiresRoles}
 * @author : wee
 * @version v 2.0
 * @Date 2019-06-19  00:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AuthorizationWrapper {

    /**RequiresPermissions包装*/
    RequiresPermissions value();

    /**权限别名：用于存储到数据库，与{@link RequiresPermissions#value()}的值一一对应*/
    String[] name();

    /**设置前缀：用于拼接，详细描述参考{@link AuthorizationPrefix#prefix()}*/
    String prefix() default "";

    /**是否忽略前缀：默认不忽略*/
    boolean ignorePrefix() default false;
}
