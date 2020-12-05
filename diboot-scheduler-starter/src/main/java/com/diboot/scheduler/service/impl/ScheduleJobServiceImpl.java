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
import com.diboot.scheduler.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
@Service
@Slf4j
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    @Override
    public boolean createEntity(ScheduleJob entity) {
        boolean success = super.createEntity(entity);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "创建定时任务失败!");
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
            throw new BusinessException(Status.FAIL_OPERATION, "更新定时任务失败!");
        }
        // 如果参数发生变化 或状态为I,或策略改变，那么先触发删除原来的job
        if (V.notEquals(oldJob.getParamJson(), entity.getParamJson())
                || V.equals(oldJob.getJobStatus(), Cons.ENABLE_STATUS.I.name())
                || V.notEquals(oldJob.getInitStrategy(), entity.getInitStrategy())
                || V.notEquals(oldJob.getCron(), entity.getCron())
        ) {
            quartzSchedulerService.deleteJob(entity.getJobKey());
        }
        if (V.equals(entity.getJobStatus(), Cons.ENABLE_STATUS.A.name())) {
            quartzSchedulerService.addJob(entity);
        }
        return true;
    }

    @Override
    public boolean deleteEntity(Serializable jobId) {
        String jobKeyObj = getValueOfField(ScheduleJob::getId, jobId, ScheduleJob::getJobKey);
        quartzSchedulerService.deleteJob(jobKeyObj);
        return super.deleteEntity(jobId);
    }

    @Override
    public boolean executeOnceJob(Long jobId) {
        String jobKeyObj = getValueOfField(ScheduleJob::getId, jobId, ScheduleJob::getJobKey);
        // 如果已经存在job，那么直接恢复，否则添加job
        if (!quartzSchedulerService.existJob(jobKeyObj)) {
            ScheduleJob entity = this.getEntity(jobId);
            if (V.isEmpty(entity)) {
                throw new BusinessException(Status.FAIL_OPERATION, "当前任务无效！");
            }
            quartzSchedulerService.addJobExecuteOnce(entity);
        } else {
            quartzSchedulerService.runJob(jobKeyObj);
        }

        return true;
    }

    @Override
    public boolean changeScheduleJobStatus(Long jobId, String jobStatus) {
        boolean success = this.update(
                Wrappers.<ScheduleJob>lambdaUpdate()
                        .set(ScheduleJob::getJobStatus, jobStatus)
                        .eq(ScheduleJob::getId, jobId)
        );
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "更新状态失败！");
        }
        String jobKeyObj = getValueOfField(ScheduleJob::getId, jobId, ScheduleJob::getJobKey);
        // 恢复
        if (Cons.ENABLE_STATUS.A.name().equals(jobStatus)) {
            // 如果已经存在job，那么直接恢复，否则添加job
            if (quartzSchedulerService.existJob(jobKeyObj)) {
                quartzSchedulerService.resumeJob((String) jobKeyObj);
            } else {
                ScheduleJob entity = this.getEntity(jobId);
                if (V.isEmpty(entity)) {
                    throw new BusinessException(Status.FAIL_OPERATION, "当前任务无效！");
                }
                quartzSchedulerService.addJob(entity);
            }
        }
        // 停止
        else if (Cons.ENABLE_STATUS.I.name().equals(jobStatus)) {
            quartzSchedulerService.pauseJob((String) jobKeyObj);
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