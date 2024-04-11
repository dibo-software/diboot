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

import com.diboot.core.service.BaseService;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.vo.IamOrgVO;

import java.util.List;
import java.util.Map;

/**
* 组织机构相关Service
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
public interface IamOrgService extends BaseService<IamOrg> {

    /**
     * 获取全部子节点ID
     * @param rootOrgId
     * @return
     */
    List<String> getChildOrgIds(String rootOrgId);

    /**
     * 获取指定根下的全部节点的组织树
     * @param rootOrgId
     * @return
     */
    List<IamOrgVO> getOrgTree(String rootOrgId);

    /**
     * 获取指定根下的全部节点的组织树
     * @param rootOrgId
     * @return
     */
    List<LabelValue> getSimpleOrgTree(String rootOrgId);

    /**
     * 获取当前部门节点所有上级部门id集合
     * @param orgId
     * @return
     */
    List<String> getParentOrgIds(String orgId);

    /**
     * 获取某负责人负责的相关部门ids
     * @param managerId 负责人id
     * @return
     */
    List<String> getOrgIdsByManagerId(String managerId);

    /**
     * 获取id值-选项的映射Map
     * @param orgIds
     * @return
     */
    Map<String, LabelValue> getLabelValueMap(List<String> orgIds);

    /**
     * 获取租户的根节点id
     * @param tenantId
     * @return
     */
    String getTenantRootOrgId(String tenantId);

}