package com.diboot.example.controller;

import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : wee
 * @version : v todo
 * @Date 2019-10-10  16:37
 */
@RestController
public class TestController {
    @GetMapping("/jwturl")
    public JsonResult get() {
        return new JsonResult(Status.FAIL_EXCEPTION);
    }

    @GetMapping("/public/anon")
    public JsonResult anon() {
        return new JsonResult(Status.FAIL_EXCEPTION);
    }
}
