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


import com.diboot.iam.dto.IamResourcePermissionDTO;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import com.diboot.iam.vo.InvalidResourcePermissionVO;

import java.util.List;
import java.util.Map;

/**
 * 前端资源权限相关Service
 *
 * @author yangzhao
 * @version 2.0.0
 * @date 2020-02-27
 * Copyright © diboot.com
 */
public interface IamResourcePermissionService extends BaseIamService<IamResourcePermission> {

    /***
     * 深度创建资源权限及其所有子列表
     * @param iamResourcePermissionListVO
     */
    void deepCreatePermissionAndChildren(IamResourcePermissionListVO iamResourcePermissionListVO);

    /***
     * 创建菜单和按钮/权限列表
     * @param iamResourcePermissionDTO
     */
    void createMenuAndPermissions(IamResourcePermissionDTO iamResourcePermissionDTO);

    /***
     * 更新菜单和按钮/权限列表
     * @param iamResourcePermissionDTO
     */
    void updateMenuAndPermissions(IamResourcePermissionDTO iamResourcePermissionDTO);

    /***
     * 删除菜单及其包含的所有子菜单以及按钮/权限列表
     * @param id
     */
    void deleteMenuAndPermissions(Long id);

    /***
     * 删除指定id的权限及其包含的所有子菜单以及按钮/权限列表
     * @param idList
     */
    void deleteMenuAndPermissions(List<Long> idList);

    /**
     * 获取所有前端权限定义
     *
     * @param application
     * @return
     */
    List<IamResourcePermission> getAllResourcePermissions(String application);

    /***
     * 对列表进行排序
     * @param permissionList
     */
    void sortList(List<IamResourcePermission> permissionList);

    /***
     * 提取代码中的权限和已经存在数据库的权限不同的数据
     *
     * 1、获取DB存储的API<br/>
     * 2、获取代码中的API<br/>
     * 3、对比，提出返回结果<br/>
     * 结果返回：数据库中无效的API、数据库无效的记录id
     *
     * @param application
     * @return
     */
    Map<String, Object> extractCodeDiffDbPermissions(String application);
}