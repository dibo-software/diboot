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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.Cons;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.*;
import com.diboot.core.vo.Status;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.job.anno.BindJob;
import com.diboot.scheduler.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 调度任务quartz实现
 *
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/11/27
 */
@Slf4j
@Service
public class QuartzSchedulerService {

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
    public List<Map<String, Object>> loadAllJobs() {
        if (V.notEmpty(CACHE_JOB)) {
            return CACHE_JOB;
        }
        // 获取所有被com.diboot.scheduler.job.anno.BindJob注解的job
        List<Object> jobByScheduledAnnotationList = ContextHelper.getBeansByAnnotation(BindJob.class);
        if (V.isEmpty(jobByScheduledAnnotationList)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object job : jobByScheduledAnnotationList) {
            if (!(job instanceof Job)) {
                log.warn("无效的job任务: {}", job.getClass());
                continue;
            }
            Map<String, Object> temp = new HashMap<>(8);
            Class targetClass = BeanUtils.getTargetClass(job);

            BindJob annotation = (BindJob) targetClass.getAnnotation(BindJob.class);
            temp.put("jobCron", annotation.cron());
            temp.put("jobName", annotation.name());
            temp.put("jobClass", targetClass);
            String paramJsonExample = "";
            if (V.notEmpty(annotation.paramJson())) {
                paramJsonExample = annotation.paramJson();
            } else if (!Object.class.getTypeName().equals(annotation.paramClass().getTypeName())) {
                try {
                    paramJsonExample = JSON.stringify(annotation.paramClass().newInstance());
                } catch (Exception e) {
                    log.error("job任务：{}, Scheduled#paramClass参数任务无效，建议使用Scheduled#paramJson参数替换！", job.getClass());
                }
            }
            temp.put("paramJsonExample", paramJsonExample);
            result.add(temp);
        }
        CACHE_JOB.addAll(result);
        return result;
    }

    /**
     * 获取调度器重的所有任务列表
     *
     * @return
     */
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
     * @return
     */
    public String addJob(ScheduleJob job) {
        try {
            if (V.isEmpty(job.getJobKey())) {
                job.setJobKey(S.newUuid());
            }
            // 设置job
            if (V.isEmpty(job.getJobClass())) {
                setJobClass(job);
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobKey());
            // 构建参数
            JobDetail jobDetail = JobBuilder.newJob(job.getJobClass()).withIdentity(job.getJobKey()).build();
            if (V.notEmpty(job.getParamJson())) {
                Map<String, Object> jsonData = JSON.toMap(job.getParamJson());
                jobDetail.getJobDataMap().putAll(jsonData);
            }
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
        }
        return job.getJobKey();
    }

    /**
     * 添加一个立即执行一次的job
     *
     * @param job
     * @return
     */
    public String addJobExecuteOnce(ScheduleJob job) {
        try {
            if (V.isEmpty(job.getJobKey())) {
                job.setJobKey(S.newUuid());
            }
            // 设置job
            if (V.isEmpty(job.getJobClass())) {
                setJobClass(job);
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobKey());
            // 构建参数
            JobDetail jobDetail = JobBuilder.newJob(job.getJobClass()).withIdentity(job.getJobKey()).build();
            if (V.notEmpty(job.getParamJson())) {
                Map<String, Object> jsonData = JSON.toMap(job.getParamJson());
                jobDetail.getJobDataMap().putAll(jsonData);
            }
            // 立即执行,且只执行一次
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0)).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("添加定时任务异常", e);
        }
        return job.getJobKey();
    }


    /**
     * 立即执行job
     *
     * @param jobKey
     */
    public void runJob(String jobKey) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobKey));
        } catch (SchedulerException e) {
            log.error("运行job异常", e);
        }
    }

    /**
     * 暂停job
     *
     * @param jobKey
     */
    public void pauseJob(String jobKey) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobKey));
        } catch (Exception e) {
            log.error("暂停job异常", e);
        }
    }

    /**
     * 恢复job
     *
     * @param jobKey
     */
    public void resumeJob(String jobKey) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobKey));
        } catch (SchedulerException e) {
            log.error("恢复job异常", e);
        }
    }

    /**
     * 删除job
     *
     * @param jobKey
     */
    public void deleteJob(String jobKey) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobKey);
            if (V.notEmpty(triggerKey)) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(JobKey.jobKey(jobKey));
            }
        } catch (Exception e) {
            log.error("删除job异常", e);
        }
    }

    /**
     * 更新一个job的cron表达式
     *
     * @param jobKey
     * @param cron   定时表达式
     */
    public void updateJobCron(String jobKey, String cron) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobKey);
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
    public boolean existJob(String jobKey) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobKey);
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            return V.notEmpty(trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置ScheduleJob#JobClass
     *
     * @param entity
     */
    public void setJobClass(ScheduleJob entity) {
        String jobName = V.isEmpty(entity.getJobName()) ? "" : entity.getJobName();
        if (V.isEmpty(CACHE_JOB)) {
            try {
                loadAllJobs();
            } catch (Exception e) {
                throw new BusinessException(Status.FAIL_OPERATION, "定时任务加载失败！");
            }

        }
        Map<String, Object> jobMap = CACHE_JOB.stream()
                .filter(s -> jobName.equals(String.valueOf(s.get("jobName"))))
                .findFirst()
                .get();
        if (jobMap == null) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "非法的定时任务名称!");
        }
        entity.setJobClass((Class<? extends Job>) jobMap.get("jobClass"));
    }

}