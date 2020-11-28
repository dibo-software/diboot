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
package com.diboot.scheduler.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.mapper.ScheduleJobMapper;
import com.diboot.scheduler.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
* 定时任务Job定义Service实现
* @author mazc@dibo.ltd
* @version 2.2
* @date 2020-11-27
*/
@Service
@Slf4j
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    @Override
    public boolean createEntity(ScheduleJob entity) {
        quartzSchedulerService.addJob(entity);
        return super.createEntity(entity);
    }

    @Override
    public boolean updateEntity(ScheduleJob entity) {
        ScheduleJob oldJob = getEntity(entity.getId());
        // 定时不同
        if(V.notEquals(oldJob.getCron(), entity.getCron())){
            quartzSchedulerService.updateJobCron(oldJob.getJobKey(), entity.getCron());
        }
        return super.updateEntity(entity);
    }

    @Override
    public boolean deleteEntity(Serializable jobId) {
        String jobKeyObj = getValueOfField(ScheduleJob::getId, jobId, ScheduleJob::getJobKey);
        quartzSchedulerService.deleteJob(jobKeyObj);
        return super.deleteEntity(jobId);
    }

    @Override
    public boolean changeJobState(Long jobId, String action) {
        String jobKeyObj = getValueOfField(ScheduleJob::getId, jobId, ScheduleJob::getJobKey);
        if("run".equals(action)){
            quartzSchedulerService.runJob((String)jobKeyObj);
        }
        else if("resume".equals(action)){
            quartzSchedulerService.resumeJob((String)jobKeyObj);
        }
        else if("pause".equals(action)){
            quartzSchedulerService.pauseJob((String)jobKeyObj);
        }
        else{
            log.warn("无效的action参数: {}", action);
        }
        return true;
    }

}