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
package com.diboot.scheduler.service.impl;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.scheduler.annotation.CollectThisJob;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.service.QuartzSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 调度任务quartz实现
 *
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/11/27
 */
@Slf4j
public class QuartzSchedulerServiceImpl implements QuartzSchedulerService {

    /**
     * 任务初始化策略
     */
    enum INIT_STRATEGY {
        // 周期执行
        DO_NOTHING,
        //立即执行一次，并周期执行
        FIRE_AND_PROCEED,
        //超期立即执行，并周期执行
        IGNORE_MISFIRES
    }

    // 任务的缓存
    public static final List<Map<String, Object>> CACHE_JOB = new ArrayList<>();

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("定时任务scheduler初始化异常，请检查！", e);
        }
    }

    /**
     * 获取当前系统中的所有定时任务
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> loadAllJobs() {
        if (V.notEmpty(CACHE_JOB)) {
            return CACHE_JOB;
        }
        // 获取所有被com.diboot.scheduler.job.anno.CollectThisJob注解的job
        List<Object> annoJobList = ContextHolder.getBeansByAnnotation(CollectThisJob.class);
        if (V.notEmpty(annoJobList)) {
            List<Map<String, Object>> result = loadJobs(annoJobList);
            CACHE_JOB.addAll(result);
        }
        return CACHE_JOB;
    }

    /**
     * 加载job定义
     *
     * @param annoJobList
     * @return
     */
    private List<Map<String, Object>> loadJobs(List<Object> annoJobList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object job : annoJobList) {
            if (!(job instanceof Job)) {
                log.warn("无效的job任务: {}", job.getClass());
                continue;
            }
            Map<String, Object> temp = new HashMap<>(8);
            Class<?> targetClass = BeanUtils.getTargetClass(job);
            CollectThisJob annotation = targetClass.getAnnotation(CollectThisJob.class);
            temp.put("jobKey", targetClass.getSimpleName());
            temp.put("jobCron", annotation.cron());
            temp.put("jobName", annotation.name());
            temp.put("jobClass", targetClass);
            Class<?> paramClass = annotation.paramClass();
            String paramJsonExample = annotation.paramJson();
            if (V.isEmpty(paramJsonExample) && !Object.class.getTypeName().equals(paramClass.getTypeName())) {
                try {
                    paramJsonExample = JSON.stringify(paramClass.newInstance());
                } catch (Exception e) {
                    log.error("job任务：{}, Scheduled#paramClass参数任务无效，建议使用Scheduled#paramJson参数替换！", job.getClass());
                }
            }
            temp.put("paramJsonExample", paramJsonExample);
            result.add(temp);
        }
        return result;
    }

    /**
     * 获取调度器重的所有任务列表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> loadJobsInScheduler() {
        List<Map<String, Object>> jobList = null;
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            jobList = new ArrayList<>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobKey.getName());
                    map.put("jobGroupName", jobKey.getGroup());
                    map.put("description", trigger.getKey());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    map.put("jobStatus", triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        map.put("jobTime", cronExpression);
                    }
                    jobList.add(map);
                }
            }
        } catch (SchedulerException e) {
            log.error("加载全部Job异常", e);
        }
        return jobList;
    }

    /**
     * 添加 cron表达式job
     *
     * @param job
     */
    @Override
    public void addJob(ScheduleJob job) {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getId().toString());
        // 构建参数
        JobDetail jobDetail = JobBuilder.newJob(getJobClass(job.getJobKey())).withIdentity(job.getId().toString()).build();
        if (V.notEmpty(job.getParamJson())) {
            Map<String, Object> jsonData = JSON.toMap(job.getParamJson());
            jobDetail.getJobDataMap().putAll(jsonData);
        }
        try {
            // 表达式调度构建器
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron());
            // 设置定时任务初始化策略
            if (INIT_STRATEGY.FIRE_AND_PROCEED.name().equals(job.getInitStrategy())) {
                cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            } else if (INIT_STRATEGY.IGNORE_MISFIRES.name().equals(job.getInitStrategy())) {
                cronScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            }
            // 定时任务
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("添加定时任务异常", e);
            throw new BusinessException(Status.FAIL_OPERATION, e, "exception.business.quartzSchedulerService.addTaskFailed");
        }
    }

    /**
     * 添加一个立即执行一次的job
     *
     * @param job
     */
    @Override
    public void addJobExecuteOnce(ScheduleJob job) {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getId().toString());
        // 构建参数
        JobDetail jobDetail = JobBuilder.newJob(getJobClass(job.getJobKey())).withIdentity(job.getId().toString()).build();
        if (V.notEmpty(job.getParamJson())) {
            Map<String, Object> jsonData = JSON.toMap(job.getParamJson());
            jobDetail.getJobDataMap().putAll(jsonData);
        }
        try {
            // 立即执行,且只执行一次
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0)).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("添加定时任务异常", e);
            throw new BusinessException(Status.FAIL_OPERATION, e, "exception.business.quartzSchedulerService.addTaskFailed");
        }
    }


    /**
     * 立即执行job
     *
     * @param jobId
     */
    @Override
    public void runJob(String jobId) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobId.toString()));
        } catch (SchedulerException e) {
            log.error("运行job异常", e);
        }
    }

    /**
     * 暂停job
     *
     * @param jobId
     */
    @Override
    public void pauseJob(String jobId) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobId.toString()));
        } catch (Exception e) {
            log.error("暂停job异常", e);
        }
    }

    /**
     * 恢复job
     *
     * @param jobId
     */
    @Override
    public void resumeJob(String jobId) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobId.toString()));
        } catch (SchedulerException e) {
            log.error("恢复job异常", e);
        }
    }

    /**
     * 删除job
     *
     * @param jobId
     */
    @Override
    public void deleteJob(String jobId) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId.toString());
        try {
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(JobKey.jobKey(jobId.toString()));
        } catch (Exception e) {
            log.error("删除job异常", e);
        }
    }

    /**
     * 更新一个job的cron表达式
     *
     * @param jobId
     * @param cron  定时表达式
     */
    @Override
    public void updateJobCron(String jobId, String cron) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId.toString());
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            CronTrigger cronTrigger = (CronTrigger) trigger;
            if (!cronTrigger.getCronExpression().equals(cron)) {
                cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(cron)).startNow().build();
                scheduler.rescheduleJob(triggerKey, cronTrigger);
            }
        } catch (Exception e) {
            log.error("更新job的cron定时表达式异常", e);
        }
    }


    /**
     * 判断是否存在job
     *
     * @return
     */
    @Override
    public boolean existJob(String jobId) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId.toString());
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            return V.notEmpty(trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取JobClass
     *
     * @param jobKey
     * @return jobClass
     */
    public Class<? extends Job> getJobClass(String jobKey) {
        try {
            Class<? extends Job> jobClass = loadAllJobs().stream()
                    .filter(e -> String.valueOf(e.get("jobKey")).equals(jobKey))
                    .map(e -> (Class<? extends Job>) e.get("jobClass"))
                    .findAny()
                    .orElse(null);
            if (jobClass == null) {
                throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.quartzSchedulerService.illegalTask", jobKey);
            }
            return jobClass;
        } catch (Exception e) {
            log.error("定时任务加载失败", e);
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.quartzSchedulerService.loadFailed");
        }
    }
}
