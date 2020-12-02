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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import com.diboot.iam.entity.*;
import com.diboot.iam.mapper.IamUserPositionMapper;
import com.diboot.iam.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* 用户岗位关联相关Service实现
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamUserPositionServiceImpl extends BaseIamServiceImpl<IamUserPositionMapper, IamUserPosition> implements IamUserPositionService {

    @Autowired
    private IamPositionService iamPositionService;

    @Override
    public List<IamPosition> getUserPositionList(String userType, Long userId) {
        LambdaQueryWrapper queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId).orderByDesc(IamUserPosition::getIsPrimaryPosition);
        List<Long> positionIds = getValuesOfField(queryWrapper, IamUserPosition::getPositionId);
        if(V.isEmpty(positionIds)){
            return Collections.emptyList();
        }
        return iamPositionService.getEntityListByIds(positionIds);
    }

    @Override
    public List<IamPosition> getAllPositionListByUser(String userType, Long userId, Long orgId) {
        List<Long> positionIdList = this.getAllPositionIdListByUser(userType, userId,orgId);
        if (V.isEmpty(positionIdList)) {
            return Collections.emptyList();
        }
        return iamPositionService.getEntityListByIds(positionIdList);
    }

    @Override
    public List<KeyValue> getAllPositionKvListByUser(String userType, Long userId, Long orgId) {
        List<Long> positionIdList = this.getAllPositionIdListByUser(userType, userId,orgId);
        if (V.isEmpty(positionIdList)) {
            return Collections.emptyList();
        }
        return iamPositionService.getKeyValueList(
                Wrappers.<IamPosition>lambdaQuery()
                        .select(IamPosition::getName, IamPosition::getId)
                        .in(IamPosition::getId, positionIdList)
        );
    }

    @Override
    public List<Long> getAllPositionIdListByUser(String userType, Long userId, Long orgId) {
        List<Long> positionIdList = new ArrayList<>();
        // 根据user与position的关联获取positionId列表
        List<Long> positionIdsByUser = super.getValuesOfField(Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId).orderByDesc(IamUserPosition::getIsPrimaryPosition), IamUserPosition::getPositionId);
        if (V.notEmpty(positionIdsByUser)) {
            positionIdList.addAll(positionIdsByUser);
        }
        if (V.isEmpty(positionIdList)) {
            return Collections.emptyList();
        }
        // 对岗位ID列表去重
        return positionIdList.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public IamPosition getUserPosition(String userType, Long userId, Long orgId, Long positionId) {
        List<Long> positionIdList = this.getAllPositionIdListByUser(userType, userId, orgId);
        // 验证当前岗位是否存在
        if (V.isEmpty(positionIdList) || !positionIdList.contains(positionId)) {
            return null;
        }
        return iamPositionService.getEntity(positionId);
    }

    @Override
    public IamPosition getUserPosition(String userType, Long userId, Long positionId) {
        LambdaQueryWrapper queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getPositionId, positionId)
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId);
        if(exists(queryWrapper)){
            return iamPositionService.getEntity(positionId);
        }
        return null;
    }

    @Override
    public boolean updateUserPositionRelations(String userType, Long userId,  List<IamUserPosition> userPositionList) {
        if (V.isEmpty(userType) || V.isEmpty(userId)) {
            throw new BusinessException(Status.FAIL_OPERATION, "参数错误");
        }
        // 校验用户ID是否存在
        if (V.notEmpty(userPositionList)) {
            for (IamUserPosition userPosition : userPositionList) {
                userPosition.setUserType(userType);
                userPosition.setUserId(userId);
            }
        }
        // 删除所有旧关联数据
        LambdaQueryWrapper deleteWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId);
        int count = this.getEntityListCount(deleteWrapper);
        if (count > 0) {
            this.deleteEntities(deleteWrapper);
        }
        // 批量设置新的岗位列表
        if (V.isEmpty(userPositionList)) {
            return true;
        }
        this.saveBatch(userPositionList);
        return true;
    }

}
