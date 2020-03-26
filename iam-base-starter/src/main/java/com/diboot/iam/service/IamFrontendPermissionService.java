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
package com.diboot.iam.service;


import com.diboot.iam.dto.IamFrontendPermissionDTO;
import com.diboot.iam.entity.IamFrontendPermission;
import com.diboot.iam.vo.IamFrontendPermissionListVO;

import java.util.List;

/**
* 前端菜单相关Service
* @author yangzhao
* @version 2.0.0
* @date 2020-02-27
 * Copyright © diboot.com
*/
public interface IamFrontendPermissionService extends BaseIamService<IamFrontendPermission> {

    /***
     * 深度创建菜单权限及其所有子列表
     * @param iamFrontendPermissionListVO
     */
    void deepCreatePermissionAndChildren(IamFrontendPermissionListVO iamFrontendPermissionListVO);

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

    /**
     * 获取所有前端权限定义
     * @param application
     * @return
     */
    List<IamFrontendPermission> getAllFrontendPermissions(String application);

    /***
     * 对列表进行排序
     * @param permissionList
     */
    void sortList(List<IamFrontendPermission> permissionList);
}