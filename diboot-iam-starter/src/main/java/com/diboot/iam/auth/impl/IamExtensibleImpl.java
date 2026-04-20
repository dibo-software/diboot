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
package com.diboot.iam.auth.impl;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.service.IamPositionService;
import com.diboot.iam.service.IamUserPositionService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.vo.IamUserPositionVO;
import com.diboot.iam.vo.PositionDataScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * IAM扩展配置
 *
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/9
 * Copyright © diboot.com
 */
@Slf4j
public class IamExtensibleImpl implements IamExtensible {

    @Override
    public LabelValue getUserExtensionObj(String userType, String userId, Map<String, Object> extObj) {
        if (!IamUser.class.getSimpleName().equals(userType)) {
            log.warn("扩展的用户类型: {} 需自行实现附加扩展对象逻辑", userType);
            return null;
        }
        IamUserPositionService iamPositionService = ContextHolder.getBean(IamUserPositionService.class);
        List<IamUserPositionVO> userPositionVOList = iamPositionService.getUserPositions(userType, userId);
        if (V.isEmpty(userPositionVOList)) {
            return null;
        }
        IamUserPositionVO userPosition = userPositionVOList.stream().filter(IamUserPosition::getIsPrimaryPosition).findFirst().orElse(null);
        if (userPosition == null) {
            return null;
        }
        // 构建主岗数据范围
        PositionDataScope positionDataScope = buildPrimaryPositionDataScope(userId, userPosition.getOrgId(), userPosition);
        LabelValue primaryPositionLabelValue = new LabelValue(userPosition.getPositionName(), userPosition.getPositionCode()).setExt(positionDataScope);
        // 处理兼职岗
        List<IamUserPositionVO> partTimePositions = userPositionVOList.stream().filter(p->!p.getIsPrimaryPosition()).collect(Collectors.toList());
        if(V.notEmpty(partTimePositions)){
            List<LabelValue> partTimePositionLabelValues = processPartTimePositions(partTimePositions, userId, userPosition.getOrgId(), positionDataScope, primaryPositionLabelValue);
            primaryPositionLabelValue.setChildren(partTimePositionLabelValues);
        }
        // 返回主岗信息
        return primaryPositionLabelValue;
    }

    @Override
    public List<IamRole> getExtensionRoles(String userType, String userId, String extensionObjId) {
        return null;
    }

    /**
     * 构建主岗数据范围
     *
     * @param userId
     * @param orgId
     * @param userPosition
     * @return
     */
    private PositionDataScope buildPrimaryPositionDataScope(String userId, String orgId, IamUserPositionVO userPosition) {
        PositionDataScope dataScope = new PositionDataScope(userPosition.getPositionId(), userPosition.getDataPermissionType(), userId, orgId, userPosition.getOrgName());
        // 本人及下属的用户ids
        Set<String> accessibleUserIds = new LinkedHashSet<>();
        accessibleUserIds.add(userId);
        List<String> subordinateIds = ContextHolder.getBean(IamUserService.class).getUserIdsByManagerId(userId);
        if (V.notEmpty(subordinateIds)) {
            accessibleUserIds.addAll(subordinateIds);
        }
        dataScope.setAccessibleUserIds(new ArrayList<>(accessibleUserIds));
        // 设置可访问组织ID（本部门及下属部门）
        Set<String> accessibleOrgIds = new LinkedHashSet<>();
        accessibleOrgIds.add(orgId);
        List<String> childOrgIds = ContextHolder.getBean(IamOrgService.class).getChildOrgIds(orgId);
        if (V.notEmpty(childOrgIds)) {
            accessibleOrgIds.addAll(childOrgIds);
        }
        dataScope.setAccessibleOrgIds(new ArrayList<>(accessibleOrgIds));
        return dataScope;
    }

    /**
     * 处理兼职岗
     *
     * @param partTimePositions        兼职岗位
     * @param userId
     * @param primaryOrgId             主岗组织id
     * @param primaryPositionDataScope 主岗权限
     * @param primaryPositionLabelValue 主岗
     * @return
     */
    private List<LabelValue> processPartTimePositions(List<IamUserPositionVO> partTimePositions,
                                                      String userId, String primaryOrgId,
                                                      PositionDataScope primaryPositionDataScope, LabelValue primaryPositionLabelValue) {
        List<LabelValue> result = new ArrayList<>();
        // 克隆主岗
        result.add(BeanUtils.cloneBean(primaryPositionLabelValue));
        if (V.isEmpty(partTimePositions)) {
            return result;
        }
        IamOrgService orgService = ContextHolder.getBean(IamOrgService.class);
        for (IamUserPositionVO partTimePosition : partTimePositions) {
            String partTimeOrgId = partTimePosition.getOrgId();
            PositionDataScope partTimeDataScope = new PositionDataScope(
                    partTimePosition.getPositionId(), partTimePosition.getDataPermissionType(), userId, partTimeOrgId, partTimePosition.getOrgName());
            // 设置可访问用户ID（复用主岗数据）
            partTimeDataScope.setAccessibleUserIds(primaryPositionDataScope.getAccessibleUserIds());
            // 设置可访问组织ID， 主岗兼职岗相等的情况下，复用主岗的组织范围；否则单独查询兼职岗位的组织范围
            if (primaryOrgId.equals(partTimeOrgId)) {
                partTimeDataScope.setAccessibleOrgIds(primaryPositionDataScope.getAccessibleOrgIds());
            } else {
                Set<String> orgIds = new LinkedHashSet<>();
                orgIds.add(partTimeOrgId);
                List<String> childOrgIds = orgService.getChildOrgIds(partTimeOrgId);
                if (V.notEmpty(childOrgIds)) {
                    orgIds.addAll(childOrgIds);
                }
                partTimeDataScope.setAccessibleOrgIds(new ArrayList<>(orgIds));
            }
            result.add(new LabelValue(partTimePosition.getPositionName(), partTimePosition.getPositionCode()).setExt(partTimeDataScope));
        }

        return result;
    }
}