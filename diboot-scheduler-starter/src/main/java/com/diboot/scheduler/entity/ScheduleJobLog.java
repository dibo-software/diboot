package com.diboot.scheduler.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 任务执行日志 Entity定义
 * @author yaojf
 * @version 2.2.0
 * @date 2020-11-26
 * Copyright © dibo.ltd
 */
@Getter
@Setter
@Accessors(chain = true)
public class ScheduleJobLog extends BaseEntity {
    private static final long serialVersionUID = -1854961913574904234L;
    /**
     * 字典定义 - 运行状态
     */
    public static final String DICT_RUN_STATUS = "RUN_STATUS";
    /**
     * 租户ID
     */
    @TableField
    private Long tenantId;

    // 任务id
    @NotNull(message = "任务id不能为空")
    @TableField()
    private Long jobId;

    // 任务名称
    @NotNull(message = "任务名称不能为空")
    @Length(max = 50, message = "任务名称长度应小于50")
    @TableField()
    private String jobName;

    // 执行表达式
    @NotNull(message = "执行表达式不能为空")
    @Length(max = 100, message = "执行表达式长度应小于100")
    @TableField()
    private String cron;

    /**
     * 参数json字符串
     */
    @TableField()
    private String paramJson;

    // 开始时间
    @TableField()
    private Date startTime;

    // 结束时间
    @TableField()
    private Date endTime;

    // 耗时(秒)
    @TableField()
    private Long elapsedSeconds;

    // 状态
    @Length(max = 100, message = "状态长度应小于100")
    @TableField()
    private String jobStatus;

    // 数据执行条数
    @TableField()
    private Integer dataCount;

    // 执行结果信息
    @Length(max = 500, message = "执行结果信息长度应小于500")
    @TableField()
    private String executeMsg;

    // 创建人
    @TableField()
    private Long createBy;

    // 更新时间
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Date updateTime;

}