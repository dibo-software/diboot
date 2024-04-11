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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamResourceDTO;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.mapper.IamResourceMapper;
import com.diboot.iam.service.IamResourceService;
import com.diboot.iam.vo.IamResourceListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 前端资源权限相关Service实现
 *
 * @author yangzhao
 * @version 2.0.0
 * @date 2020-02-27
 * Copyright © diboot.com
 */
@Service
@Slf4j
public class IamResourceServiceImpl extends BaseServiceImpl<IamResourceMapper, IamResource> implements IamResourceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deepCreateResourceAndChildren(IamResourceListVO iamResource) {
        if (iamResource == null) {
            return;
        }
        if (!super.createEntity(iamResource)) {
            log.warn("新建资源权限失败，displayType=" + iamResource.getDisplayType());
            throw new BusinessException(Status.FAIL_OPERATION, "新建资源权限失败");
        }
        List<IamResourceListVO> children = iamResource.getChildren();
        if (V.notEmpty(children)) {
            for (IamResourceListVO vo : children) {
                vo.setParentId(iamResource.getId());
                if(V.isEmpty(vo.getId())) {
                    vo.setTenantId(iamResource.getTenantId());
                }
                this.deepCreateResourceAndChildren(vo);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenuResources(IamResourceDTO iamResourceDTO) {
        // 创建menu
        boolean success = this.createEntity(iamResourceDTO);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "创建菜单资源失败");
        }
        // 批量创建按钮/权限列表
        List<IamResource> permissionList = iamResourceDTO.getPermissionList();
        if (V.isEmpty(permissionList)) {
            return;
        }
        // 设置每一条按钮/权限的parentId与接口列表
        permissionList.forEach(p -> {
            p.setParentId(iamResourceDTO.getId());
            p.setTenantId(iamResourceDTO.getTenantId());
            p.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        this.createEntities(permissionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuResources(IamResourceDTO iamResourceDTO) {
        // 检查是否设置了自身id为parentId，如果设置parentId与自身id相同，将会导致非常严重的潜在隐患
        if (V.equals(iamResourceDTO.getId(), iamResourceDTO.getParentId())) {
            throw new BusinessException(Status.FAIL_OPERATION, "不可设置父级菜单资源为自身");
        }
        // 更新 menu
        this.updateEntity(iamResourceDTO);
        List<IamResource> permissionList = iamResourceDTO.getPermissionList();
        permissionList.forEach(p -> {
            p.setParentId(iamResourceDTO.getId());
            if(V.isEmpty(p.getId())) {
                p.setTenantId(iamResourceDTO.getTenantId());
            }
            p.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        // 需要更新的列表
        List<IamResource> updatePermissionList = permissionList.stream()
                .filter(p -> V.notEmpty(p.getId()))
                .collect(Collectors.toList());
        // 需要新建的列表
        List<IamResource> createPermissionList = permissionList.stream()
                .filter(p -> V.isEmpty(p.getId()))
                .collect(Collectors.toList());
        // 批量删除不存在的按钮/权限列表
        LambdaQueryWrapper<IamResource> deleteWrapper = Wrappers.<IamResource>lambdaQuery()
                .eq(IamResource::getParentId, iamResourceDTO.getId())
                .eq(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION);
        if (V.notEmpty(updatePermissionList)) {
            deleteWrapper.notIn(IamResource::getId, updatePermissionList.stream().map(IamResource::getId).collect(Collectors.toList()));
        }
        this.deleteEntities(deleteWrapper);
        // 批量新建按钮/权限列表
        if (V.notEmpty(createPermissionList)) {
            this.createEntities(createPermissionList);
        }
        // 批量更新按钮/权限列表
        if (V.notEmpty(updatePermissionList)) {
            this.updateEntities(updatePermissionList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuResources(String id) {
        if (V.isEmpty(id)) {
            throw new BusinessException(Status.FAIL_OPERATION, "参数错误");
        }
        // 删除该菜单
        this.deleteEntity(id);
        // 删除该菜单下的子菜单列表和相关按钮/权限列表
        LambdaQueryWrapper<IamResource> subWrapper = Wrappers.<IamResource>lambdaQuery()
                .eq(IamResource::getParentId, id);
        List<IamResource> subList = this.getEntityList(subWrapper);
        if (subList.size() > 0) {
            this.deleteEntities(subWrapper);
        }
        // 递归删除下级子菜单的子菜单以及按钮/权限列表
        List<String> subMenuIdList = subList.stream()
                .filter(item -> Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name().equals(item.getDisplayType()))
                .map(IamResource::getId)
                .collect(Collectors.toList());
        if (V.notEmpty(subMenuIdList)) {
            subMenuIdList.forEach(menuId -> {
                log.info("开始递归删除子资源：{}", menuId);
                this.deleteMenuResources(menuId);
            });
        }
    }

    @Override
    public List<IamResource> getAllResources(String application) {
        // 查询数据库中的所有权限
        List<IamResource> entList = this.getEntityList(null);
        return entList;
    }

    @Override
    public List<IamResourceListVO> getMenuResources(String application) {
        LambdaQueryWrapper<IamResource> wrapper = Wrappers.<IamResource>lambdaQuery()
                .eq(V.notEmpty(application), IamResource::getAppModule, application)
                .ne(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        List<IamResource> menuPermissionList = getEntityList(wrapper);
        if (V.isEmpty(menuPermissionList)) {
            return Collections.emptyList();
        }
        // 构建树结构
        List<IamResourceListVO> iamResourceTreeVOList = RelationsBinder.convertAndBind(menuPermissionList, IamResourceListVO.class);
        return BeanUtils.buildTree(iamResourceTreeVOList, Cons.TREE_ROOT_ID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuResources(List<String> idList) {
        if (V.isEmpty(idList)) {
            throw new BusinessException(Status.FAIL_OPERATION, "id列表不能为空");
        }
        // 删除所有idList权限
        this.deleteEntities(
                Wrappers.<IamResource>lambdaQuery().in(IamResource::getId, idList)
        );
        // 删除idList下的子菜单列表和相关按钮/权限列表
        LambdaQueryWrapper<IamResource> subWrapper = Wrappers.<IamResource>lambdaQuery()
                .in(IamResource::getParentId, idList);
        List<IamResource> subList = this.getEntityList(subWrapper);
        if (subList.size() > 0) {
            this.deleteEntities(subWrapper);
        }
        // 筛选出是菜单的选项，递归删除
        List<String> subMenuIdList = subList.stream()
                .filter(item -> Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name().equals(item.getDisplayType()))
                .map(IamResource::getId)
                .collect(Collectors.toList());
        if (V.notEmpty(subMenuIdList)) {
            deleteMenuResources(subMenuIdList);
        }
    }

    @Override
    public List<LabelValue> getMenuCatalogues() {
        LambdaQueryWrapper<IamResource> queryWrapper = new LambdaQueryWrapper<IamResource>()
                .select(IamResource::getDisplayName, IamResource::getId, IamResource::getRoutePath, IamResource::getParentId)
        .in(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.CATALOGUE.name())
        .orderByAsc(IamResource::getSortId);
        return this.getLabelValueList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createOrUpdateMenuResources(IamResourceDTO resourceDTO) {
        // 如果dto的id存在，则更新
        if(V.isEmpty(resourceDTO.getId())) {
            this.createMenuResources(resourceDTO);
            return;
        }
        // 更新 menu
        this.updateEntity(resourceDTO);
        List<IamResource> permissionList = resourceDTO.getPermissionList();
        if(V.isEmpty(permissionList)) {
            return;
        }
        List<String> resourceCodes = new ArrayList<>();
        permissionList.forEach(p -> {
            p.setParentId(resourceDTO.getId());
            p.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
            resourceCodes.add(p.getResourceCode());
        });
        // 查询本次涉及的资源
        LambdaQueryWrapper<IamResource> oldPermissionsQuery = new QueryWrapper<IamResource>().lambda()
                .eq(IamResource::getParentId, resourceDTO.getId()).eq(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name())
                .in(IamResource::getResourceCode, resourceCodes);
        List<IamResource> oldPermissionList = this.getEntityList(oldPermissionsQuery);
        Map<String, IamResource> oldPermissionMap = oldPermissionList.stream().collect(Collectors.toMap(IamResource::getResourceCode, p->p));
        // 需要更新的列表
        List<IamResource> updatePermissionList = new ArrayList<>();
        List<IamResource> createPermissionList = new ArrayList<>();
        for(IamResource current : permissionList) {
            IamResource originRes = oldPermissionMap.get(current.getResourceCode());
            if(originRes != null) {
                originRes.setMeta(current.getMeta()).setPermissionCode(current.getPermissionCode()).setDisplayName(current.getDisplayName());
                updatePermissionList.add(originRes);
            }
            else {
                current.setTenantId(resourceDTO.getTenantId());
                createPermissionList.add(current);
            }
        }
        // 批量新建按钮/权限列表
        if (V.notEmpty(createPermissionList)) {
            this.createEntities(createPermissionList);
        }
        // 批量更新按钮/权限列表
        if (V.notEmpty(updatePermissionList)) {
            this.updateEntities(updatePermissionList);
        }
    }
}
