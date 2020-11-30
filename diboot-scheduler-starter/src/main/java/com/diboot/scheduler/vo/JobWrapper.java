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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.quartz.Job;

import java.io.Serializable;

/**
 * 定时任务Job定义wrapper
 * @author JerryMa
 * @version 2.2.0
 * @date 2020-11-30
 * Copyright © dibo.ltd
 */
public class JobWrapper implements Serializable {

    @Getter
    private String jobName;

    @Getter
    private String paramJsonExample;

    @Getter
    @JsonIgnore
    private Class<? extends Job> jobClass;

    public String getJobClassName(){
        return jobClass.getSimpleName();
    }

    public JobWrapper(String jobName, Class<? extends Job> jobClass, String paramJsonExample){
        this.jobName = jobName;
        this.jobClass = jobClass;
        this.paramJsonExample = paramJsonExample;
    }
}
