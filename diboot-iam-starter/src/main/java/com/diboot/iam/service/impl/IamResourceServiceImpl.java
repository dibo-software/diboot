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
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
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
public class IamResourceServiceImpl extends BaseIamServiceImpl<IamResourceMapper, IamResource> implements IamResourceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deepCreateResourceAndChildren(IamResourceListVO iamResourceListVO) {
        if (iamResourceListVO == null) {
            return ;
        }
        IamResource iamResource = (IamResource) iamResourceListVO;
        if(!super.createEntity(iamResource)){
            log.warn("新建资源权限失败，displayType="+ iamResource.getDisplayType());
            throw new BusinessException(Status.FAIL_OPERATION, "新建资源权限失败");
        }
        List<IamResourceListVO> children = iamResourceListVO.getChildren();
        if (V.notEmpty(children)) {
            for (IamResourceListVO vo : children) {
                vo.setParentId(iamResource.getId());
                this.deepCreateResourceAndChildren(vo);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenuResources(IamResourceDTO iamResourceDTO) {
        // 创建menu
        boolean success = this.createEntity(iamResourceDTO);
        if (!success){
            throw new BusinessException(Status.FAIL_OPERATION, "创建菜单资源失败");
        }

        // 批量创建按钮/权限列表
        List<IamResourceDTO> permissionDTOList = iamResourceDTO.getPermissionList();
        if (V.isEmpty(permissionDTOList)){
            return;
        }
        List<IamResource> permissionList = BeanUtils.convertList(permissionDTOList, IamResource.class);
        // 设置每一条按钮/权限的parentId与接口列表
        permissionList.forEach(p -> {
            p.setParentId(iamResourceDTO.getId());
            p.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        this.createEntities(permissionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuResources(IamResourceDTO iamResourceDTO) {
        // 检查是否设置了自身id为parentId，如果设置parentId与自身id相同，将会导致非常严重的潜在隐患
        if (V.equals(iamResourceDTO.getId(), iamResourceDTO.getParentId())){
            throw new BusinessException(Status.FAIL_OPERATION, "不可设置父级菜单资源为自身");
        }
        // 更新menu
        this.updateEntity(iamResourceDTO);
        List<IamResourceDTO> permissionList = iamResourceDTO.getPermissionList();
        permissionList.forEach(p -> {
            p.setParentId(iamResourceDTO.getId());
            p.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        // 需要更新的列表
        List<IamResourceDTO> updatePermissionList = permissionList.stream()
                .filter(p -> V.notEmpty(p.getId()))
                .collect(Collectors.toList());
        // 需要新建的列表
        List<IamResourceDTO> createPermissionDTOList = permissionList.stream()
                .filter(p -> V.isEmpty(p.getId()))
                .collect(Collectors.toList());
        List<String> updatePermissionIdList = updatePermissionList.stream()
                .map(IamResource::getId)
                .collect(Collectors.toList());
        // 批量删除不存在的按钮/权限列表
        List<IamResource> oldPermissionList = this.getEntityList(
                Wrappers.<IamResource>lambdaQuery()
                        .eq(IamResource::getParentId, iamResourceDTO.getId())
                        .eq(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION)
        );
        if (V.notEmpty(oldPermissionList)) {
            LambdaQueryWrapper<IamResource> deleteWrapper = Wrappers.<IamResource>lambdaQuery()
                    .eq(IamResource::getParentId, iamResourceDTO.getId())
                    .eq(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION);
            if (V.notEmpty(updatePermissionIdList)) {
                deleteWrapper.notIn(IamResource::getId, updatePermissionIdList);
            }
            this.deleteEntities(deleteWrapper);
        }
        // 批量新建按钮/权限列表
        if (V.notEmpty(createPermissionDTOList)) {
            List<IamResource> createPermissionList = BeanUtils.convertList(createPermissionDTOList, IamResource.class);
            this.createEntities(createPermissionList);
        }
        // 批量更新按钮/权限列表
        if (V.notEmpty(updatePermissionList)) {
            for (IamResourceDTO updatePermission : updatePermissionList) {
                this.updateEntity(updatePermission);
            }
        }
        // 检测是否有脏数据存在
        if (hasDirtyData()) {
            throw new BusinessException(Status.FAIL_OPERATION, "父级节点不可设置在自己的子节点上");
        }
        // 清理脏数据
        //this.clearDirtyData();
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

    /***
     * 检测是否具有脏数据存在
     * @return
     */
    private boolean hasDirtyData() {
        List<IamResource> list = this.getEntityList(null);
        if (V.isEmpty(list)) {
            return false;
        }
        Map<String, IamResource> idObjectMap = BeanUtils.convertToStringKeyObjectMap(list, BeanUtils.convertToFieldName(IamResource::getId));
        List<String> deleteIdList = new ArrayList<>();
        for (IamResource item : list) {
            if (!hasTopRootNode(idObjectMap, item, null)) {
                deleteIdList.add(item.getId());
            }
        }
        return V.notEmpty(deleteIdList);
    }

    /***
     * 清理没有关联关系的
     */
    private void clearDirtyData() {
        List<IamResource> list = this.getEntityList(null);
        if (V.isEmpty(list)) {
            return;
        }
        Map<String, IamResource> idObjectMap = BeanUtils.convertToStringKeyObjectMap(list, BeanUtils.convertToFieldName(IamResource::getId));
        List<String> deleteIdList = new ArrayList<>();
        for (IamResource item : list) {
            if (!hasTopRootNode(idObjectMap, item, null)) {
                deleteIdList.add(item.getId());
            }
        }
        if (V.notEmpty(deleteIdList)) {
            LambdaQueryWrapper deleteWrapper = Wrappers.<IamResource>lambdaQuery()
                    .in(IamResource::getId, deleteIdList);
            long count = this.getEntityListCount(deleteWrapper);
            if (count > 0) {
                this.deleteEntities(deleteWrapper);
                log.info("共清理掉{}条无用数据", count);
            }
        }
    }

    /***
     * 是否拥有顶级节点
     * @param idObjectMap
     * @param item
     * @return
     */
    private boolean hasTopRootNode(Map<String, IamResource> idObjectMap, IamResource item, List<String> existIdList) {
        if (V.equals(item.getParentId(), "0")) {
            return true;
        }
        if (existIdList == null) {
            existIdList = new ArrayList<>();
        }
        if (existIdList.contains(item.getId())) {
            return false;
        }
        // 如果不是顶级节点，则以此向上查找顶级节点，如果没有找到父级节点，则认为没有顶级节点，如果找到，则继续查找父级节点是否具有顶级节点
        IamResource parentItem = idObjectMap.get(String.valueOf(item.getParentId()));
        if (parentItem == null) {
            return false;
        }

        // 记录下当前检查的id，以免循环调用
        existIdList.add(item.getId());
        return hasTopRootNode(idObjectMap, parentItem, existIdList);
    }
}
