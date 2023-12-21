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
package com.diboot.tenant.service;


import com.diboot.core.service.BaseService;
import com.diboot.iam.dto.IamUserFormDTO;
import com.diboot.tenant.entity.IamTenant;
import com.diboot.tenant.vo.TenantAdminUserVO;

import java.util.List;

/**
 * 租户相关Service
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
public interface IamTenantService extends BaseService<IamTenant> {

    /**
     * 创建租户
     * <p>创建租户</p>
     * <p>创建默认公司</p>
     * <p>创建租户管理员角色</p>
     *
     * @param tenant
     * @return
     * @throws Exception
     */
    boolean createIamTenantAndTenantOrgAndTenantAdminRole(IamTenant tenant) throws Exception;

    /**
     * 获取租户管理员
     *
     * @param tenantId
     * @return
     * @throws Exception
     */
    TenantAdminUserVO getTenantAdminUserVO(String tenantId) throws Exception;

    /**
     * 创建or更新租户管理员
     *
     * @param iamUserFormDTO
     * @return
     * @throws Exception
     */
    boolean createOrUpdateTenantAdminUser(IamUserFormDTO iamUserFormDTO) throws Exception;

    /**
     * 获取租户的资源ID列表
     *
     * @param tenantId 租户ID
     * @return 资源ID列表
     */
    List<String> getResourceIds(String tenantId);
}
