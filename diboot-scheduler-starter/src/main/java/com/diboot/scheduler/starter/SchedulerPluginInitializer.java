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
import com.diboot.core.starter.SqlHandler;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.service.ScheduleJobService;
import com.diboot.scheduler.service.impl.QuartzSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务插件
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2020-11-27
 */
@Slf4j
@Component
@Order(940)
public class SchedulerPluginInitializer implements ApplicationRunner {

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库是否已存在
        if(schedulerProperties.isInitSql()){
            Environment environment = ContextHelper.getApplicationContext().getEnvironment();
            SqlHandler.init(environment);
            // 验证SQL
            String initDetectSql = "SELECT id FROM ${SCHEMA}.schedule_job WHERE id=0";
            if(SqlHandler.checkSqlExecutable(initDetectSql) == false){
                log.info("diboot-scheduler 初始化SQL ...");
                // 执行初始化SQL
                SqlHandler.initBootstrapSql(this.getClass(), environment, "scheduler");
                log.info("diboot-scheduler 初始化SQL完成.");
            }
        }
        // 初始化job
        initJob();
    }

    /**
     * 初始化job
     */
    private void initJob() {
        // 初始化数据库里面状态为启用的定时任务
        List<ScheduleJob> scheduleJobList = scheduleJobService.getEntityList(
                Wrappers.<ScheduleJob>lambdaQuery()
                        .eq(ScheduleJob::getJobStatus, Cons.ENABLE_STATUS.A.name())
        );
        if (V.notEmpty(scheduleJobList)) {
            scheduleJobList.stream().forEach(scheduleJob -> quartzSchedulerService.addJob(scheduleJob));
        }
    }

}
