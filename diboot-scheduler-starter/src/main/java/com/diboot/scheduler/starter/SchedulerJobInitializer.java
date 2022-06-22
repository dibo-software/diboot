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
package com.diboot.scheduler.starter;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.Cons;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.service.QuartzSchedulerService;
import com.diboot.scheduler.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 定时任务初始化
 * <p>
 * 在定时任务启用时，初始化数据库里面状态为启用的定时任务
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/29
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.quartz", name = "job-store-type", havingValue = "MEMORY", matchIfMissing = true)
public class SchedulerJobInitializer implements ApplicationRunner {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    @Override
    public void run(ApplicationArguments args) {
        scheduleJobService.getEntityList(
                Wrappers.<ScheduleJob>lambdaQuery().eq(ScheduleJob::getJobStatus, Cons.ENABLE_STATUS.A.name())
        ).forEach(scheduleJob -> {
            try {
                quartzSchedulerService.addJob(scheduleJob);
            } catch (Exception e) {
                log.error("定时任务：jobKey={}，初始化加载失败！", scheduleJob.getJobKey(), e);
            }
        });
    }
}
