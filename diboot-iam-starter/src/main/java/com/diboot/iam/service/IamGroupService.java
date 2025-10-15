/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.iam.entity.IamGroup;

import java.util.List;
import java.util.Map;

/**
 * 用户组相关Service
 *
 * @version 3.6.1
 * @date 2025/05/28
 */
public interface IamGroupService extends BaseService<IamGroup> {

    /**
     * 获取指定用户组下的成员ids
     * @param groupIds
     * @return
     */
    List<String> getUserIdsByGroup(List<String> groupIds);

    /**
     * 获取指定用户组的id-名称列表
     * @param groupIds
     * @return
     */
    List<LabelValue> getGroupListByIds(List<String> groupIds);

    /**
     * 获取用户ids所属的组
     * @param userIds
     * @return
     */
    List<IamGroup> getGroupListByUserIds(List<String> userIds);

    /**
     * 根据组/团队ids获取对应的用户id及名称
     * @param groupIds
     * @return
     */
    Map<String, List<LabelValue>> getGroupUsersMap(List<String> groupIds);

}
