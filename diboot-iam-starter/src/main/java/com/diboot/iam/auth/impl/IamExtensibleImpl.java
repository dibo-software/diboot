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

/**
 * IAM扩展配置
 *
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/9
 * Copyright © diboot.com
 */
@Slf4j
@Service
public class IamExtensibleImpl implements IamExtensible {

    @Override
    public LabelValue getUserExtensionObj(String userType, String userId, Map<String, Object> extObj) {
        if (!IamUser.class.getSimpleName().equals(userType)) {
            log.warn("扩展的用户类型: {} 需自行实现附加扩展对象逻辑", userType);
            return null;
        }
        IamUserPositionService iamPositionService = ContextHolder.getBean(IamUserPositionService.class);
        IamUserPositionVO userPosition = iamPositionService.getUserPrimaryPosition(userType, userId);
        if (userPosition == null) {
            return null;
        }
        // 获取主岗信息
        String orgId = userPosition.getOrgId();
        IamPosition position = ContextHolder.getBean(IamPositionService.class).getEntity(userPosition.getPositionId());
        // 构建主岗数据范围
        PositionDataScope positionDataScope = buildPrimaryPositionDataScope(userId, orgId, position, userPosition);
        LabelValue primaryPositionLabelValue = new LabelValue(position.getName(), position.getCode()).setExt(positionDataScope);
        // 处理兼职岗
        List<IamUserPositionVO> partTimePositions = iamPositionService.getUserPartTimeJobPosition(userType, userId);
        List<LabelValue> partTimePositionLabelValues = processPartTimePositions(partTimePositions, userId, orgId, positionDataScope, primaryPositionLabelValue);
        // 返回主岗信息
        return primaryPositionLabelValue.setChildren(partTimePositionLabelValues);
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
     * @param position
     * @return
     */
    private PositionDataScope buildPrimaryPositionDataScope(String userId, String orgId, IamPosition position, IamUserPositionVO userPosition) {
        PositionDataScope dataScope = new PositionDataScope(position.getId(), position.getDataPermissionType(), userId, orgId, userPosition.getOrgName());
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
        IamPositionService positionService = ContextHolder.getBean(IamPositionService.class);
        IamOrgService orgService = ContextHolder.getBean(IamOrgService.class);

        for (IamUserPositionVO partTimePosition : partTimePositions) {
            String partTimeOrgId = partTimePosition.getOrgId();
            IamPosition position = positionService.getEntity(partTimePosition.getPositionId());

            PositionDataScope partTimeDataScope = new PositionDataScope(
                    position.getId(), position.getDataPermissionType(), userId, partTimeOrgId, partTimePosition.getOrgName());

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

            result.add(new LabelValue(position.getName(), position.getCode()).setExt(partTimeDataScope));
        }

        return result;
    }
}