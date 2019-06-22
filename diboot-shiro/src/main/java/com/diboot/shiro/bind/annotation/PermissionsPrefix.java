package com.diboot.shiro.bind.annotation;

import java.lang.annotation.*;

/**
 * 权限注解的前缀，用于controller注解
 *
 * @author : wee
 * @version v 2.0
 * @Date 2019-06-17  20:42
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PermissionsPrefix {

    /**
     * 名称
     *
     * 设置当前权限前缀名称
     * @return
     */
    String name();

    /**
     * 编码
     *
     * 设置当前权限前缀编码
     * @return
     */
    String code();

    /**
     * <h3>{@link RequiresPermissionsWrapper#value()}的前缀</h3>
     * <ul>
     *     <li> value = permissions</li>
     *     <li>{@link RequiresPermissionsWrapper#value()} = {"list", "get"}</li>
     *     <li>实际权限为：{"permissions:list", "permissions:list"}</li>
     * </ul>
     * 注：当前注解优先级低于{@link RequiresPermissionsWrapper#prefix()},
     * 如果两者都配置优先使用{@link RequiresPermissionsWrapper#prefix()}
     * @return
     */
    String prefix() default "";
}
