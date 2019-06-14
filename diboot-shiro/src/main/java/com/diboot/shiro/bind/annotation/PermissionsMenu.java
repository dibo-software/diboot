package com.diboot.shiro.bind.annotation;

import java.lang.annotation.*;

/**
 * controller上注解，用于标记权限的菜单分类
 * @author : wee
 * @version v2.0
 * @Date 2019-06-14  23:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PermissionsMenu {
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
}
