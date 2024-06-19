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

import com.diboot.core.util.*;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamOperationLog;
import com.diboot.iam.util.HttpHelper;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    private IamAsyncWorker iamAsyncWorker;

    private static int maxLength = 1000;

    /**
     * 注解切面
     */
    @Pointcut("@annotation(com.diboot.iam.annotation.Log)")
    public void pointCut() {}

    private ThreadLocal<BaseLoginUser> threadLocal = new ThreadLocal<>();

    /**
     * 操作日志处理
     * @param joinPoint
     */
    @Before(value = "pointCut()")
    public void beforeHandler(JoinPoint joinPoint){
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        threadLocal.set(currentUser);
    }

    /**
     * 操作日志处理
     * @param proceedingJoinPoint
     */
    @AfterReturning(value = "pointCut()", returning = "returnObj")
    public void afterReturningHandler(JoinPoint proceedingJoinPoint, Object returnObj){
        IamOperationLog operationLog = buildOperationLog(proceedingJoinPoint);
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        if(currentUser == null){
            currentUser = threadLocal.get();
        }
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
        iamAsyncWorker.saveOperationLog(operationLog, currentUser);
        threadLocal.remove();
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
        iamAsyncWorker.saveOperationLog(operationLog, IamSecurityUtils.getCurrentUser());
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
                .setRequestIp(HttpHelper.getRequestIp(request));
        // 记录url请求参数 及 body参数
        Map<String, Object> paramsMap = new HashMap<>();
        if(V.notEmpty(request.getQueryString())){
            paramsMap.put("query", request.getQueryString());
        }
        // 补充注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnno = AnnotationUtils.getAnnotation(method, Log.class);
        // 保存requestBody数据
        if(logAnno.saveRequestData()){
            Object[] bodyParams = joinPoint.getArgs();
            if(V.notEmpty(bodyParams)){
                for(Object arg : bodyParams){
                    if(arg != null && isSimpleDataType(arg)){
                        paramsMap.put(arg.getClass().getSimpleName(), arg);
                    }
                }
            }
        }
        String paramsJson = null;
        if(V.notEmpty(paramsMap)){
            paramsJson = JSON.stringify(paramsMap);
        }
        operationLog.setRequestParams(paramsJson);

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

    /**
     * 是否为简单类型数据
     * @param arg
     * @return
     */
    private boolean isSimpleDataType(Object arg){
        Class<?> clazz = arg.getClass();
        if (arg instanceof Collection) {
            Collection collection = (Collection) arg;
            Object firstElement = collection.stream().findFirst().orElse(null);
            return isSimpleClassType(firstElement);
        }
        else if (clazz.isArray()) {
            return isSimpleClassType(clazz.getComponentType());
        }
        else if (arg instanceof Map) {
            Map map = (Map)arg;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                boolean isSimple = isSimpleClassType(entry.getValue().getClass());
                if(!isSimple) {
                    return false;
                }
            }
            return true;
        }
        return isSimpleClassType(arg);
    }

    private boolean isSimpleClassType(Object arg){
        if(arg == null) {
            return false;
        }
        Class<?> clazz = arg.getClass();
        boolean isSimple = org.springframework.beans.BeanUtils.isSimpleProperty(clazz);
        if(isSimple){
            return true;
        }
        if(arg instanceof InputStreamSource
            || S.containsIgnoreCase(arg.getClass().getName(), "multipart")
            || arg instanceof ServletRequest
            || arg instanceof ServletResponse
            || arg instanceof Errors)
        {
            return false;
        }
        return true;
    }

}
