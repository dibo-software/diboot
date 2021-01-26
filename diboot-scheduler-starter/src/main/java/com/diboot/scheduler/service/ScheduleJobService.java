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
package com.diboot.scheduler.service;

import com.diboot.core.service.BaseService;
import com.diboot.scheduler.annotation.BindJob;
import com.diboot.scheduler.entity.ScheduleJob;

import java.util.List;
import java.util.Map;

/**
 * 定时任务Job定义Service
 *
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2020-11-27
 */
public interface ScheduleJobService extends BaseService<ScheduleJob> {

    /**
     * 执行一次job
     *
     * @param jobId
     * @return
     */
    boolean executeOnceJob(Long jobId);

    /**
     * 对${@link ScheduleJob}进行操作 【启用/停用】
     * <p>
     * 操作数据库的任务记录
     *
     * @param jobId
     * @param action
     * @return
     */
    boolean changeScheduleJobStatus(Long jobId, String action);

    /**
     * 获取所有被{@link BindJob}注解的job
     *
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getAllJobs() throws Exception;

}