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
@RequestMapping("/authorization")
@AuthorizationPrefix(name = "测试权限", code = "authorization", prefix = "authorization")
public class AuthorizationWrapperController {

    /**此处权限为：authorization:get 或 authorization:test*/
    @GetMapping("/get")
    @AuthorizationWrapper(value = @RequiresPermissions(value = {"get", "test"},
                            logical = Logical.OR),
                            name = {"查看", "测试"})
    public JsonResult get() {
        return new  JsonResult("ok");
    }

    /**此处权限为：getAll 或 test*/
    @GetMapping("/getAll")
    @AuthorizationWrapper(value = @RequiresPermissions({"getAll", "test"}),
                          name = {"获取所有", "测试"},
                          ignorePrefix = true)
    public JsonResult getAll() {
        return new  JsonResult("ok");
    }
}
