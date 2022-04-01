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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 同步任务 Entity定义
 *
 * @author yaojf
 * @version 2.2.0
 * @date 2020-11-26
 * Copyright © dibo.ltd
 */
@Getter
@Setter
@Accessors(chain = true)
public class ScheduleJob extends BaseEntity {
    private static final long serialVersionUID = 2238760903350953170L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private Long tenantId;

    /**
     * job key
     */
    @NotNull(message = "任务不能为空")
    @TableField()
    private String jobKey;

    /**
     * job名称
     */
    @NotNull(message = "名称不能为空")
    @Length(max = 50, message = "名称长度应小于50")
    @TableField()
    @BindQuery(comparison = Comparison.LIKE)
    private String jobName;

    /**
     * 定时执行表达式
     */
    @NotNull(message = "定时表达式不能为空")
    @Length(max = 100, message = "定时表达式长度应小于100")
    @TableField()
    private String cron;

    /**
     * 参数json字符串
     */
    @TableField()
    private String paramJson;

    /**
     * 初始策略
     */
    @TableField()
    private String initStrategy;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    @Length(max = 10, message = "状态长度应小于10")
    @TableField()
    private String jobStatus;

    /**
     * 是否保存日志，默认true
     */
    @TableField()
    private Boolean saveLog;

    /**
     * 备注
     */
    @Length(max = 200, message = "备注长度应小于200")
    @TableField()
    private String jobComment;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
