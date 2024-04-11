/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.iam.entity.IamUserPosition;

import java.util.List;

/**
 * 用户岗位关联相关Service
 *
 * @author wind
 * @version v2.6.0
 * @date 2022-06-23
 */
public interface IamUserPositionService extends BaseService<IamUserPosition> {

    /**
     * 获取当前用户的任职岗位列表
     *
     * @param userType
     * @param userId
     * @return
     */
    List<IamUserPosition> getUserPositionListByUser(String userType, String userId);

    /**
     * 获取指定部门下的岗位列表
     *
     * @return
     */
    List<String> getPositionIdsByOrg(String orgId);

    /**
     * 获取用户的第一主岗
     *
     * @param userType
     * @param userId
     * @return
     */
    IamUserPosition getUserPrimaryPosition(String userType, String userId);

    /**
     * 批量更新用户-岗位的关联关系
     *
     * @param userType
     * @param userId
     * @param userPositionList
     * @return
     */
    boolean updateUserPositionRelations(String userType, String userId, List<IamUserPosition> userPositionList);

    /**
     * 获取岗位下匹配的用户
     * @param positionIds
     * @return
     */
    List<String> getUserIdsByPosition(String orgId, List<String> positionIds);
}