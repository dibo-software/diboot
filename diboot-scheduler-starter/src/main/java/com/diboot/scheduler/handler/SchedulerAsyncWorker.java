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

import com.diboot.core.util.JSON;
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
 *
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/10/21
 */
@Slf4j
@Async("schedulerAsyncExecutor")
@Component
public class SchedulerAsyncWorker {
    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    /**
     * 保存job执行日志
     *
     * @param scheduleJobLog
     */
    public void saveScheduleJobLog(ScheduleJobLog scheduleJobLog) {
        ScheduleJob scheduleJob = scheduleJobService.getEntity(scheduleJobLog.getJobId());
        if (scheduleJob == null) {
            log.error("获取定时任务`{}`异常；日志内容：{}", scheduleJobLog.getJobId(), JSON.toJSONString(scheduleJobLog));
        } else {
            scheduleJobLog.setJobKey(scheduleJob.getJobKey())
                    .setCron(scheduleJob.getCron())
                    .setJobName(scheduleJob.getJobName())
                    .setParamJson(scheduleJob.getParamJson())
                    .setTenantId(scheduleJob.getTenantId());
            if (scheduleJob.getSaveLog()) {
                scheduleJobLogService.createEntity(scheduleJobLog);
            } else {
                log.debug("定时任务`{}`关闭了日志记录，日志内容：{}", scheduleJobLog.getJobId(), JSON.toJSONString(scheduleJobLog));
            }
        }
    }

}
