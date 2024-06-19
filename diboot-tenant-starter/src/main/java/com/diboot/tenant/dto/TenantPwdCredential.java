/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.dto;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.PwdCredential;
import com.diboot.tenant.config.TenantProperties;
import com.diboot.tenant.entity.IamTenant;
import com.diboot.tenant.service.IamTenantService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 登录凭证
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */

/**
 * 租户登录凭证
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class TenantPwdCredential extends PwdCredential {
    private static final long serialVersionUID = -5020652642432896556L;

    public TenantPwdCredential() {
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    public TenantPwdCredential(String username, String password, String tenantCode) {
        super(username, password);
        this.tenantCode = tenantCode;
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    @NotNull(message = "{validation.tenantPwdCredential.tenantCode.NotNull.message}")
    private String tenantCode;

    @Override
    public String getTenantId() {
        if (V.notEmpty(super.getTenantId())) return super.getTenantId();
        if (V.equals(tenantCode, Objects.requireNonNull(ContextHolder.getBean(TenantProperties.class)).getPlatformCode())) {
            setTenantId(Cons.ID_PREVENT_NULL);
            return super.getTenantId();
        }
        // 获取租户id
        LambdaQueryWrapper<IamTenant> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(IamTenant::getCode, tenantCode);
        IamTenant tenant = Objects.requireNonNull(ContextHolder.getBean(IamTenantService.class)).getSingleEntity(queryWrapper);
        if (tenant == null) {
            throw new BusinessException("exception.business.tenantPwdCredential.tenant.nonexistent");
        }
        if (!Cons.DICTCODE_ACCOUNT_STATUS.A.name().equals(tenant.getStatus())) {
            throw new BusinessException("exception.business.tenantPwdCredential.tenant.unusable");
        }
        LocalDate startDate = tenant.getStartDate();
        LocalDate endDate = tenant.getEndDate();
        if (V.isEmpty(startDate) || V.isEmpty(endDate)) {
            throw new BusinessException("exception.business.tenantPwdCredential.tenant.configFailed");
        }
        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate)) {
            throw new BusinessException("exception.business.tenantPwdCredential.tenant.invalid");
        }
        if (now.isAfter(endDate)) {
            throw new BusinessException("exception.business.tenantPwdCredential.tenant.expired");
        }
        setTenantId(tenant.getId());
        return super.getTenantId();
    }

    @Override
    public String getAuthAccount() {
        return this.getUsername();
    }

    @Override
    public String getAuthSecret() {
        return this.getPassword();
    }


}
