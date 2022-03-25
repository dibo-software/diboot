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
package com.diboot.scheduler.job;

import com.diboot.core.util.D;
import com.diboot.core.util.SqlExecutor;
import com.diboot.scheduler.annotation.CollectThisJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 清除过期操作日志Job定义
 * @author JerryMa
 * @version 2.2.0
 * @date 2020-11-26
 * Copyright © dibo.ltd
 */
@Slf4j
@DisallowConcurrentExecution
@CollectThisJob(name = "清除过期操作日志", paramJson = "{\"daysBefore\":30}", cron = "0 0 1 * * ?")
public class ClearOperationLogJob extends QuartzJobBean {
    /**
     * 清理过期日志的SQL示例
     */
    private static final String SQL = "DELETE FROM iam_operation_log WHERE create_time <= ?";
    private static final String PARAM_KEY_DAYS_BEFORE = "daysBefore";

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 获取参数
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        int days = 30;
        if (jobDataMap.containsKey(PARAM_KEY_DAYS_BEFORE)) {
            days = jobDataMap.getInt(PARAM_KEY_DAYS_BEFORE);
        }
        List<Object> params = new ArrayList<>(1);
        params.add(D.getDate(new Date(), -days));
        try {
            SqlExecutor.executeUpdate(SQL, params);
        } catch (Exception e) {
            log.error("ClearOperationLogJob执行异常", e);
            throw new JobExecutionException(e);
        }
        log.info("ClearOperationLogJob成功清理{}天之前的操作日志", days);
    }

}