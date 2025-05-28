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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.entity.IamGroup;
import com.diboot.iam.mapper.IamGroupMapper;
import com.diboot.iam.service.IamGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户组相关Service实现
 *
 * @version 3.6.1
 * @date 2025/05/28
 */
@Slf4j
@Service
public class IamGroupServiceImpl extends BaseServiceImpl<IamGroupMapper, IamGroup> implements IamGroupService {

    @Override
    public List<String> getUserIdsByGroup(List<String> groupIds) {
        LambdaQueryWrapper<IamGroup> queryWrapper = new LambdaQueryWrapper<IamGroup>()
                .select(IamGroup::getMembers)
                .in(IamGroup::getId, groupIds);
        List<IamGroup> groupList = getEntityList(queryWrapper);
        if (V.notEmpty(groupList)) {
            List<String> groupMembers = new ArrayList<>();
            for (IamGroup group : groupList) {
                groupMembers.addAll(group.getMembers());
            }
            return groupMembers.stream().distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<LabelValue> getGroupListByIds(List<String> groupIds) {
        LambdaQueryWrapper<IamGroup> queryWrapper = new LambdaQueryWrapper<IamGroup>()
                .select(IamGroup::getName, IamGroup::getId)
                .in(IamGroup::getId, groupIds);
        List<LabelValue> groupList = getLabelValueList(queryWrapper);
        return groupList;
    }

}
