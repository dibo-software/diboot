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
import com.diboot.tenant.entity.IamTenantResource;

import java.util.List;

/**
 * 租户资源相关Service
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
public interface IamTenantResourceService extends BaseService<IamTenantResource> {

    /**
     * 过滤权限
     *
     * @param resourceIds 资源ID列表
     * @return 拥有资源权限ID列表
     */
    List<String> filterPermission(List<String> resourceIds);

    /**
     * 过滤权限
     *
     * @param tenantId    租户ID
     * @param resourceIds 资源ID列表
     * @return 拥有资源权限ID列表
     */
    List<String> filterPermission(String tenantId, List<String> resourceIds);

}
