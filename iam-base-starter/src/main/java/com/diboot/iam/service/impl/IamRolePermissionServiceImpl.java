package com.diboot.iam.service.impl;

import com.diboot.core.util.BeanUtils;
import com.diboot.iam.entity.IamPermission;
import com.diboot.iam.entity.IamRolePermission;
import com.diboot.iam.mapper.IamRolePermissionMapper;
import com.diboot.iam.service.IamPermissionService;
import com.diboot.iam.service.IamRolePermissionService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.vo.PermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private IamPermissionService iamPermissionService;

    @Autowired
    private IamRolePermissionMapper iamRolePermissionMapper;

    @Override
    public List<PermissionVO> getPermissionsByRoleId(String application, Long roleId) {
        List<Long> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        return getPermissionsByRoleIds(application, roleIdList);
    }

    @Override
    public List<PermissionVO> getPermissionsByRoleIds(String application, List<Long> roleIds) {
        List<IamPermission> list = iamRolePermissionMapper.getPermissionsByRoleIds(application, roleIds);
        List<PermissionVO> voList = BeanUtils.convertList(list, PermissionVO.class);
        return BeanUtils.buildTree(voList);
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
        return createEntities(rolePermissionList);
    }

    @Override
    public IamRoleService getRoleService() {
        return iamRoleService;
    }

    @Override
    public IamPermissionService getPermissionService() {
        return iamPermissionService;
    }
}
