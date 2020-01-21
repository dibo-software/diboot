package com.diboot.iam.service;

import com.diboot.iam.entity.IamPermission;
import com.diboot.iam.vo.PermissionVO;

import java.util.List;

/**
* 权限相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
public interface IamPermissionService extends BaseIamService<IamPermission> {

    /**
     * 获取指定应用下的全部权限
     * @param application
     * @return
     */
    List<IamPermission> getAllPermissions(String application);

    /**
     * 获取指定应用下的全部权限，并转换为树形结构VO
     * @param application
     * @return
     */
    List<PermissionVO> getAllPermissionsTree(String application);

    /**
     * 更新权限及其子节点
     * @param oldPermission
     * @param newPermission
     * @return
     */
    boolean updatePermissionAndChildren(PermissionVO oldPermission, PermissionVO newPermission);
}