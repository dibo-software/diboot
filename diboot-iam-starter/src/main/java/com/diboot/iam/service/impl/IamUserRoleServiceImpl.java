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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.exception.PermissionException;
import com.diboot.iam.mapper.IamUserRoleMapper;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.vo.IamRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* 用户角色关联相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamUserRoleServiceImpl extends BaseIamServiceImpl<IamUserRoleMapper, IamUserRole> implements IamUserRoleService {

    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamAccountService iamAccountService;

    // 扩展接口
    @Autowired(required = false)
    private IamExtensible iamExtensible;

    @Autowired(required = false)
    private IamCustomize iamCustomize;

    /**
     * 超级管理员的角色ID
     */
    private static Long ROLE_ID_SUPER_ADMIN = null;

    @Override
    public List<IamRole> getUserRoleList(String userType, Long userId) {
        return getUserRoleList(userType, userId, null);
    }

    @Override
    public List<IamRole> getUserRoleList(String userType, Long userId, Long extentionObjId) {
        List<IamUserRole> userRoleList = getEntityList(Wrappers.<IamUserRole>lambdaQuery()
                .select(IamUserRole::getRoleId)
                .eq(IamUserRole::getUserType, userType)
                .eq(IamUserRole::getUserId, userId)
        );
        if(V.isEmpty(userRoleList)){
            return Collections.emptyList();
        }
        List<Long> roleIds = BeanUtils.collectToList(userRoleList, IamUserRole::getRoleId);
        // 查询当前角色
        List<IamRole> roles = iamRoleService.getEntityList(Wrappers.<IamRole>lambdaQuery()
                .select(IamRole::getId, IamRole::getName, IamRole::getCode)
                .in(IamRole::getId, roleIds));
        // 加载扩展角色
        if(getIamExtensible() != null){
            List<IamRole> extRoles = getIamExtensible().getExtentionRoles(userType, userId, extentionObjId);
            if(V.notEmpty(extRoles)){
                roles.addAll(extRoles);
                roles = BeanUtils.distinctByKey(roles, IamRole::getId);
            }
        }
        return roles;
    }

    @Override
    public boolean createEntity(IamUserRole entity){
        Long superAdminRoleId = getSuperAdminRoleId();
        if(superAdminRoleId != null && superAdminRoleId.equals(entity.getRoleId())){
            checkSuperAdminIdentity();
        }
        boolean success = super.createEntity(entity);
        if(success){
            // 清空缓存
            clearUserAuthCache(entity.getUserType(), entity.getUserId());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean createEntities(Collection entityList) {
        if (V.isEmpty(entityList)) {
            return true;
        }

        Long superAdminRoleId = getSuperAdminRoleId();
        boolean hasSuperAdmin = false;
        String userType = null;
        Long userId = null;
        for(Object entity : entityList){
            IamUserRole iamUserRole = (IamUserRole)entity;
            if(superAdminRoleId != null && superAdminRoleId.equals(iamUserRole.getRoleId())){
                hasSuperAdmin = true;
            }
            if(userId == null){
                userType = iamUserRole.getUserType();
                userId = iamUserRole.getUserId();
            }
        }
        if(hasSuperAdmin){
            checkSuperAdminIdentity();
        }
        boolean success = super.createEntities(entityList);
        if(success){
            // 清空用户缓存
            clearUserAuthCache(userType, userId);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserRoleRelations(String userType, Long userId, List<Long> roleIds) {
        if(V.isEmpty(roleIds)){
            return true;
        }
        Long superAdminRoleId = getSuperAdminRoleId();
        // 给用户赋予了超级管理员，需确保当前用户为超级管理员权限
        if(superAdminRoleId != null && roleIds.contains(superAdminRoleId)){
            checkSuperAdminIdentity();
        }
        List<IamUserRole> entityList = new ArrayList<>();
        for(Long roleId : roleIds){
            entityList.add(new IamUserRole(userType, userId, roleId));
        }
        boolean success = super.createEntities(entityList);
        if(success){
            // 清空用户缓存
            clearUserAuthCache(userType, userId);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserRoleRelations(String userType, Long userId, List<Long> roleIds) {
        if (V.isEmpty(roleIds)){
            return true;
        }
        // 需要先获取旧的角色列表，来进行超级管理员权限判定
        List<IamUserRole> oldUserRoleList = this.getEntityList(
                Wrappers.<IamUserRole>lambdaQuery()
                        .eq(IamUserRole::getUserType, userType)
                        .eq(IamUserRole::getUserId, userId)
        );
        List oldRoleIds = new ArrayList();
        if (V.notEmpty(oldUserRoleList)){
            oldRoleIds = oldUserRoleList.stream()
                    .map(IamUserRole::getRoleId)
                    .collect(Collectors.toList());
        }

        Long superAdminRoleId = getSuperAdminRoleId();
        // 给用户赋予了超级管理员，需确保当前用户为超级管理员权限
        if(superAdminRoleId != null && (roleIds.contains(superAdminRoleId) || oldRoleIds.contains(superAdminRoleId))){
            checkSuperAdminIdentity();
        }

        // 删除旧的用户-角色关联关系
        this.deleteEntities(
                Wrappers.<IamUserRole>lambdaQuery()
                        .eq(IamUserRole::getUserId, userId)
                        .eq(IamUserRole::getUserType, userType)
        );
        List<IamUserRole> entityList = new ArrayList<>();
        for(Long roleId : roleIds){
            entityList.add(new IamUserRole(userType, userId, roleId));
        }
        boolean success = super.createEntities(entityList);
        if(success){
            // 清空用户缓存
            clearUserAuthCache(userType, userId);
        }
        return success;
    }

    @Override
    public List<IamRoleVO> getAllRoleVOList(BaseEntity userObject) {
        List<IamRole> roleList = getUserRoleList(userObject.getClass().getSimpleName(), userObject.getId());
        if (V.isEmpty(roleList)){
            return null;
        }
        return Binder.convertAndBindRelations(roleList, IamRoleVO.class);
    }

    /**
     * 获取Iam扩展实现
     * @return
     */
    @Override
    public IamExtensible getIamExtensible(){
        return iamExtensible;
    }

    /**
     * 获取超级管理员角色ID
     * @return
     */
    private Long getSuperAdminRoleId(){
        if(ROLE_ID_SUPER_ADMIN == null){
            LambdaQueryWrapper<IamRole> queryWrapper = new LambdaQueryWrapper<IamRole>()
                    .select(IamRole::getId)
                    .eq(IamRole::getCode, Cons.ROLE_SUPER_ADMIN);
            IamRole admin = iamRoleService.getSingleEntity(queryWrapper);
            if(admin != null){
                ROLE_ID_SUPER_ADMIN = admin.getId();
            }
        }
        return ROLE_ID_SUPER_ADMIN;
    }

    /**
     * 检查超级管理员身份
     */
    private void checkSuperAdminIdentity(){
        if(!iamCustomize.checkCurrentUserHasRole(Cons.ROLE_SUPER_ADMIN)){
            throw new PermissionException("非超级管理员用户不可授予其他用户超级管理员权限！");
        }
    }

    /**
     * 清空用户的认证缓存，以便权限变化及时生效
     * @param userType
     * @param userId
     */
    private void clearUserAuthCache(String userType, Long userId){
        String username = iamAccountService.getAuthAccount(userType, userId);
        if(V.notEmpty(username)){
            iamCustomize.clearAuthorizationCache(username);
        }
    }

}
