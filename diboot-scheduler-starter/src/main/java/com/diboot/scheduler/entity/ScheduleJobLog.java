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
package com.diboot.scheduler.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 任务执行日志 Entity定义
 *
 * @author yaojf
 * @version 2.2.0
 * @date 2020-11-26
 * Copyright © dibo.ltd
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_schedule_job_log")
public class ScheduleJobLog extends BaseEntity<String> {
    private static final long serialVersionUID = -1854961913574904234L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    /**
     * 任务id
     */
    @NotNull(message = "{validation.scheduleJobLog.jobId.NotNull.message}")
    @TableField()
    private Long jobId;

    @TableField(exist = false)
    private String jobKey;

    /**
     * 任务名称
     */
    @NotNull(message = "{validation.scheduleJobLog.jobName.NotNull.message}")
    @Length(max = 50, message = "{validation.scheduleJobLog.jobName.Length.message}")
    @TableField()
    @BindQuery(comparison = Comparison.LIKE)
    private String jobName;

    /**
     * 执行表达式
     */
    @NotNull(message = "{validation.scheduleJobLog.cron.NotNull.message}")
    @Length(max = 100, message = "{validation.scheduleJobLog.cron.Length.message}")
    @TableField()
    private String cron;

    /**
     * 参数json字符串
     */
    @TableField()
    private String paramJson;

    /**
     * 开始时间
     */
    @TableField()
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField()
    private LocalDateTime endTime;

    /**
     * 耗时(秒)
     */
    @TableField()
    private Long elapsedSeconds;

    /**
     * 执行状态
     */
    @Length(max = 20, message = "{validation.scheduleJobLog.runStatus.Length.message}")
    @TableField()
    private String runStatus;

    /**
     * 数据执行条数
     */
    @TableField()
    private Integer dataCount;

    /**
     * 执行结果信息
     */
    @Length(max = 500, message = "{validation.scheduleJobLog.executeMsg.Length.message}")
    @TableField()
    private String executeMsg;
}
