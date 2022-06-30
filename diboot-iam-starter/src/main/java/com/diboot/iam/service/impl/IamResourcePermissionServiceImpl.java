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
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamResourcePermissionDTO;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.mapper.IamResourcePermissionMapper;
import com.diboot.iam.service.IamResourcePermissionService;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 前端资源权限相关Service实现
* @author yangzhao
* @version 2.0.0
* @date 2020-02-27
 * Copyright © diboot.com
*/
@Service
@Slf4j
public class IamResourcePermissionServiceImpl extends BaseIamServiceImpl<IamResourcePermissionMapper, IamResourcePermission> implements IamResourcePermissionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deepCreatePermissionAndChildren(IamResourcePermissionListVO iamResourcePermissionListVO) {
        if (iamResourcePermissionListVO == null) {
            return ;
        }
        IamResourcePermission iamResourcePermission = (IamResourcePermission) iamResourcePermissionListVO;
        if(!super.createEntity(iamResourcePermission)){
            log.warn("新建资源权限失败，displayType="+iamResourcePermission.getDisplayType());
            throw new BusinessException(Status.FAIL_OPERATION, "新建资源权限失败");
        }
        List<IamResourcePermissionListVO> children = iamResourcePermissionListVO.getChildren();
        if (V.notEmpty(children)) {
            for (IamResourcePermissionListVO vo : children) {
                vo.setParentId(iamResourcePermission.getId());
                this.deepCreatePermissionAndChildren(vo);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenuAndPermissions(IamResourcePermissionDTO iamResourcePermissionDTO) {
        // 创建menu
        iamResourcePermissionDTO.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name());
        boolean success = this.createEntity(iamResourcePermissionDTO);
        if (!success){
            throw new BusinessException(Status.FAIL_OPERATION, "创建菜单资源失败");
        }

        // 批量创建按钮/权限列表
        List<IamResourcePermissionDTO> permissionDTOList = iamResourcePermissionDTO.getPermissionList();
        if (V.isEmpty(permissionDTOList)){
            return;
        }
        List<IamResourcePermission> permissionList = BeanUtils.convertList(permissionDTOList, IamResourcePermission.class);
        // 设置每一条按钮/权限的parentId与接口列表
        permissionList.forEach(p -> {
            p.setParentId(iamResourcePermissionDTO.getId());
            p.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        this.createEntities(permissionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuAndPermissions(IamResourcePermissionDTO iamResourcePermissionDTO) {
        // 检查是否设置了自身id为parentId，如果设置parentId与自身id相同，将会导致非常严重的潜在隐患
        if (V.equals(iamResourcePermissionDTO.getId(), iamResourcePermissionDTO.getParentId())){
            throw new BusinessException(Status.FAIL_OPERATION, "不可设置父级菜单资源为自身");
        }
        // 更新menu
        this.updateEntity(iamResourcePermissionDTO);
        List<IamResourcePermissionDTO> permissionList = iamResourcePermissionDTO.getPermissionList();
        permissionList.forEach(p -> {
            p.setParentId(iamResourcePermissionDTO.getId());
            p.setDisplayType(Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        // 需要更新的列表
        List<IamResourcePermissionDTO> updatePermissionList = permissionList.stream()
                .filter(p -> V.notEmpty(p.getId()))
                .collect(Collectors.toList());
        // 需要新建的列表
        List<IamResourcePermissionDTO> createPermissionDTOList = permissionList.stream()
                .filter(p -> V.isEmpty(p.getId()))
                .collect(Collectors.toList());
        List<Long> updatePermissionIdList = updatePermissionList.stream()
                .map(IamResourcePermission::getId)
                .collect(Collectors.toList());
        // 批量删除不存在的按钮/权限列表
        List<IamResourcePermission> oldPermissionList = this.getEntityList(
                Wrappers.<IamResourcePermission>lambdaQuery()
                        .eq(IamResourcePermission::getParentId, iamResourcePermissionDTO.getId())
                        .eq(IamResourcePermission::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name())
        );
        if (V.notEmpty(oldPermissionList)){
            LambdaQueryWrapper<IamResourcePermission> deleteWrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                    .eq(IamResourcePermission::getParentId, iamResourcePermissionDTO.getId())
                    .eq(IamResourcePermission::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
            if (V.notEmpty(updatePermissionIdList)) {
                deleteWrapper.notIn(IamResourcePermission::getId, updatePermissionIdList);
            }
            this.deleteEntities(deleteWrapper);
        }
        // 批量新建按钮/权限列表
        if (V.notEmpty(createPermissionDTOList)) {
            List<IamResourcePermission> createPermissionList = BeanUtils.convertList(createPermissionDTOList, IamResourcePermission.class);
            this.createEntities(createPermissionList);
        }
        // 批量更新按钮/权限列表
        if (V.notEmpty(updatePermissionList)) {
            for (IamResourcePermissionDTO updatePermission : updatePermissionList) {
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
    public void deleteMenuAndPermissions(Long id) {
        if (V.isEmpty(id) || id == 0L){
            throw new BusinessException(Status.FAIL_OPERATION, "参数错误");
        }
        // 删除该菜单
        this.deleteEntity(id);
        // 删除该菜单下的子菜单列表和相关按钮/权限列表
        LambdaQueryWrapper<IamResourcePermission> subWrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                .eq(IamResourcePermission::getParentId, id);
        List<IamResourcePermission> subList = this.getEntityList(subWrapper);
        if (subList.size() > 0){
            this.deleteEntities(subWrapper);
        }
        // 递归删除下级子菜单的子菜单以及按钮/权限列表
        List<Long> subMenuIdList = subList.stream()
                .filter(item -> Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name().equals(item.getDisplayType()))
                .map(IamResourcePermission::getId)
                .collect(Collectors.toList());
        if (V.notEmpty(subMenuIdList)){
            subMenuIdList.forEach(menuId -> {
                log.info("开始递归删除子资源：{}", menuId);
                this.deleteMenuAndPermissions(menuId);
            });
        }
    }

    @Override
    public List<IamResourcePermission> getAllResourcePermissions(String application) {
        // 查询数据库中的所有权限
        List<IamResourcePermission> entList = this.getEntityList(null);
        return entList;
    }

    @Override
    public void sortList(List<IamResourcePermission> permissionList) {
        if (V.isEmpty(permissionList)) {
            throw new BusinessException(Status.FAIL_OPERATION, "排序列表不能为空");
        }
        List<Long> sortIdList = new ArrayList();
        // 先将所有序号重新设置为自身当前id
        for (IamResourcePermission item : permissionList) {
            item.setSortId(item.getId());
            sortIdList.add(item.getSortId());
        }
        // 将序号列表倒序排序
        sortIdList = sortIdList.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        // 整理需要更新的列表
        List<IamResourcePermission> updateList = new ArrayList<>();
        for (int i=0; i<permissionList.size(); i++) {
            IamResourcePermission item = permissionList.get(i);
            IamResourcePermission updateItem = new IamResourcePermission();
            updateItem.setId(item.getId());
            updateItem.setSortId(sortIdList.get(i));
            updateList.add(updateItem);
        }
        if (updateList.size() > 0) {
            super.updateBatchById(updateList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuAndPermissions(List<Long> idList) {
        if (V.isEmpty(idList)){
            throw new BusinessException(Status.FAIL_OPERATION, "id列表不能为空");
        }
        // 删除所有idList权限
        this.deleteEntities(
                Wrappers.<IamResourcePermission>lambdaQuery().in(IamResourcePermission::getId, idList)
        );
        // 删除idList下的子菜单列表和相关按钮/权限列表
        LambdaQueryWrapper<IamResourcePermission> subWrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                .in(IamResourcePermission::getParentId, idList);
        List<IamResourcePermission> subList = this.getEntityList(subWrapper);
        if (subList.size() > 0){
            this.deleteEntities(subWrapper);
        }
        // 筛选出是菜单的选项，递归删除
        List<Long> subMenuIdList = subList.stream()
                .filter(item -> Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name().equals(item.getDisplayType()))
                .map(IamResourcePermission::getId)
                .collect(Collectors.toList());
        if (V.notEmpty(subMenuIdList)){
            deleteMenuAndPermissions(subMenuIdList);
        }
    }

    /***
     * 检测是否具有脏数据存在
     * @return
     */
    private boolean hasDirtyData() {
        List<IamResourcePermission> list = this.getEntityList(null);
        if (V.isEmpty(list)){
            return false;
        }
        Map<String, IamResourcePermission> idObjectMap = BeanUtils.convertToStringKeyObjectMap(list, BeanUtils.convertToFieldName(IamResourcePermission::getId));
        List<Long> deleteIdList = new ArrayList<>();
        for (IamResourcePermission item : list){
            if (!hasTopRootNode(idObjectMap, item, null)){
                deleteIdList.add(item.getId());
            }
        }
        return V.notEmpty(deleteIdList);
    }

    /***
     * 清理没有关联关系的
     */
    private void clearDirtyData(){
        List<IamResourcePermission> list = this.getEntityList(null);
        if (V.isEmpty(list)){
            return;
        }
        Map<String, IamResourcePermission> idObjectMap = BeanUtils.convertToStringKeyObjectMap(list, BeanUtils.convertToFieldName(IamResourcePermission::getId));
        List<Long> deleteIdList = new ArrayList<>();
        for (IamResourcePermission item : list){
            if (!hasTopRootNode(idObjectMap, item, null)){
                deleteIdList.add(item.getId());
            }
        }
        if (V.notEmpty(deleteIdList)){
            LambdaQueryWrapper deleteWrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                    .in(IamResourcePermission::getId, deleteIdList);
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
    private boolean hasTopRootNode(Map<String, IamResourcePermission> idObjectMap, IamResourcePermission item, List<Long> existIdList) {
        if (V.equals(item.getParentId(), 0L)) {
            return true;
        }
        if (existIdList == null) {
            existIdList = new ArrayList<Long>();
        }
        if (existIdList.contains(item.getId())) {
            return false;
        }
        // 如果不是顶级节点，则以此向上查找顶级节点，如果没有找到父级节点，则认为没有顶级节点，如果找到，则继续查找父级节点是否具有顶级节点
        IamResourcePermission parentItem = idObjectMap.get(String.valueOf(item.getParentId()));
        if (parentItem == null){
            return false;
        }

        // 记录下当前检查的id，以免循环调用
        existIdList.add(item.getId());
        return hasTopRootNode(idObjectMap, parentItem, existIdList);
    }
}
