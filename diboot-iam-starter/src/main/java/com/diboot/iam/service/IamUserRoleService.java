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
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamRoleVO;

import java.util.List;

/**
 * 用户角色关联相关Service
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-17
 */
public interface IamUserRoleService extends BaseService<IamUserRole> {

    /**
     * 获取用户所有的全部角色
     *
     * @param userType
     * @param userId
     * @return
     */
    default List<IamRole> getUserRoleList(String userType, String userId) {
        return getUserRoleList(IamSecurityUtils.getCurrentTenantId(), userType, userId);
    }

    /**
     * 获取用户所有的全部角色
     *
     * @param tenantId
     * @param userType
     * @param userId
     * @return
     */
    default List<IamRole> getUserRoleList(String tenantId, String userType, String userId) {
        return getUserRoleList(tenantId, userType, userId, null);
    }

    /**
     * 获取用户所有的全部角色
     *
     * @param tenantId
     * @param userType
     * @param userId
     * @param extensionObjId 岗位等扩展对象id
     * @return
     */
    List<IamRole> getUserRoleList(String tenantId, String userType, String userId, String extensionObjId);

    /**
     * 批量创建用户-角色的关系
     *
     * @param userType
     * @param userId
     * @param roleIds
     * @return
     */
    boolean createUserRoleRelations(String userType, String userId, List<String> roleIds);

    /***
     * 批量更新用户-角色的关系
     * @param userType
     * @param userId
     * @param roleIds
     * @return
     */
    boolean updateUserRoleRelations(String userType, String userId, List<String> roleIds);

    /**
     * 批量删除用户-角色的关系
     *
     * @param userType
     * @param userId
     * @return
     */
    boolean deleteUserRoleRelations(String userType, String userId);

    /***
     * 获取用户的所有角色列表（包括扩展的关联角色）
     * @param userObject
     * @return
     */
    List<IamRoleVO> getAllRoleVOList(BaseLoginUser userObject);

    /**
     * 获取Iam扩展实现
     *
     * @return
     */
    IamExtensible getIamExtensible();

    /**
     * 构建role-permission角色权限数据格式(合并role等)，用于前端适配
     *
     * @param loginUser 登录用户
     * @return 角色VO
     */
    IamRoleVO buildRoleVo4FrontEnd(BaseLoginUser loginUser);

    /**
     * 根据角色获取用户ids
     *
     * @param roleIds
     * @return
     */
    List<String> getUserIdsByRoleIds(List<String> roleIds);
}
