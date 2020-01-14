package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamPermission;
import com.diboot.iam.mapper.IamPermissionMapper;
import com.diboot.iam.service.IamPermissionService;
import com.diboot.iam.vo.PermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 权限相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Service
@Slf4j
public class IamPermissionServiceImpl extends BaseIamServiceImpl<IamPermissionMapper, IamPermission> implements IamPermissionService {

    @Override
    public List<IamPermission> getAllPermissions(String application) {
        // 查询数据库中的所有权限
        QueryWrapper<IamPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(IamPermission::getApplication, application)
                .orderByAsc(IamPermission::getType, IamPermission::getCode, IamPermission::getSortId);
        List<IamPermission> entList = getEntityList(queryWrapper);
        return entList;
    }

    @Override
    public List<PermissionVO> getAllPermissionsTree(String application) {
        // 查询数据库中的所有权限
        List<IamPermission> entList = getAllPermissions(application);
        // 转换为树形结构
        List<PermissionVO> voList = BeanUtils.convertList(entList, PermissionVO.class);
        return BeanUtils.buildTree(voList);
    }

    @Override
    public boolean updatePermissionAndChildren(PermissionVO oldPermission, PermissionVO newPermission) {
        // 全部新增
        if(oldPermission == null && newPermission != null){
            // 保存父节点
            IamPermission parent = (IamPermission)newPermission;
            createEntity(parent);
            // 保存子节点
            if(V.notEmpty(newPermission.getChildren())){
                List<IamPermission> children = new ArrayList<>();
                for(PermissionVO vo : newPermission.getChildren()){
                    IamPermission child = (IamPermission)vo;
                    child.setParentId(parent.getId());
                    children.add(child);
                }
                createEntities(children);
            }
            return true;
        }
        // 全部删除
        if(oldPermission != null && newPermission == null){
            QueryWrapper<IamPermission> queryWrapper = new QueryWrapper();
            queryWrapper.lambda()
                    .eq(IamPermission::getId, oldPermission.getId())
                    .or()
                    .eq(IamPermission::getParentId, oldPermission.getId());
            deleteEntities(queryWrapper);
            return true;
        }
        // 修改
        if(oldPermission != null && newPermission != null){
            // 更新父节点，保持ID不变
            IamPermission parent = (IamPermission)newPermission;
            parent.setId(oldPermission.getId());
            if(V.notEquals(oldPermission.buildEqualsKey(), newPermission.buildEqualsKey())){
                updateEntity(parent);
            }
            // 更新子节点
            updateChildren(oldPermission, newPermission);
        }
        return false;
    }

    /**
     * 更新子节点
     * @param oldPermission
     * @param newPermission
     */
    private void updateChildren(PermissionVO oldPermission, PermissionVO newPermission){
        List<PermissionVO> oldChildren = oldPermission.getChildren(), newChildren = newPermission.getChildren();
        // 需要新增/修改的权限
        if(V.notEmpty(newChildren)){
            // 填充ID
            for(PermissionVO newVo : newChildren){
                newVo.setParentId(newPermission.getId());
            }
            if(V.notEmpty(oldChildren)){// 修改
                Map<String, PermissionVO> key2ObjMap = new HashMap<>();
                for(PermissionVO oldVo : oldChildren){
                    key2ObjMap.put(oldVo.buildExistKey(), oldVo);
                }
                for(PermissionVO newVo : newChildren){
                    PermissionVO oldVo = key2ObjMap.get(newVo.buildExistKey());
                    if(oldVo != null){
                        if(V.notEquals(oldVo.buildEqualsKey(), newVo.buildEqualsKey())){
                            newVo.setId(oldVo.getId());
                            updateEntity((IamPermission)newVo);
                        }
                    }
                    else{
                        createEntity((IamPermission)newVo);
                    }
                }
                key2ObjMap.clear();
                for(PermissionVO newVo : newChildren){
                    key2ObjMap.put(newVo.buildExistKey(), newVo);
                }
                // 基于老的权限删除
                for(PermissionVO oldVo : oldChildren){
                    PermissionVO newVo = key2ObjMap.get(oldVo.buildExistKey());
                    if(newVo == null){ //非空的情况已经在上面处理过
                        deleteEntity(oldVo.getId());
                    }
                }
            }// 新增
            else{
                createEntities(newChildren);
            }
        }
        // 需要删除的权限
        else if(V.notEmpty(oldChildren)){
            List<Long> ids = BeanUtils.collectIdToList(oldChildren);
            QueryWrapper<IamPermission> queryWrapper = new QueryWrapper();
            queryWrapper.lambda().in(IamPermission::getId, ids);
            deleteEntities(queryWrapper);
        }
    }
}
