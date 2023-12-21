/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.annotation.process;

import com.diboot.iam.entity.IamUser;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * 销毁或设置{@link TenantContext}的数据
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Aspect
@Slf4j
public class BindTenantAspect {

    @Pointcut("@annotation(com.diboot.tenant.annotation.BindTenant)")
    public void pointCut() {
    }

    /**
     * 进入方法之前设置租户id
     *
     * @param proceedingJoinPoint
     */
    @Before(value = "pointCut()")
    public void beforeReturningHandler(JoinPoint proceedingJoinPoint) {
        IamUser currentUser = IamSecurityUtils.getCurrentUser();
        if (currentUser != null) {
            TenantHolder.setTenantId(String.valueOf(currentUser.getTenantId()));
        }
    }

    /**
     * 返回后清理数据
     *
     * @param proceedingJoinPoint
     * @param returnObj
     */
    @AfterReturning(value = "pointCut()", returning = "returnObj")
    public void afterReturningHandler(JoinPoint proceedingJoinPoint, Object returnObj) {
        TenantHolder.removeTenantId();
    }

    /**
     * 抛出异常清理数据
     *
     * @param joinPoint
     * @param throwable
     */
    @AfterThrowing(value = "pointCut()", throwing = "throwable")
    public void afterThrowingHandler(JoinPoint joinPoint, Throwable throwable) {
        TenantHolder.removeTenantId();
    }

}
