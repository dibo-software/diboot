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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.Cons;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.mapper.ScheduleJobMapper;
import com.diboot.scheduler.service.QuartzSchedulerService;
import com.diboot.scheduler.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 定时任务Job定义Service实现
 *
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2020-11-27
 */
@Slf4j
@Service
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    @Override
    public boolean createEntity(ScheduleJob entity) {
        boolean success = super.createEntity(entity);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.scheduleJobService.createFailed");
        }
        // 只有启用情况才加入任务队列
        if (V.equals(entity.getJobStatus(), Cons.ENABLE_STATUS.A.name())) {
            quartzSchedulerService.addJob(entity);
        }
        return true;
    }

    @Override
    public boolean updateEntity(ScheduleJob entity) {
        // 更新后对系统的定时任务进行操作
        ScheduleJob oldJob = getEntity(entity.getId());
        boolean success = super.updateEntity(entity);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.scheduleJobService.updateFailed");
        }
        // job如果存在且参数发生了变化，那么先触发删除原来的job
        if (quartzSchedulerService.existJob(entity.getId()) &&
                (V.notEquals(oldJob.getJobStatus(), entity.getJobStatus())
                || V.notEquals(oldJob.getJobKey(), entity.getJobKey())
                || V.notEquals(oldJob.getCron(), entity.getCron())
                || V.notEquals(oldJob.getParamJson(), entity.getParamJson())
                || V.notEquals(oldJob.getInitStrategy(), entity.getInitStrategy()))
        ) {
            quartzSchedulerService.deleteJob(entity.getId());
            if (V.equals(entity.getJobStatus(), Cons.ENABLE_STATUS.A.name())) {
                quartzSchedulerService.addJob(entity);
            }
        }
        return true;
    }

    @Override
    public boolean deleteEntity(Serializable jobId) {
        quartzSchedulerService.deleteJob((String) jobId);
        return super.deleteEntity(jobId);
    }

    @Override
    public boolean executeOnceJob(String jobId) {
        // 如果已经存在job，那么直接恢复，否则添加job
        if (!quartzSchedulerService.existJob(jobId)) {
            ScheduleJob entity = this.getEntity(jobId);
            if (V.isEmpty(entity)) {
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.scheduleJobService.invalidTask");
            }
            quartzSchedulerService.addJobExecuteOnce(entity);
        } else {
            quartzSchedulerService.runJob(jobId);
        }

        return true;
    }

    @Override
    public boolean changeScheduleJobStatus(String jobId, String jobStatus) {
        boolean success = this.update(
                Wrappers.<ScheduleJob>lambdaUpdate()
                        .set(ScheduleJob::getJobStatus, jobStatus)
                        .eq(ScheduleJob::getId, jobId)
        );
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.scheduleJobService.updateStatusFailed");
        }
        // 恢复
        if (Cons.ENABLE_STATUS.A.name().equals(jobStatus)) {
            // 如果已经存在job，那么直接恢复，否则添加job
            if (quartzSchedulerService.existJob(jobId)) {
                quartzSchedulerService.resumeJob(jobId);
            } else {
                ScheduleJob entity = this.getEntity(jobId);
                if (V.isEmpty(entity)) {
                    throw new BusinessException(Status.FAIL_OPERATION, "exception.business.scheduleJobService.invalidTask");
                }
                quartzSchedulerService.addJob(entity);
            }
        }
        // 停止
        else if (Cons.ENABLE_STATUS.I.name().equals(jobStatus)) {
            quartzSchedulerService.pauseJob(jobId);
        } else {
            log.warn("无效的action参数: {}", jobStatus);
        }

        return true;
    }

    @Override
    public List<Map<String, Object>> getAllJobs() throws Exception {
        return quartzSchedulerService.loadAllJobs();
    }
}
