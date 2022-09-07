/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.scheduler.service;

import com.diboot.scheduler.entity.ScheduleJob;

import java.util.List;
import java.util.Map;

/**
 * 调度任务quartz
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/01/12
 */
public interface QuartzSchedulerService {

    /**
     * 获取调度器重的所有任务列表
     *
     * @return
     */
    List<Map<String, Object>> loadJobsInScheduler();

    /**
     * 添加 cron表达式job
     *
     * @param job
     */
    void addJob(ScheduleJob job);

    /**
     * 添加一个立即执行一次的job
     *
     * @param job
     */
    void addJobExecuteOnce(ScheduleJob job);

    /**
     * 更新一个job的cron表达式
     *
     * @param jobId
     * @param cron
     */
    void updateJobCron(String jobId, String cron);

    /**
     * 删除job
     *
     * @param jobId
     */
    void deleteJob(String jobId);


    /**
     * 判断是否存在job
     *
     * @param jobId
     * @return
     */
    boolean existJob(String jobId);

    /**
     * 立即执行job
     *
     * @param jobId
     */
    void runJob(String jobId);

    /**
     * 暂停job
     *
     * @param jobId
     */
    void pauseJob(String jobId);

    /**
     * 恢复job
     *
     * @param jobId
     */
    void resumeJob(String jobId);

    /**
     * 获取当前系统中的所有定时任务
     *
     * @return
     */
    List<Map<String, Object>> loadAllJobs();

}
