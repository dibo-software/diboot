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
package com.diboot.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.iam.auth.IamTenantPermission;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamResourceService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.tenant.entity.IamTenantResource;
import com.diboot.tenant.mapper.IamTenantResourceMapper;
import com.diboot.tenant.service.IamTenantResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


/**
 * 租户资源相关Service实现
 *
 * @author uu
 * @version 2.5
 * @date 2022-02-24
 */
@Service
@Slf4j
public class IamTenantResourceServiceImpl extends BaseServiceImpl<IamTenantResourceMapper, IamTenantResource> implements IamTenantResourceService, IamTenantPermission {

    @Autowired
    private IamResourceService iamResourceService;

    @Override
    public List<String> filterPermission(List<String> resourceIds) {
        IamUser currentUser = IamSecurityUtils.getCurrentUser();
        // 未登录无权限
        if (currentUser == null) {
            return Collections.emptyList();
        } else {
            return filterPermission(currentUser.getTenantId(), resourceIds);
        }
    }

    @Override
    public List<String> filterPermission(String tenantId, List<String> resourceIds) {
        // 平台端无需权限过滤
        if (V.equals(tenantId, "0")) {
            return resourceIds;
        }
        LambdaQueryWrapper<IamTenantResource> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(IamTenantResource::getTenantId, tenantId).in(IamTenantResource::getResourceId, resourceIds);
        return getValuesOfField(queryWrapper, IamTenantResource::getResourceId);
    }

    @Override
    public List<String> findAllPermissions(String tenantId) {
        LambdaQueryWrapper<IamTenantResource> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(IamTenantResource::getTenantId, tenantId);
        return getValuesOfField(queryWrapper, IamTenantResource::getResourceId);
    }

    @Override
    public List<String> findAllPermissionCodes(String tenantId) {
        List<String> permissionIds = findAllPermissions(tenantId);
        if (V.isEmpty(permissionIds)) {
            return Collections.emptyList();
        }
        // 查询权限
        LambdaQueryWrapper<IamResource> queryWrapper = Wrappers.<IamResource>lambdaQuery()
                .select(IamResource::getPermissionCode)
                .in(IamResource::getId, permissionIds)
                .isNotNull(IamResource::getPermissionCode);
        // 仅查询PermissionCode字段
        return iamResourceService.getValuesOfField(
                queryWrapper, IamResource::getPermissionCode
        );
    }
}
