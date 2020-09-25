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
package com.diboot.iam.annotation.process;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamOperationLog;
import com.diboot.iam.util.AnnotationUtils;
import com.diboot.iam.util.IamHelper;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Log操作日志注解的切面处理
 * @author mazc@dibo.ltd
 * @version v2.1.2
 * @date 2020/09/21
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private AsyncWorker asyncWorker;

    private static int maxLength = 1000;

    /**
     * 注解切面
     */
    @Pointcut("@annotation(com.diboot.iam.annotation.Log)")
    public void pointCut() {}

    /**
     * 操作日志处理
     * @param joinPoint
     */
    @AfterReturning(value = "pointCut()", returning = "returnObj")
    public void afterReturningHandler(JoinPoint joinPoint, Object returnObj) {
        IamOperationLog operationLog = buildOperationLog(joinPoint);
        // 处理返回结果
        int statusCode = 0;
        String errorMsg = null;
        if(returnObj instanceof JsonResult){
            JsonResult jsonResult = (JsonResult)returnObj;
            statusCode = jsonResult.getCode();
            if(statusCode > 0){
                errorMsg = jsonResult.getMsg();
            }
        }
        operationLog.setStatusCode(statusCode).setErrorMsg(errorMsg);
        // 异步保存操作日志
        asyncWorker.saveOperationLog(operationLog);
    }

    /**
     * 操作日志处理
     * @param joinPoint
     */
    @AfterThrowing(value = "pointCut()", throwing = "throwable")
    public void afterThrowingHandler(JoinPoint joinPoint, Throwable throwable) {
        IamOperationLog operationLog = buildOperationLog(joinPoint);
        // 处理返回结果
        int statusCode = Status.FAIL_EXCEPTION.code();
        String errorMsg = null;
        if(throwable != null){
            errorMsg = throwable.toString();
            StackTraceElement[] stackTraceElements = throwable.getStackTrace();
            if(V.notEmpty(stackTraceElements)){
                errorMsg += " : " + stackTraceElements[0].toString();
            }
            errorMsg = S.cut(errorMsg, maxLength);
        }
        operationLog.setStatusCode(statusCode).setErrorMsg(errorMsg);
        // 异步保存操作日志
        asyncWorker.saveOperationLog(operationLog);
    }

    /**
     * 构建操作日志对象
     * @param joinPoint
     * @return
     */
    private IamOperationLog buildOperationLog(JoinPoint joinPoint){
        IamOperationLog operationLog = new IamOperationLog();
        // 当前请求信息
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
        operationLog.setRequestMethod(request.getMethod())
                .setRequestUri(request.getRequestURI())
                .setRequestIp(IamSecurityUtils.getRequestIp(request));
        // 请求参数
        Map<String, Object> params = IamHelper.buildParamsMap(request);
        String paramsJson = JSON.stringify(params);
        paramsJson = S.cut(paramsJson, maxLength);
        operationLog.setRequestParams(paramsJson);
        // 操作用户信息
        BaseLoginUser loginUser = IamSecurityUtils.getCurrentUser();
        if(loginUser != null){
            operationLog.setUserType(loginUser.getClass().getSimpleName())
                    .setUserId(loginUser.getId())
                    .setUserRealname(loginUser.getDisplayName());
        }
        // 补充注解信息
        // 需要验证
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnno = AnnotationUtils.getAnnotation(method, Log.class);
        String businessObj = logAnno.businessObj();
        if(V.isEmpty(businessObj)){
            Class clazz = method.getDeclaringClass();
            Class entityClazz = BeanUtils.getGenericityClass(clazz, 0);
            if(entityClazz != null){
                businessObj = entityClazz.getSimpleName();
            }
            else{
                log.warn("@Log(operation='{}') 注解未识别到class泛型参数，请指定 businessObj", logAnno.operation());
            }
        }
        operationLog.setBusinessObj(businessObj).setOperation(logAnno.operation());

        return operationLog;
    }

}
