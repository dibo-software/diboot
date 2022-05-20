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
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamRoleResource;
import com.diboot.iam.entity.route.RouteMeta;
import com.diboot.iam.entity.route.RouteRecord;
import com.diboot.iam.mapper.IamRoleResourceMapper;
import com.diboot.iam.service.IamResourcePermissionService;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import com.diboot.iam.vo.IamResourcePermissionVO;
import com.diboot.iam.vo.PositionDataScope;
import com.diboot.iam.vo.ResourceRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class IamRoleResourceServiceImpl extends BaseIamServiceImpl<IamRoleResourceMapper, IamRoleResource> implements IamRoleResourceService {

    @Autowired
    private IamRoleService iamRoleService;

    @Autowired
    private IamResourcePermissionService iamResourcePermissionService;

    @Autowired
    private IamUserRoleService iamUserRoleService;

    @Override
    public List<RouteRecord> getRouteRecords(String appModule) throws Exception {
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(Status.FAIL_NO_PERMISSION, "请登录后获取菜单授权！");
        }
        // 获取当前用户的角色
        LabelValue extensionObj = currentUser.getExtensionObj();
        // 根据用户类型与用户id获取roleList
        Long extensionObjId = null;
        if (extensionObj != null) {
            extensionObjId = ((PositionDataScope) extensionObj.getValue()).getPositionId();
        }
        // 获取当前用户的角色列表
        List<IamRole> roleList = iamUserRoleService.getUserRoleList(currentUser.getClass().getSimpleName(), currentUser.getId(), extensionObjId);
        if (V.isEmpty(roleList)) {
            return Collections.emptyList();
        }
        boolean isAdmin = false;
        for (IamRole iamRole : roleList) {
            if (Cons.ROLE_SUPER_ADMIN.equalsIgnoreCase(iamRole.getCode())) {
                isAdmin = true;
                break;
            }
        }
        LambdaQueryWrapper<IamResourcePermission> wrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                .ne(IamResourcePermission::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name())
                .eq(IamResourcePermission::getStatus, Cons.DICTCODE_RESOURCE_STATUS.A.name());
        if (!isAdmin) {
            List<Long> roleIds = roleList.stream().map(IamRole::getId).collect(Collectors.toList());
            // 获取角色对应的菜单权限
            List<Long> permissionIds = getPermissionIdsByRoleIds(appModule, roleIds);
            if (V.isEmpty(permissionIds)) {
                return Collections.emptyList();
            }
            wrapper.in(IamResourcePermission::getId, permissionIds);

        }
        List<IamResourcePermission> menuPermissionList = iamResourcePermissionService.getEntityList(wrapper);
        if (V.isEmpty(menuPermissionList)) {
            return Collections.emptyList();
        }
        // 绑定菜单下按钮权限
        List<IamResourcePermissionListVO> iamResourcePermissionListVOS = RelationsBinder.convertAndBind(menuPermissionList, IamResourcePermissionListVO.class);
        iamResourcePermissionListVOS = BeanUtils.buildTree(iamResourcePermissionListVOS);
        // 构建路由菜单
        List<RouteRecord> routeRecordList = new ArrayList<>();
        buildRouteRecordList(routeRecordList, iamResourcePermissionListVOS);
        return routeRecordList;
    }

    @Override
    public List<IamResourcePermissionVO> getPermissionVOList(String appModule, Long roleId) {
        List<Long> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        return getPermissionVOList(appModule, roleIdList);
    }

    @Override
    public List<IamResourcePermissionVO> getPermissionVOList(String appModule, List<Long> roleIds) {
        List<IamResourcePermission> list = getPermissionList(appModule, roleIds);
        List<IamResourcePermissionVO> voList = BeanUtils.convertList(list, IamResourcePermissionVO.class);
        return BeanUtils.buildTree(voList);
    }

    @Override
    public List<IamResourcePermission> getPermissionList(String appModule, List<Long> roleIds) {
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = getPermissionIdsByRoleIds(appModule, roleIds);
        if (V.isEmpty(permissionIds)) {
            return Collections.emptyList();
        }
        List<IamResourcePermission> list = iamResourcePermissionService.getEntityList(Wrappers.<IamResourcePermission>lambdaQuery()
                .in(IamResourcePermission::getId, permissionIds));
        if (list == null) {
            list = Collections.emptyList();
        }
        return list;
    }

    @Override
    public List<String> getPermissionCodeList(String appModule, List<Long> roleIds) {
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = getPermissionIdsByRoleIds(appModule, roleIds);
        if (V.isEmpty(permissionIds)) {
            return Collections.emptyList();
        }
        // 查询权限
        LambdaQueryWrapper<IamResourcePermission> queryWrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                .select(IamResourcePermission::getPermissionCode)
                .in(IamResourcePermission::getId, permissionIds)
                .isNotNull(IamResourcePermission::getPermissionCode);
        // 仅查询PermissionCode字段
        List<String> resourcePermissions = iamResourcePermissionService.getValuesOfField(
                queryWrapper, IamResourcePermission::getPermissionCode
        );
        return resourcePermissions;
    }

    @Override
    public List<ResourceRoleVO> getAllResourceRoleVOList() {
        LambdaQueryWrapper<IamResourcePermission> wrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                .isNotNull(IamResourcePermission::getPermissionCode);
        List<IamResourcePermission> list = iamResourcePermissionService.getEntityList(wrapper);
        if (list == null) {
            list = Collections.emptyList();
        }
        // 绑定关联roles
        return Binder.convertAndBindRelations(list, ResourceRoleVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRoleResourceRelations(Long roleId, List<Long> resourceIdList) {
        if (V.isEmpty(resourceIdList)) {
            return true;
        }
        // 批量创建
        List<IamRoleResource> roleResourceList = new ArrayList<>();
        for (Long resourceId : resourceIdList) {
            roleResourceList.add(new IamRoleResource(roleId, resourceId));
        }
        boolean success = createEntities(roleResourceList);
        IamSecurityUtils.clearAllAuthorizationCache();
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleResourceRelations(Long roleId, List<Long> resourceIdList) {
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
        for (Long resourceId : resourceIdList) {
            roleResourceList.add(new IamRoleResource(roleId, resourceId));
        }
        boolean success = createEntities(roleResourceList);
        IamSecurityUtils.clearAllAuthorizationCache();
        return success;
    }

    @Override
    public IamRoleService getRoleService() {
        return iamRoleService;
    }

    @Override
    public IamResourcePermissionService getPermissionService() {
        return iamResourcePermissionService;
    }

    /**
     * 获取角色关联的权限id集合
     *
     * @param roleIds
     * @return
     */
    private List<Long> getPermissionIdsByRoleIds(String appModule, List<Long> roleIds) {
        if (V.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<IamRoleResource> permissions = getEntityList(Wrappers.<IamRoleResource>lambdaQuery()
                .select(IamRoleResource::getResourceId)
                .in(IamRoleResource::getRoleId, roleIds));
        if (V.isEmpty(permissions)) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = BeanUtils.collectToList(permissions, IamRoleResource::getResourceId);
        return permissionIds;
    }

    /**
     * 构建前端路由信息
     *
     * @param routeRecordList
     * @param iamResourcePermissionListVOList
     */
    private void buildRouteRecordList(List<RouteRecord> routeRecordList, List<IamResourcePermissionListVO> iamResourcePermissionListVOList) {
        RouteRecord routeRecord = null;
        for (IamResourcePermissionListVO resource : iamResourcePermissionListVOList) {
            routeRecord = new RouteRecord();
            RouteMeta routeMeta = resource.getRouteMeta();
            routeMeta.setTitle(resource.getDisplayName()).setSort(resource.getSortId());
            // 设置当前路由的按钮权限
            if (V.notEmpty(resource.getPermissionList())) {
                routeMeta.setPermissions(resource.getPermissionList()
                        .stream()
                        .map(IamResourcePermission::getResourceCode)
                        .collect(Collectors.toList()));
            }
            // 非菜单的情况下，可能是外链或iframe
            if (V.notEquals(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name(), resource.getDisplayType())) {
                routeMeta.setUrl(resource.getRoutePath());
                resource.setRoutePath(null);
            }
            // 设置类型
            if (V.equals(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.IFRAME.name(), resource.getDisplayType())) {
                routeMeta.setIframe(true);
            }
            routeRecord.setName(resource.getResourceCode())
                    .setPath(resource.getRoutePath())
                    .setRedirect(resource.getRedirectPath())
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
