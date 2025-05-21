/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.example.api.job;

import com.diboot.core.util.ContextHolder;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamUserService;
import com.diboot.scheduler.annotation.CollectThisJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * CAS用户信息同步
 * @author MyName
 * @version 1.0
 * @date 2024-02-28
 * Copyright © MyCompany
 */
@Slf4j
@DisallowConcurrentExecution
@CollectThisJob(name = "CAS Server用户信息同步", paramJson = "{}", cron = "0 0 1 * * ?")
public class CasUserSyncJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 仅作为示例，添加默认用户信息，实际场景需要根据业务需要进行处理
        // 添加IamUser
        IamUser iamUser = new IamUser();
        iamUser.setUserNum("100011")
                .setRealname("casuser")
                .setGender("M")
                .setOrgId("0")
                .setStatus("A");
        ContextHolder.getBean(IamUserService.class).createEntity(iamUser);
        // 添加IamAccount
        IamAccount iamAccount = new IamAccount();
        iamAccount.setUserType(IamUser.class.getSimpleName())
                .setUserId(iamUser.getId())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.CAS_SERVER.name())
                .setAuthAccount("casuser")
                .setStatus("A");
        ContextHolder.getBean(IamAccountService.class).createEntity(iamAccount);
    }

}