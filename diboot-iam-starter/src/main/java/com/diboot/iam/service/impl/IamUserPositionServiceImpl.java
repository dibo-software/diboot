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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.mapper.IamUserPositionMapper;
import com.diboot.iam.service.IamUserPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户岗位关联相关Service实现
 *
 * @author wind
 * @version v2.6.0
 * @date 2022-06-23
 */
@Slf4j
@Service
public class IamUserPositionServiceImpl extends BaseServiceImpl<IamUserPositionMapper, IamUserPosition> implements IamUserPositionService {

    @Override
    public List<IamUserPosition> getUserPositionListByUser(String userType, String userId) {
        LambdaQueryWrapper<IamUserPosition> queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId)
                .orderByDesc(IamUserPosition::getIsPrimaryPosition);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<String> getPositionIdsByOrg(String orgId) {
        LambdaQueryWrapper<IamUserPosition> queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .select(IamUserPosition::getPositionId)
                .eq(IamUserPosition::getOrgId, orgId);
        List<IamUserPosition> userPositions = baseMapper.selectList(queryWrapper);
        if(V.notEmpty(userPositions)) {
            return userPositions.stream().map(IamUserPosition::getPositionId).distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public IamUserPosition getUserPrimaryPosition(String userType, String userId) {
        LambdaQueryWrapper<IamUserPosition> queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId)
                .eq(IamUserPosition::getIsPrimaryPosition, true);
        List<IamUserPosition> userPositionList = baseMapper.selectList(queryWrapper);
        if(V.isEmpty(userPositionList)){
            return null;
        }
        if(userPositionList.size() > 1){
            log.warn("用户 {}:{} 主岗多于1个，当前以第一个为准", userType, userId);
        }
        return userPositionList.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUserPositionRelations(String userType, String userId, List<IamUserPosition> userPositionList) {
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
        long count = baseMapper.selectCount(deleteWrapper);
        if (count > 0) {
            baseMapper.delete(deleteWrapper);
        }
        // 批量设置新的岗位列表
        if (V.isEmpty(userPositionList)) {
            return true;
        }
        for (IamUserPosition userPosition : userPositionList) {
            userPosition.setId(null);
            baseMapper.insert(userPosition);
        }
        return true;
    }

    @Override
    public List<String> getUserIdsByPosition(String orgId, List<String> positionIds) {
        LambdaQueryWrapper<IamUserPosition> queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .select(IamUserPosition::getUserId)
                .in(IamUserPosition::getPositionId, positionIds);
        if(V.notEmpty(orgId)) {
            queryWrapper.eq(IamUserPosition::getOrgId, orgId);
        }
        List<IamUserPosition> userPositions = baseMapper.selectList(queryWrapper);
        if(V.notEmpty(userPositions)) {
            return userPositions.stream().map(IamUserPosition::getUserId).distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
