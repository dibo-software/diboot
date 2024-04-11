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
package com.diboot.iam.service;

import com.diboot.core.service.BaseService;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.entity.IamRoleResource;
import com.diboot.iam.entity.route.RouteRecord;
import com.diboot.iam.vo.IamResourceVO;
import com.diboot.iam.vo.ResourceRoleVO;

import java.util.List;

/**
 * 角色权限关联相关Service
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
public interface IamRoleResourceService extends BaseService<IamRoleResource> {


    /***
     * 获取用户的路由信息
     *
     * @return
     * @throws Exception
     */
    List<RouteRecord> getRouteRecords();

    /**
     * 获取指定角色对应的权限集（转换为树形结构VO）
     *
     * @param appModule
     * @param roleId
     * @return
     */
    List<IamResourceVO> getPermissionVOList(String appModule, String roleId);

    /**
     * 获取指定角色集合对应的权限VO集合（转换为树形结构VO）
     *
     * @param appModule
     * @param roleIds
     * @return
     */
    List<IamResourceVO> getPermissionVOList(String appModule, List<String> roleIds);

    /**
     * 获取指定角色集合对应的权限集
     *
     * @param appModule
     * @param roleIds
     * @return
     */
    List<IamResource> getPermissionList(String appModule, List<String> roleIds);

    /**
     * 获取指定角色集合对应的权限码集合
     *
     * @param appModule
     * @param roleIds
     * @return
     */
    List<String> getPermissionCodeList(String appModule, List<String> roleIds);

    /**
     * 获取资源角色VO集合
     *
     * @return
     */
    List<ResourceRoleVO> getAllResourceRoleVOList();

    /**
     * 批量创建角色与资源集的关系
     *
     * @param roleId
     * @param resourceIdList
     * @return
     */
    boolean createRoleResourceRelations(String roleId, List<String> resourceIdList);

    /***
     * 批量更新角色与资源集的关系
     * @param roleId
     * @param resourceIdList
     * @return
     */
    boolean updateRoleResourceRelations(String roleId, List<String> resourceIdList);

    /**
     * 删除角色下的资源
     * @param roleId
     * @return
     */
    boolean deleteRoleResourceRelations(String roleId);

    /**
     * 获取RoleService实例
     *
     * @return
     */
    IamRoleService getRoleService();

    /**
     * 获取PermissionService实例
     *
     * @return
     */
    IamResourceService getPermissionService();

}