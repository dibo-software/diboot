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
