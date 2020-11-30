package com.diboot.scheduler.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.config.Cons;
import com.diboot.iam.entity.IamUser;
import com.diboot.scheduler.entity.ScheduleJobLog;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 定时任务日志VO定义
 * @author JerryMa
 * @version 2.2.0
 * @date 2020-11-26
 * Copyright © dibo.ltd
 */
@Getter
@Setter
@Accessors(chain = true)
public class ScheduleJobLogVO extends ScheduleJobLog {
    private static final long serialVersionUID = 4753135850894402717L;
    /**
     * 状态
     */
    public String getRunStatusLabel(){
        return Cons.RESULT_STATUS.getLabel(this.getRunStatus());
    }

    /**
     * 创建人姓名
     */
    @BindField(entity = IamUser.class, field = "realname", condition = "this.create_by=id")
    private String createByName;

}