package com.diboot.shiro.authz.annotation;

import java.lang.annotation.*;

/**
 * 权限注解的前缀，用于controller注解<br/>
 * <strong>注：当你使用{@link AuthorizationWrapper}，请在类上使用{@link AuthorizationPrefix}注解进行标记，系统启动的时候才会将权限入库</strong>
 * @author : wee
 * @version v 2.0
 * @Date 2019-06-17  20:42
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthorizationPrefix {

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
     * <h3>{@link AuthorizationWrapper#value()#value()}的前缀</h3>
     * <ul>
     *     <li> value = permissions</li>
     *     <li>{@link AuthorizationWrapper#value()#value()} = {"list", "get"}</li>
     *     <li>实际权限为：{"permissions:list", "permissions:list"}</li>
     * </ul>
     * 注：当前注解优先级低于{@link AuthorizationWrapper#prefix()},
     * 如果两者都配置优先使用{@link AuthorizationWrapper#prefix()}
     * @return
     */
    String prefix() default "";
}
