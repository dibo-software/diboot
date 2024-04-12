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


import com.diboot.core.service.BaseService;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.dto.IamResourceDTO;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.vo.IamResourceListVO;

import java.util.List;

/**
 * 前端资源权限相关Service
 *
 * @author yangzhao
 * @version 2.0.0
 * @date 2020-02-27
 * Copyright © diboot.com
 */
public interface IamResourceService extends BaseService<IamResource> {

    /***
     * 深度创建资源权限及其所有子列表
     * @param iamResourceListVO
     */
    void deepCreateResourceAndChildren(IamResourceListVO iamResourceListVO);

    /***
     * 创建菜单和按钮/权限列表
     * @param iamResourceDTO
     */
    void createMenuResources(IamResourceDTO iamResourceDTO);

    /***
     * 更新菜单和按钮/权限列表
     * @param iamResourceDTO
     */
    void updateMenuResources(IamResourceDTO iamResourceDTO);

    /***
     * 删除菜单及其包含的所有子菜单以及按钮/权限列表
     * @param id
     */
    void deleteMenuResources(String id);

    /***
     * 删除指定id的权限及其包含的所有子菜单以及按钮/权限列表
     * @param idList
     */
    void deleteMenuResources(List<String> idList);

    /**
     * 获取所有前端权限定义
     *
     * @param application
     * @return
     */
    List<IamResource> getAllResources(String application);

    /**
     * 获取前端菜单
     *
     * @param application
     * @return
     */
    List<IamResourceListVO> getMenuResources(String application);

    /**
     * 获取系统中的所有菜单目录
     * @return
     */
    List<LabelValue> getMenuCatalogues();

    /**
     * 创建或者更新菜单资源
     * @param resourceDTO
     */
    void createOrUpdateMenuResources(IamResourceDTO resourceDTO);

}