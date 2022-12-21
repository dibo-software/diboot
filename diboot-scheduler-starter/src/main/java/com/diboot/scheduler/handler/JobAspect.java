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

import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.scheduler.entity.ScheduleJobLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * job执行日志的切面处理
 *
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

    private static final int MAX_LENGTH = 500;

    /**
     * 切面
     */
    @Pointcut("execution(void *.execute*(org.quartz.JobExecutionContext))")
    public void pointCut() {
    }

    /**
     * 定时任务日志处理
     *
     * @param joinPoint
     */
    @Around(value = "pointCut()")
    public void afterHandler(ProceedingJoinPoint joinPoint) {
        ScheduleJobLog jobLog = new ScheduleJobLog();
        jobLog.setJobId(Long.valueOf(((JobExecutionContext) joinPoint.getArgs()[0]).getJobDetail().getKey().getName()));
        try {
            jobLog.setStartTime(new Date());
            joinPoint.proceed(joinPoint.getArgs());
            jobLog.setEndTime(new Date());
            long seconds = (jobLog.getEndTime().getTime() - jobLog.getStartTime().getTime()) / 1000;
            jobLog.setElapsedSeconds(seconds).setRunStatus(Cons.RESULT_STATUS.S.name()).setExecuteMsg("执行成功");
        } catch (Throwable throwable) {
            log.error("定时任务执行异常", throwable);
            // 处理异常返回结果
            String errorMsg = throwable.toString();
            StackTraceElement[] stackTraceElements = throwable.getStackTrace();
            if (V.notEmpty(stackTraceElements)) {
                errorMsg += " : " + stackTraceElements[0].toString();
            }
            errorMsg = S.cut(Status.FAIL_EXCEPTION.code() + ":" + errorMsg, MAX_LENGTH);
            jobLog.setRunStatus(Cons.RESULT_STATUS.F.name()).setExecuteMsg(errorMsg);
        }
        // 异步保存日志
        schedulerAsyncWorker.saveScheduleJobLog(jobLog);
    }
}
