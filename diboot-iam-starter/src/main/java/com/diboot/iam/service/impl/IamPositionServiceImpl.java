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
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.mapper.IamPositionMapper;
import com.diboot.iam.mapper.IamUserPositionMapper;
import com.diboot.iam.service.IamPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class IamPositionServiceImpl extends BaseIamServiceImpl<IamPositionMapper, IamPosition> implements IamPositionService {

    @Autowired
    private IamUserPositionMapper iamUserPositionMapper;

    @Override
    public List<IamUserPosition> getUserPositionListByUser(String userType, Long userId) {
        LambdaQueryWrapper<IamUserPosition> queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId).orderByDesc(IamUserPosition::getIsPrimaryPosition);
        List<IamUserPosition> userPositionList = iamUserPositionMapper.selectList(queryWrapper);
        return userPositionList;
    }

    @Override
    public IamUserPosition getUserPrimaryPosition(String userType, Long userId) {
        LambdaQueryWrapper<IamUserPosition> queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId)
                .eq(IamUserPosition::getIsPrimaryPosition, true);
        List<IamUserPosition> userPositionList = iamUserPositionMapper.selectList(queryWrapper);
        if(V.isEmpty(userPositionList)){
            return null;
        }
        if(userPositionList.size() > 1){
            log.warn("用户 {}:{} 主岗多于1个，当前以第一个为准", userType, userId);
        }
        return userPositionList.get(0);
    }

    @Override
    public List<IamPosition> getPositionListByUser(String userType, Long userId) {
        // 根据user与position的关联获取positionId列表
        LambdaQueryWrapper<IamUserPosition> queryWrapper = Wrappers.<IamUserPosition>lambdaQuery()
                .select(IamUserPosition::getPositionId)
                .eq(IamUserPosition::getUserType, userType)
                .eq(IamUserPosition::getUserId, userId).orderByDesc(IamUserPosition::getIsPrimaryPosition);
        List<IamUserPosition> userPositionList = iamUserPositionMapper.selectList(queryWrapper);
        List<Long> positionIds = BeanUtils.collectToList(userPositionList, IamUserPosition::getPositionId);
        return V.isEmpty(positionIds) ? Collections.emptyList() : this.getEntityListByIds(positionIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUserPositionRelations(String userType, Long userId, List<IamUserPosition> userPositionList) {
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
        long count = iamUserPositionMapper.selectCount(deleteWrapper);
        if (count > 0) {
            iamUserPositionMapper.delete(deleteWrapper);
        }
        // 批量设置新的岗位列表
        if (V.isEmpty(userPositionList)) {
            return true;
        }
        for (IamUserPosition userPosition : userPositionList) {
            userPosition.setId(null);
            iamUserPositionMapper.insert(userPosition);
        }
        return true;
    }
}
