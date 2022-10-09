/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.core.handler;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常统一处理的默认实现
 * （继承自该类并添加@ControllerAdvice注解即可自动支持兼容页面和JSON的异常处理）
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/07/19
 */
@SuppressWarnings("JavaDoc")
public class DefaultExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    /**
     * 统一处理校验错误 BindResult
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public Object validExceptionHandler(Exception ex){
        BindingResult br = null;
        if(ex instanceof BindException){
            br = ((BindException)ex).getBindingResult();
        }
        Map<String, Object> map = new HashMap<>(8);
        if (br != null && br.hasErrors()) {
            map.put("code", Status.FAIL_VALIDATION.code());
            String validateErrorMsg = V.getBindingError(br);
            map.put("msg", validateErrorMsg);
            map.put("ok", false);
            log.warn("数据校验失败, {}: {}", br.getObjectName(), validateErrorMsg);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * 统一异常处理类
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, Exception e) {
        HttpStatus status = getStatus(request);
        Map<String, Object> map;
        if(e instanceof BusinessException){
            map = ((BusinessException)e).toMap();
        }
        else if(e.getCause() instanceof BusinessException){
            map = ((BusinessException)e.getCause()).toMap();
        }
        else if(e instanceof InvalidUsageException){
            map = ((InvalidUsageException)e).toMap();
        }
        else if(e.getCause() instanceof InvalidUsageException){
            map = ((InvalidUsageException)e.getCause()).toMap();
        }
        else{
            map = new HashMap<>(8);
            map.put("code", status.value());
            String msg = buildMsg(status, e);
            map.put("msg", msg);
            map.put("ok", false);
        }
        log.warn("请求处理异常", e);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * 构建 response msg 内容
     * @param status
     * @param e
     * @return
     */
    protected String buildMsg(HttpStatus status, Exception e){
        // 返回最原始的异常信息
        return e == null ? status.getReasonPhrase() : getRootCauseMessage(e);
    }

    /**
     * 获取状态码
     * @param request
     * @return
     */
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * 返回最开始的异常信息
     *
     * @param t 异常对象
     * @return 异常信息
     */
    public static String getRootCauseMessage(Throwable t) {
        List<Throwable> list = ExceptionUtils.getThrowableList(t);
        Assert.notEmpty(list, () -> "没有异常信息");
        return list.get(list.size() - 1).getMessage();
    }
}
