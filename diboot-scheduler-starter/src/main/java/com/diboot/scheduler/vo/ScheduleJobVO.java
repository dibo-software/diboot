package com.diboot.scheduler.vo;

import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.config.Cons;
import com.diboot.iam.entity.IamUser;
import com.diboot.scheduler.entity.ScheduleJob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

 /**
 * 定时任务VO定义
 * @author JerryMa
 * @version 2.2.0
 * @date 2020-11-26
 * Copyright © dibo.ltd
 */
@Getter
@Setter
@Accessors(chain = true)
public class ScheduleJobVO extends ScheduleJob {
    private static final long serialVersionUID = 1577319715591105599L;

     /**
      * 状态
       */
     public String getJobStatusLabel(){
        return Cons.ENABLE_STATUS.getLabel(this.getJobStatus());
     }

    /**
     * 创建人姓名
     */
    @BindField(entity = IamUser.class, field = "realname", condition = "this.create_by=id")
    private String createByName;

}