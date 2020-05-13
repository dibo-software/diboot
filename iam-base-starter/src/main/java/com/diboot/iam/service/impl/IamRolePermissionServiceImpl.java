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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamFrontendPermission;
import com.diboot.iam.entity.IamRolePermission;
import com.diboot.iam.mapper.IamRolePermissionMapper;
import com.diboot.iam.service.IamFrontendPermissionService;
import com.diboot.iam.service.IamRolePermissionService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.util.BeanUtils;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamFrontendPermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* 角色权限关联相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Service
@Slf4j
public class IamRolePermissionServiceImpl extends BaseIamServiceImpl<IamRolePermissionMapper, IamRolePermission> implements IamRolePermissionService {

    @Autowired
    private IamRoleService iamRoleService;

    @Autowired
    private IamFrontendPermissionService iamFrontendPermissionService;

    @Override
    public List<IamFrontendPermissionVO> getPermissionVOList(String application, Long roleId) {
        List<Long> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        return getPermissionVOList(application, roleIdList);
    }

    @Override
    public List<IamFrontendPermissionVO> getPermissionVOList(String application, List<Long> roleIds) {
        List<IamFrontendPermission> list = getPermissionList(application, roleIds);
        List<IamFrontendPermissionVO> voList = BeanUtils.convertList(list, IamFrontendPermissionVO.class);
        return BeanUtils.buildTree(voList);
    }

    @Override
    public List<IamFrontendPermission> getPermissionList(String application, List<Long> roleIds) {
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = getPermissionIdsByRoleIds(application, roleIds);
        if(V.isEmpty(permissionIds)){
            return Collections.emptyList();
        }
        List<IamFrontendPermission> list = iamFrontendPermissionService.getEntityList(Wrappers.<IamFrontendPermission>lambdaQuery()
                .in(IamFrontendPermission::getId, permissionIds));
        if(list == null){
            list = Collections.emptyList();
        }
        return list;
    }

    @Override
    public List<String> getApiUrlList(String application, List<Long> roleIds) {
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = getPermissionIdsByRoleIds(application, roleIds);
        if (V.isEmpty(permissionIds)) {
            return Collections.emptyList();
        }
        // 查询权限
        List<IamFrontendPermission> frontendPermissions = iamFrontendPermissionService.getEntityList(
            Wrappers.<IamFrontendPermission>lambdaQuery()
            .select(IamFrontendPermission::getApiSet)
            .in(IamFrontendPermission::getId, permissionIds)
        );
        if(frontendPermissions == null){
            return Collections.emptyList();
        }
        // 转换为string list
        List<String> list = BeanUtils.collectToList(frontendPermissions, IamFrontendPermission::getApiSet);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRolePermissionRelations(Long roleId, List<Long> permissionIdList) {
        // 批量创建
        List<IamRolePermission> rolePermissionList = new ArrayList<>();
        for(Long permissionId : permissionIdList){
            IamRolePermission rolePermission = new IamRolePermission(roleId, permissionId);
            rolePermissionList.add(rolePermission);
        }
        boolean success = createEntities(rolePermissionList);
        IamSecurityUtils.clearAllAuthorizationCache();
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRolePermissionRelations(Long roleId, List<Long> permissionIdList) {
        // 删除新列表中不存在的关联记录
        this.deleteEntities(
                Wrappers.<IamRolePermission>lambdaQuery()
                        .eq(IamRolePermission::getRoleId, roleId)
        );
        // 批量新增
        List<IamRolePermission> rolePermissionList = new ArrayList<>();
        for(Long permissionId : permissionIdList){
            IamRolePermission rolePermission = new IamRolePermission(roleId, permissionId);
            rolePermissionList.add(rolePermission);
        }
        boolean success = createEntities(rolePermissionList);
        IamSecurityUtils.clearAllAuthorizationCache();
        return success;
    }


    @Override
    public IamRoleService getRoleService() {
        return iamRoleService;
    }

    @Override
    public IamFrontendPermissionService getPermissionService() {
        return iamFrontendPermissionService;
    }

    /**
     * 获取角色关联的权限id集合
     * @param roleIds
     * @return
     */
    private List<Long> getPermissionIdsByRoleIds(String application, List<Long> roleIds){
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<IamRolePermission> permissions = getEntityList(Wrappers.<IamRolePermission>lambdaQuery()
                .select(IamRolePermission::getPermissionId)
                .in(IamRolePermission::getRoleId, roleIds));
        if(V.isEmpty(permissions)){
            return Collections.emptyList();
        }
        List<Long> permissionIds = BeanUtils.collectToList(permissions, IamRolePermission::getPermissionId);
        return permissionIds;
    }
}
