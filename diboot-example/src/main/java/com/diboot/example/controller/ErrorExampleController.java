package com.diboot.example.controller;

import com.diboot.core.controller.BaseController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 错误异常展示
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  15:02
 */
@Controller
@RequestMapping("/error")
public class ErrorExampleController extends BaseController {

    /**
     * 测试自定义
     * @param num
     * @return
     */
    @GetMapping("/rest/{num}")
    @ResponseBody
    public JsonResult testRest(@PathVariable("num") Integer num) {
        if (num == 1) {
            throw new BusinessException();
        }
        if (num == 2) {
            throw new BusinessException(Status.FAIL_EXCEPTION);
        }
        if (num == 3) {
            throw new BusinessException(Status.FAIL_EXCEPTION, "自定义描述");
        }

        if (num == 4) {
            throw new BusinessException(Status.FAIL_NO_PERMISSION, "将传入的数据返回数据");
        }

        if (num == 5) {
            try{
                int i=2/0;
            }
            catch (Exception e){
                throw new BusinessException(Status.FAIL_NO_PERMISSION, "继承rest异常", e);
            }
        }

        if (num == 6) {
            throw new RuntimeException("eeeeeee");
        }
        return new JsonResult();
    }

    @GetMapping("/web/{num}")
    public ModelAndView testWeb(@PathVariable("num") Integer num) {
        if (num == 1) {
            throw new BusinessException(Status.FAIL_NO_PERMISSION);
        }
        if (num == 2) {
            throw new BusinessException(Status.FAIL_INVALID_TOKEN);
        }
        if(num == 3){
            int i=2/0;
        }
        return new ModelAndView("redirect:/index.html");
    }
}
