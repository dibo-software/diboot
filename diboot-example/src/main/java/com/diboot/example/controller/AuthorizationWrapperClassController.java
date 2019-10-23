package com.diboot.example.controller;

import com.diboot.core.vo.JsonResult;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限测试
 * @author : wee
 * @version : v 2.0
 * @Date 2019-06-19  13:37
 */
@RestController
@RequestMapping("/authorizationClass")
@AuthorizationPrefix(name = "前缀", code = "authorizationClass", prefix = "authorizationClass")
@AuthorizationWrapper(value = @RequiresPermissions("global"), name = "全局权限")
public class AuthorizationWrapperClassController {

    /**
     * 全局权限：authorizationClass:global
     * @return
     */
    @GetMapping("/test")
    public JsonResult test() {
        return new JsonResult();
    }

    /**
     *
     * 自定义权限1：authorizationClass:test1
     * @return
     */
    @GetMapping("/test1")
    @AuthorizationWrapper(value = @RequiresPermissions("test1"), name = "测试1")
    public JsonResult test1() {
        return new JsonResult();
    }

    /**
     * 自定义权限2：test2
     * @return
     */
    @GetMapping("/test2")
    @AuthorizationWrapper(value = @RequiresPermissions("test2"), name = "测试2", ignorePrefix = true)
    public JsonResult test2() {
        return new JsonResult();
    }

    /**
     * 自定义权限3：custom:test3
     * @return
     */
    @GetMapping("/test3")
    @AuthorizationWrapper(value = @RequiresPermissions("test3"), name = "测试3", prefix = "custom")
    public JsonResult test3() {
        return new JsonResult();
    }

    /**
     * 自定义权限4：同时满足 authorizationClass:test3 and authorizationClass:test4
     * @return
     */
    @GetMapping("/test4or5")
    @AuthorizationWrapper(value = @RequiresPermissions(value = {"test4", "test5"}, logical = Logical.AND),
                          name = {"测试4", "测试5"})
    public JsonResult test4or5() {
        return new JsonResult();
    }

    /**
     * @return
     */
    @GetMapping("/test6")
    @AuthorizationWrapper(value = @RequiresPermissions(value = "test7"),
                          name = "测试7")
    public JsonResult test6() {
        return new JsonResult();
    }
}
