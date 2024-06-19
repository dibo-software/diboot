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
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.event.OperationEvent;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.IamTenantPermission;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.*;
import com.diboot.iam.entity.route.RouteMeta;
import com.diboot.iam.entity.route.RouteRecord;
import com.diboot.iam.mapper.IamRoleResourceMapper;
import com.diboot.iam.service.IamResourceService;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamResourceListVO;
import com.diboot.iam.vo.IamResourceVO;
import com.diboot.iam.vo.PositionDataScope;
import com.diboot.iam.vo.ResourceRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色权限关联相关Service实现
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
@Service
@Slf4j
public class IamRoleResourceServiceImpl extends BaseServiceImpl<IamRoleResourceMapper, IamRoleResource> implements IamRoleResourceService {

    @Autowired
    private IamRoleService iamRoleService;

    @Autowired
    private IamResourceService iamResourceService;

    @Autowired
    private IamUserRoleService iamUserRoleService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public List<RouteRecord> getRouteRecords() {
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(Status.FAIL_NO_PERMISSION, "exception.business.roleResourceService.fetchMenuAfterLogin");
        }
        // 获取当前用户的角色
        LabelValue extensionObj = currentUser.getExtensionObj();
        // 根据用户类型与用户id获取roleList
        String extensionObjId = null;
        if (extensionObj != null) {
            extensionObjId = ((PositionDataScope) extensionObj.getExt()).getPositionId();
        }
        // 获取当前用户的角色列表
        List<IamRole> roleList = iamUserRoleService.getUserRoleList(currentUser.getTenantId(), currentUser.getClass().getSimpleName(), currentUser.getId(), extensionObjId);
        if (V.isEmpty(roleList)) {
            return Collections.emptyList();
        }
        boolean isAdmin = false;
        boolean isTenantAdmin = false;
        for (IamRole iamRole : roleList) {
            if (Cons.ROLE_SUPER_ADMIN.equalsIgnoreCase(iamRole.getCode())) {
                isAdmin = true;
                break;
            }
            if (Cons.ROLE_TENANT_ADMIN.equalsIgnoreCase(iamRole.getCode())) {
                isTenantAdmin = true;
                break;
            }
        }
        List<String> permissionIds = null;
        // 如果是租户管理员，直接查询租户表
        if (isTenantAdmin) {
            IamTenantPermission iamTenantPermission = ContextHolder.getBean(IamTenantPermission.class);
            if (V.isEmpty(iamTenantPermission)) {
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.roleResourceService.tenantNonConfigPermission");
            }
            permissionIds = iamTenantPermission.findAllPermissions(currentUser.getTenantId());
        }
        LambdaQueryWrapper<IamResource> wrapper = Wrappers.<IamResource>lambdaQuery()
                .eq(IamResource::getStatus, Cons.DICTCODE_RESOURCE_STATUS.A.name());
        if (!isAdmin && !isTenantAdmin) {
            List<String> roleIds = roleList.stream().map(IamRole::getId).collect(Collectors.toList());
            // 获取角色对应的菜单权限
            permissionIds = getPermissionIdsByRoleIds(roleIds);
        }
        if (!isAdmin && V.isEmpty(permissionIds)) {
            return Collections.emptyList();
        }
        wrapper.in(V.notEmpty(permissionIds), IamResource::getId, permissionIds);
        List<IamResource> menuPermissionList = iamResourceService.getEntityList(wrapper);
        if (V.isEmpty(menuPermissionList)) {
            return Collections.emptyList();
        }
        RelationsBinder.bind(menuPermissionList);
        // 绑定菜单下按钮权限，进行菜单权限分组
        Map<String, List<IamResource>> listMap = menuPermissionList.stream().collect(Collectors.groupingBy(e ->
                Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name().equals(e.getDisplayType()) ? e.getParentId() : Cons.TREE_ROOT_ID));
        List<IamResourceListVO> iamResourceList = BeanUtils.convertList(listMap.get(Cons.TREE_ROOT_ID), IamResourceListVO.class);
        iamResourceList.forEach(e-> e.setPermissionList(listMap.get(e.getId())));
        // 构建路由菜单
        List<RouteRecord> routeRecordList = new ArrayList<>();
        buildRouteRecordList(routeRecordList,  BeanUtils.buildTree(iamResourceList));
        return routeRecordList;
    }

    @Override
    public List<IamResourceVO> getPermissionVOList(String appModule, String roleId) {
        List<String> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        return getPermissionVOList(appModule, roleIdList);
    }

    @Override
    public List<IamResourceVO> getPermissionVOList(String appModule, List<String> roleIds) {
        List<IamResource> list = getPermissionList(appModule, roleIds);
        List<IamResourceVO> voList = BeanUtils.convertList(list, IamResourceVO.class);
        return BeanUtils.buildTree(voList, Cons.TREE_ROOT_ID);
    }

    @Override
    public List<IamResource> getPermissionList(String appModule, List<String> roleIds) {
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<String> permissionIds = getPermissionIdsByRoleIds(roleIds);
        if (V.isEmpty(permissionIds)) {
            return Collections.emptyList();
        }
        return iamResourceService.getEntityList(Wrappers.<IamResource>lambdaQuery()
                .in(IamResource::getId, permissionIds));
    }

    @Override
    public List<String> getPermissionCodeList(String appModule, List<String> roleIds) {
        List<String> permissionIds = getPermissionIdsByRoleIds(roleIds);
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

    @Override
    public List<ResourceRoleVO> getAllResourceRoleVOList() {
        LambdaQueryWrapper<IamResource> wrapper = Wrappers.<IamResource>lambdaQuery()
                .isNotNull(IamResource::getPermissionCode);
        List<IamResource> list = iamResourceService.getEntityList(wrapper);
        if (list == null) {
            list = Collections.emptyList();
        }
        // 绑定关联roles
        return Binder.convertAndBindRelations(list, ResourceRoleVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRoleResourceRelations(String roleId, List<String> resourceIdList) {
        if (V.isEmpty(resourceIdList)) {
            return true;
        }
        // 批量创建
        List<IamRoleResource> roleResourceList = new ArrayList<>();
        for (String resourceId : resourceIdList) {
            roleResourceList.add(new IamRoleResource(roleId, resourceId));
        }
        boolean success = createEntities(roleResourceList);
        // 对外发布角色资源变更事件
        applicationEventPublisher.publishEvent(new OperationEvent(IamRoleResource.class.getSimpleName(), roleId));
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleResourceRelations(String roleId, List<String> resourceIdList) {
        if (resourceIdList == null) {
            return true;
        }
        // 删除新列表中不存在的关联记录
        this.deleteEntities(
                Wrappers.<IamRoleResource>lambdaQuery()
                        .eq(IamRoleResource::getRoleId, roleId)
        );
        // 批量新增
        if (resourceIdList.isEmpty()) {
            return true;
        }
        List<IamRoleResource> roleResourceList = new ArrayList<>();
        for (String resourceId : resourceIdList) {
            roleResourceList.add(new IamRoleResource(roleId, resourceId));
        }
        boolean success = createEntities(roleResourceList);
        // 对外发布角色资源变更事件
        applicationEventPublisher.publishEvent(new OperationEvent(IamRoleResource.class.getSimpleName(), roleId));
        return success;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteRoleResourceRelations(String roleId) {
        if (roleId == null) {
            return false;
        }
        boolean success = deleteEntities(IamRoleResource::getRoleId, roleId);
        // 对外发布角色资源变更事件
        applicationEventPublisher.publishEvent(new OperationEvent(IamRoleResource.class.getSimpleName(), roleId));
        return success;
    }

    @Override
    public IamRoleService getRoleService() {
        return iamRoleService;
    }

    @Override
    public IamResourceService getPermissionService() {
        return iamResourceService;
    }

    /**
     * 获取角色关联的权限id集合
     *
     * @param roleIds
     * @return
     */
    private List<String> getPermissionIdsByRoleIds(List<String> roleIds) {
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return getValuesOfField(Wrappers.<IamRoleResource>lambdaQuery().in(IamRoleResource::getRoleId, roleIds),
                IamRoleResource::getResourceId);
    }

    /**
     * 构建前端路由信息
     *
     * @param routeRecordList
     * @param iamResourceListVOList
     */
    private void buildRouteRecordList(List<RouteRecord> routeRecordList, List<IamResourceListVO> iamResourceListVOList) {
        RouteRecord routeRecord;
        for (IamResourceListVO resource : iamResourceListVOList) {
            routeRecord = new RouteRecord();
            RouteMeta routeMeta = resource.getRouteMeta();
            routeMeta.setTitle(resource.getDisplayName()).setSort(resource.getSortId());
            // 设置当前路由的按钮权限
            if (V.notEmpty(resource.getPermissionList())) {
                routeMeta.setPermissions(resource.getPermissionList()
                        .stream()
                        .map(IamResource::getResourceCode)
                        .collect(Collectors.toList()));
            }
            routeRecord.setName(resource.getResourceCode())
                    .setPath(resource.getRoutePath())
                    .setRedirect(routeMeta.getRedirectPath())
                    .setMeta(routeMeta);
            if (V.notEmpty(resource.getChildren())) {
                List<RouteRecord> routeRecordChildren = new ArrayList<>();
                buildRouteRecordList(routeRecordChildren, resource.getChildren());
                routeRecord.setChildren(routeRecordChildren);
            }
            routeRecordList.add(routeRecord);
        }
    }
}
