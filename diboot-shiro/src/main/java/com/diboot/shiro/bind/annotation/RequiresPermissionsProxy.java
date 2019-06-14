package com.diboot.shiro.bind.annotation;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.lang.annotation.*;

/**
 * @author : wee
 * @version v2.0
 * @Date 2019-06-14  17:50
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@RequiresPermissions("default")
public @interface RequiresPermissionsProxy {

    /**
     * 代理 {@link RequiresPermissions#value()}
     */
    String[] value();

    /**
     * 代理 {@link RequiresPermissions#logical()}
     */
    Logical logical() default Logical.AND;

    /**
     * 菜单编码
     * @return
     */
    String menuCode();
    /**
     * 菜单名称
     * @return
     */
    String menuName();

    /**
     * 权限名称
     * @return
     */
    String permissionName();


}
