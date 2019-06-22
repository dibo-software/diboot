package com.diboot.shiro.bind.annotation;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.lang.annotation.*;

/**
 * 注解{@link RequiresPermissions}的包装，增加权限描述等字段
 * @author : wee
 * @version v2.0
 * @Date 2019-06-14  17:50
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequiresPermissionsWrapper {

    /**
     * 包装 {@link RequiresPermissions#value()}
     */
    String[] value();

    /**
     * 包装 {@link RequiresPermissions#logical()}
     */
    Logical logical() default Logical.AND;

    /**
     * 权限名称
     * @return
     */
    String name();

    /**
     * 参照{@link PermissionsPrefix#prefix()}解释
     * @return
     */
    String prefix() default "";




}
