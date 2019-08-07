package com.diboot.shiro.authz.annotation;

import java.lang.annotation.*;

/**
 * 权限缓存
 * <p>
 * 缓存目的：在资源授权校验过程中，系统会频繁与数据库进行交互，故而提供缓存机制<br/>
 * 缓存时机：缓存会在用户第一次进行权限验证的之后缓存数据
 * 当前注解作用：如果通过系统调整角色的权限，只需要将该注解加在更新或添加权限处，将会清空缓存，下次进入将重新加载
 * </p>
 * @author : wee
 * @version v1.0
 * @Date 2019-07-23  09:27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizationCache {
}
