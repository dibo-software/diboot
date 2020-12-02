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

import com.diboot.core.vo.KeyValue;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamUserPosition;

import java.util.List;

/**
* 用户岗位关联相关Service
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-17
*/
public interface IamUserPositionService extends BaseIamService<IamUserPosition> {

    /**
     * 获取用户关联的任职岗位集合
     * @param userType 用户类型Class
     * @param userId 用户id
     * @return
     */
    List<IamPosition> getUserPositionList(String userType, Long userId);

    /***
     * 通过用户ID获取用户的所有任职岗位集合（包含了部门的岗位）
     * @param userType
     * @param userId
     * @return
     */
    List<IamPosition> getAllPositionListByUser(String userType, Long userId, Long orgId);

    /***
     * 通过用户ID获取用户的所有任职岗位Kv集合（包含了部门的岗位）
     * @param userType
     * @param userId
     * @return
     */
    List<KeyValue> getAllPositionKvListByUser(String userType, Long userId, Long orgId);

    /***
     * 通过用户ID获取用户的所有任职岗位ID集合（包含了部门的岗位）
     * @param userType
     * @param userId
     * @return
     */
    List<Long> getAllPositionIdListByUser(String userType, Long userId, Long orgId);


    /**
     * 获取用户的指定岗位
     * @param userType 用户类型Class
     * @param userId 用户id
     * @param positionId 岗位ID
     * @param orgId 组织ID
     * @return
     */
    IamPosition getUserPosition(String userType, Long userId, Long orgId, Long positionId);

    /**
     * 获取用户的指定岗位
     * @param userType 用户类型Class
     * @param userId 用户id
     * @param positionId 岗位ID
     * @return
     */
    IamPosition getUserPosition(String userType, Long userId, Long positionId);

    /***
     * 批量更新用户-岗位的关联关系
     * @param userType
     * @param userId
     * @param userPositionList
     * @return
     */
    boolean updateUserPositionRelations(String userType, Long userId, List<IamUserPosition> userPositionList);

}