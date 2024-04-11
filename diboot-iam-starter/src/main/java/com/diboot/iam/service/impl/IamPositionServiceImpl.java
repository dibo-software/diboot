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
package com.diboot.iam.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.mapper.IamPositionMapper;
import com.diboot.iam.service.IamPositionService;
import com.diboot.iam.service.IamUserPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 岗位相关Service实现
 *
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2019-12-03
 */
@Service
@Slf4j
public class IamPositionServiceImpl extends BaseServiceImpl<IamPositionMapper, IamPosition> implements IamPositionService {

    @Autowired
    private IamUserPositionService iamUserPositionService;

    @Override
    public List<IamPosition> getPositionsByUser(String userType, String userId) {
        List<IamUserPosition> userPositionList = iamUserPositionService.getUserPositionListByUser(userType, userId);
        List<String> positionIds = BeanUtils.collectToList(userPositionList, IamUserPosition::getPositionId);
        return V.isEmpty(positionIds) ? Collections.emptyList() : this.getEntityListByIds(positionIds);
    }

    @Override
    public List<IamPosition> getPositionsByOrg(String orgId) {
        List<String> positionIds = iamUserPositionService.getPositionIdsByOrg(orgId);
        return V.isEmpty(positionIds) ? Collections.emptyList() : this.getEntityListByIds(positionIds);
    }

}
