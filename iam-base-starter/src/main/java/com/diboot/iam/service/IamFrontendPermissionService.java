package com.diboot.iam.service;


import com.diboot.iam.dto.IamFrontendPermissionDTO;
import com.diboot.iam.entity.IamFrontendPermission;

/**
* 前端菜单相关Service
* @author yangzhao
* @version 2.0.0
* @date 2020-02-27
 * Copyright © diboot.com
*/
public interface IamFrontendPermissionService extends BaseIamService<IamFrontendPermission> {

    /***
     * 创建菜单和按钮/权限列表
     * @param iamFrontendPermissionDTO
     */
    void createMenuAndPermissions(IamFrontendPermissionDTO iamFrontendPermissionDTO);

    /***
     * 更新菜单和按钮/权限列表
     * @param iamFrontendPermissionDTO
     */
    void updateMenuAndPermissions(IamFrontendPermissionDTO iamFrontendPermissionDTO);

    /***
     * 删除菜单及其包含的所有子菜单以及按钮/权限列表
     * @param id
     */
    void deleteMenuAndPermissions(Long id);
}