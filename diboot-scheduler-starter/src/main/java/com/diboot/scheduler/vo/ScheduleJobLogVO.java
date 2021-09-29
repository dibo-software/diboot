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

import com.diboot.core.config.Cons;
import com.diboot.scheduler.entity.ScheduleJobLog;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 定时任务日志VO定义
 *
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
    public String getRunStatusLabel() {
        return Cons.RESULT_STATUS.getLabel(this.getRunStatus());
    }

}
