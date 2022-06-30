/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 岗位相关的数据范围
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/9
 * Copyright © diboot.com
 */
@Getter @Setter
public class PositionDataScope implements Serializable {
    private static final long serialVersionUID = 3374139242516436636L;

    public PositionDataScope(){}

    public PositionDataScope(Long positionId, String dataPermissionType, Long userId, Long orgId){
        this.positionId = positionId;
        this.dataPermissionType = dataPermissionType;
        this.userId = userId;
        this.orgId = orgId;
    }

    /**
     * 岗位id
     */
    private Long positionId;

    /**
     * 数据权限范围
     */
    private String dataPermissionType;

    /**
     * 当前部门id
     */
    private Long orgId;

    /**
     * 当前及子级别部门ids
     */
    private List<? extends Serializable> accessibleOrgIds;

    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * 当前及子级用户ids
     */
    private List<? extends Serializable> accessibleUserIds;
}
