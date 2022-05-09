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

import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamUserPosition;

import java.util.List;

/**
* 岗位相关Service
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
public interface IamPositionService extends BaseIamService<IamPosition> {

    /**
     * 获取当前用户的任职岗位列表
     * @param userType
     * @param userId
     * @return
     */
    List<IamUserPosition> getUserPositionListByUser(String userType, Long userId);

    /**
     * 获取用户的第一主岗
     * @param userType
     * @param userId
     * @return
     */
    IamUserPosition getUserPrimaryPosition(String userType, Long userId);

    /***
     * 通过用户ID获取用户的所有任职岗位集合
     * @param userType
     * @param userId
     * @return
     */
    List<IamPosition> getPositionListByUser(String userType, Long userId);

    /***
     * 批量更新用户-岗位的关联关系
     * @param userType
     * @param userId
     * @param userPositionList
     * @return
     */
    boolean updateUserPositionRelations(String userType, Long userId, List<IamUserPosition> userPositionList);

}