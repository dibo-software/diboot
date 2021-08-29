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
package com.diboot.scheduler.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.entity.ScheduleJobLog;
import com.diboot.scheduler.service.ScheduleJobLogService;
import com.diboot.scheduler.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 定时任务异步相关处理
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/10/21
 */
@Slf4j
@Async
@Component
public class SchedulerAsyncWorker {
    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    /**
     * 保存job执行日志
     * @param scheduleJobLog
     */
    public void saveScheduleJobLog(ScheduleJobLog scheduleJobLog) {
        try{
            LambdaQueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<ScheduleJob>()
                    .lambda().eq(ScheduleJob::getJobKey, scheduleJobLog.getJobKey());
            ScheduleJob scheduleJob = scheduleJobService.getSingleEntity(queryWrapper);
            if(scheduleJob != null){
                scheduleJobLog.setCron(scheduleJob.getCron())
                        .setJobId(scheduleJob.getId())
                        .setJobName(scheduleJob.getJobName())
                        .setParamJson(scheduleJob.getParamJson())
                        .setTenantId(scheduleJob.getTenantId());
            }
            scheduleJobLogService.createEntity(scheduleJobLog);
        }
        catch (Exception e){
            log.error("保存Job执行日志异常", e);
        }
    }

}
