package com.diboot.core.handle;

import com.diboot.core.enumerate.ErrorPageEnum;
import com.diboot.core.exception.RestException;
import com.diboot.core.exception.WebException;
import com.diboot.core.properties.DibootProperties;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 异常统一捕获类：只捕获{@link RestException} 和 {@link WebException}及其子类
 *
 * <p>
 *     如果没有特殊要求，系统中可以直接抛出{@link RestException} 和 {@link WebException}异常<br/>
 *     如果想对每个异常有个具体的描述，方便排查，可以继承上述两个类，进行异常细化描述
 * </p>
 * <p>
 *     如果上述两个异常不满足要求，可以自定义异常捕获
 * </p>
 *
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  11:13
 */
@ControllerAdvice
public class DefaultExceptionAdviceHandler {
    private final static Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Autowired
    private DibootProperties dibootProperties;

    /**
     * 捕获{@link RestException}及其子类异常，返回{@link JsonResult}
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(RestException.class)
    public JsonResult advice(RestException e) {
        String msg = V.notEmpty(e.getMsg()) ? S.join(e.getStatus().label(), ":", e.getMsg()) : e.getStatus().label();
        log.error("【rest错误】<== 错误码：{}，错误信息:{}", e.getStatus().code(), msg, e);
        return new JsonResult(e.getStatus(), e.getData(), (V.isEmpty(e.getMsg()) ? "" : e.getMsg()));
    }

    /**
     * 捕获{@link WebException}及其子类异常，
     * <p>如果配置了自定义错误页面，那么跳转到指定的错误页面，否则返回spring错误页面</p>
     * <p>错误页面目前提供三种类型的枚举:{@link ErrorPageEnum}，有且仅可返回这三种类型错误</p>
     *
     * @param we
     * @return
     */
    @ResponseBody
    @ExceptionHandler(WebException.class)
    public ModelAndView advice(WebException we) {
        log.error("【web错误】<==", we);
        //获取配置信息
        String errorUrl = dibootProperties.getError().getPage().get(we.getErrorPage());
        if (V.notEmpty(errorUrl)) {
            //存在页面跳转至自定义页面
            return new ModelAndView("redirect:" + errorUrl);
        }
        return new ModelAndView();
    }
}
