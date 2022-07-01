/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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

import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.vo.IamOrgVO;

import java.util.List;

/**
* 组织机构相关Service
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
public interface IamOrgService extends BaseIamService<IamOrg> {

    /**
     * 获取全部子节点ID
     * @param rootOrgId
     * @return
     */
    List<Long> getChildOrgIds(Long rootOrgId);

    /**
     * 获取指定根下的全部节点的组织树
     * @param rootOrgId
     * @return
     */
    List<IamOrgVO> getOrgTree(Long rootOrgId);

    /***
     * 对部门列表进行排序
     * @param orgList
     */
    void sortList(List<IamOrg> orgList);

    /**
     * 获取当前部门节点所有上级部门id集合
     * @param orgId
     * @return
     */
    List<Long> getParentOrgIds(Long orgId);

    /**
     * 获取当前部门节点所有上级部门id集合
     * @param orgId
     * @param includeThis 是否包含orgId
     * @return
     */
    List<Long> getParentOrgIds(Long orgId, boolean includeThis);

    /**
     * 获取某负责人负责的相关部门ids
     * @param managerId 负责人id
     * @return
     */
    List<Long> getOrgIdsByManagerId(Long managerId);

}