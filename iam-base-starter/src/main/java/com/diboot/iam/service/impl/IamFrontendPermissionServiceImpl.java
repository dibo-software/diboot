package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamFrontendPermissionDTO;
import com.diboot.iam.entity.IamFrontendPermission;
import com.diboot.iam.mapper.IamFrontendPermissionMapper;
import com.diboot.iam.service.IamFrontendPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 前端菜单相关Service实现
* @author yangzhao
* @version 2.0.0
* @date 2020-02-27
 * Copyright © diboot.com
*/
@Service
@Slf4j
public class IamFrontendPermissionServiceImpl extends BaseIamServiceImpl<IamFrontendPermissionMapper, IamFrontendPermission> implements IamFrontendPermissionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenuAndPermissions(IamFrontendPermissionDTO iamFrontendPermissionDTO) {
        // 创建menu
        iamFrontendPermissionDTO.setDisplayType(Cons.FRONTEND_PERMISSION_DISPLAY_TYPE.MENU.name());
        boolean success = this.createEntity(iamFrontendPermissionDTO);
        if (!success){
            throw new BusinessException(Status.FAIL_OPERATION, "创建菜单失败");
        }

        // 批量创建按钮/权限列表
        List<IamFrontendPermissionDTO> permissionList = iamFrontendPermissionDTO.getPermissionList();
        if (V.isEmpty(permissionList)){
            return;
        }
        // 设置每一条按钮/权限的parentId与接口列表
        permissionList.forEach(p -> {
            p.setParentId(iamFrontendPermissionDTO.getId());
            p.setDisplayType(Cons.FRONTEND_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        this.createEntities(permissionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuAndPermissions(IamFrontendPermissionDTO iamFrontendPermissionDTO) {
        // 检查是否设置了自身id为parentId，如果设置parentId与自身id相同，将会导致非常严重的潜在隐患
        if (V.equals(iamFrontendPermissionDTO.getId(), iamFrontendPermissionDTO.getParentId())){
            throw new BusinessException(Status.FAIL_OPERATION, "不可设置父级菜单为自身");
        }
        // 设置menu的接口列表
        if (V.notEmpty(iamFrontendPermissionDTO.getApiSetList())){
            iamFrontendPermissionDTO.setApiSet(S.join(iamFrontendPermissionDTO.getApiSetList(), ","));
        }
        // 更新menu
        this.updateEntity(iamFrontendPermissionDTO);
        List<IamFrontendPermissionDTO> permissionList = iamFrontendPermissionDTO.getPermissionList();
        permissionList.forEach(p -> {
            p.setParentId(iamFrontendPermissionDTO.getId());
            p.setDisplayType(Cons.FRONTEND_PERMISSION_DISPLAY_TYPE.PERMISSION.name());
        });
        // 需要更新的列表
        List<IamFrontendPermissionDTO> updatePermissionList = permissionList.stream()
                .filter(p -> V.notEmpty(p.getId()))
                .collect(Collectors.toList());
        // 需要新建的列表
        List<IamFrontendPermissionDTO> createPermissionList = permissionList.stream()
                .filter(p -> V.isEmpty(p.getId()))
                .collect(Collectors.toList());
        List<Long> updatePermissionIdList = updatePermissionList.stream()
                .map(IamFrontendPermission::getId)
                .collect(Collectors.toList());
        // 批量删除不存在的按钮/权限列表
        List<IamFrontendPermission> oldPermissionList = this.getEntityList(
                Wrappers.<IamFrontendPermission>lambdaQuery()
                        .eq(IamFrontendPermission::getParentId, iamFrontendPermissionDTO.getId())
                        .eq(IamFrontendPermission::getDisplayType, Cons.FRONTEND_PERMISSION_DISPLAY_TYPE.PERMISSION)
        );
        if (V.notEmpty(oldPermissionList)){
            LambdaQueryWrapper<IamFrontendPermission> deleteWrapper = Wrappers.<IamFrontendPermission>lambdaQuery()
                    .eq(IamFrontendPermission::getParentId, iamFrontendPermissionDTO.getId())
                    .eq(IamFrontendPermission::getDisplayType, Cons.FRONTEND_PERMISSION_DISPLAY_TYPE.PERMISSION);
            if (V.notEmpty(updatePermissionIdList)) {
                deleteWrapper.notIn(IamFrontendPermission::getId, updatePermissionIdList);
            }
            this.deleteEntities(deleteWrapper);
        }
        // 批量新建按钮/权限列表
        if (V.notEmpty(createPermissionList)) {
            this.createEntities(createPermissionList);
        }
        // 批量更新按钮/权限列表
        if (V.notEmpty(updatePermissionList)) {
            for (IamFrontendPermissionDTO updatePermission : updatePermissionList) {
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
        LambdaQueryWrapper<IamFrontendPermission> subWrapper = Wrappers.<IamFrontendPermission>lambdaQuery()
                .eq(IamFrontendPermission::getParentId, id);
        List<IamFrontendPermission> subList = this.getEntityList(subWrapper);
        if (subList.size() > 0){
            this.deleteEntities(subWrapper);
        }
        // 递归删除下级子菜单的子菜单以及按钮/权限列表
        List<Long> subMenuIdList = subList.stream()
                .filter(item -> Cons.FRONTEND_PERMISSION_DISPLAY_TYPE.MENU.name().equals(item.getDisplayType()))
                .map(IamFrontendPermission::getId)
                .collect(Collectors.toList());
        if (V.notEmpty(subMenuIdList)){
            subMenuIdList.forEach(menuId -> {
                log.info("开始递归删除子菜单：{}", menuId);
                this.deleteMenuAndPermissions(menuId);
            });
        }
    }

    @Override
    public List<IamFrontendPermission> getAllFrontendPermissions(String application) {
        // 查询数据库中的所有权限
        List<IamFrontendPermission> entList = this.getEntityList(null);
        return entList;
    }

    @Override
    public void sortList(List<IamFrontendPermission> permissionList) {
        if (V.isEmpty(permissionList)) {
            throw new BusinessException(Status.FAIL_OPERATION, "排序列表不能为空");
        }
        List<Long> sortIdList = new ArrayList();
        // 先将所有序号重新设置为自身当前id
        for (IamFrontendPermission item : permissionList) {
            item.setSortId(item.getId());
            sortIdList.add(item.getSortId());
        }
        // 将序号列表倒序排序
        sortIdList = sortIdList.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        // 整理需要更新的列表
        List<IamFrontendPermission> updateList = new ArrayList<>();
        for (int i=0; i<permissionList.size(); i++) {
            IamFrontendPermission item = permissionList.get(i);
            IamFrontendPermission updateItem = new IamFrontendPermission();
            updateItem.setId(item.getId());
            updateItem.setSortId(sortIdList.get(i));
            updateList.add(updateItem);
        }
        if (updateList.size() > 0) {
            super.updateBatchById(updateList);
        }
    }

    /***
     * 检测是否又脏数据存在
     * @return
     */
    private boolean hasDirtyData() {
        List<IamFrontendPermission> list = this.getEntityList(null);
        if (V.isEmpty(list)){
            return false;
        }
        Map<String, IamFrontendPermission> idObjectMap = BeanUtils.convertToStringKeyObjectMap(list, BeanUtils.convertToFieldName(IamFrontendPermission::getId));
        List<Long> deleteIdList = new ArrayList<>();
        for (IamFrontendPermission item : list){
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
        List<IamFrontendPermission> list = this.getEntityList(null);
        if (V.isEmpty(list)){
            return;
        }
        Map<String, IamFrontendPermission> idObjectMap = BeanUtils.convertToStringKeyObjectMap(list, BeanUtils.convertToFieldName(IamFrontendPermission::getId));
        List<Long> deleteIdList = new ArrayList<>();
        for (IamFrontendPermission item : list){
            if (!hasTopRootNode(idObjectMap, item, null)){
                deleteIdList.add(item.getId());
            }
        }
        if (V.notEmpty(deleteIdList)){
            LambdaQueryWrapper deleteWrapper = Wrappers.<IamFrontendPermission>lambdaQuery()
                    .in(IamFrontendPermission::getId, deleteIdList);
            int count = this.getEntityListCount(deleteWrapper);
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
    private boolean hasTopRootNode(Map<String, IamFrontendPermission> idObjectMap, IamFrontendPermission item, List<Long> existIdList) {
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
        IamFrontendPermission parentItem = idObjectMap.get(String.valueOf(item.getParentId()));
        if (parentItem == null){
            return false;
        }

        // 记录下当前检查的id，以免循环调用
        existIdList.add(item.getId());
        return hasTopRootNode(idObjectMap, parentItem, existIdList);
    }
}
