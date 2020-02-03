package com.diboot.iam.service;

import com.diboot.iam.entity.IamRolePermission;
import com.diboot.iam.vo.PermissionVO;

import java.util.List;

/**
* 角色权限关联相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
public interface IamRolePermissionService extends BaseIamService<IamRolePermission> {

    /**
     * 获取指定角色对应的权限集
     * @param application
     * @param roleId
     * @return
     */
    List<PermissionVO> getPermissionsByRoleId(String application, Long roleId);

    /**
     * 获取指定角色集合对应的权限集
     * @param application
     * @param roleIds
     * @return
     */
    List<PermissionVO> getPermissionsByRoleIds(String application, List<Long> roleIds);

    /**
     * 批量创建角色与权限集的关系
     * @param roleId
     * @param permissionIdList
     * @return
     */
    boolean createRolePermissionRelations(Long roleId, List<Long> permissionIdList);

    /***
     * 批量更新角色与权限集的关系
     * @param roleId
     * @param permissionIdList
     * @return
     */
    boolean updateRolePermissionRelations(Long roleId, List<Long> permissionIdList);

    /**
     * 获取RoleService实例
     * @return
     */
    IamRoleService getRoleService();

    /**
     * 获取PermissionService实例
     * @return
     */
    IamPermissionService getPermissionService();
}