/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.scheduler.handler;

import com.diboot.core.util.*;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.process.IamAsyncWorker;
import com.diboot.iam.config.Cons;
import com.diboot.scheduler.entity.ScheduleJobLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * job执行日志的切面处理
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/11/30
 */
@Aspect
@Component
@Slf4j
public class JobAspect {
    @Autowired
    private SchedulerAsyncWorker schedulerAsyncWorker;
    private static final int maxLength = 500;

    /**
     * 切面
     */
    @Pointcut("execution(void *.execute*(org.quartz.JobExecutionContext))")
    public void pointCut() {}

    /**
     * 操作日志处理
     * @param joinPoint
     */
    @AfterReturning(value = "pointCut()", returning = "returnObj")
    public void afterReturningHandler(JoinPoint joinPoint, Object returnObj) {
        ScheduleJobLog jobLog = buildScheduleJobLog(joinPoint);
        // 异步保存日志
        schedulerAsyncWorker.saveScheduleJobLog(jobLog);
    }

    /**
     * 操作日志处理
     * @param joinPoint
     */
    @AfterThrowing(value = "pointCut()", throwing = "throwable")
    public void afterThrowingHandler(JoinPoint joinPoint, Throwable throwable) {
        ScheduleJobLog jobLog = buildScheduleJobLog(joinPoint);
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
        jobLog.setRunStatus(Cons.RESULT_STATUS.F.name()).setExecuteMsg(errorMsg);
        // 异步保存日志
        schedulerAsyncWorker.saveScheduleJobLog(jobLog);
    }

    /**
     * 构建Job执行日志对象
     * @param joinPoint
     * @return
     */
    private ScheduleJobLog buildScheduleJobLog(JoinPoint joinPoint){
        ScheduleJobLog jobLog = new ScheduleJobLog();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter parameter = method.getParameters()[0];
        //JobExecutionContext context = (JobExecutionContext)

        return jobLog;
    }

}