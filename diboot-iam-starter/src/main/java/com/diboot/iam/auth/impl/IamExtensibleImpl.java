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
package com.diboot.iam.auth.impl;

import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.service.IamPositionService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.vo.PositionDataScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IAM扩展配置
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/9
 * Copyright © diboot.com
 */
@Slf4j
@Service
public class IamExtensibleImpl implements IamExtensible {

    @Override
    public LabelValue getUserExtensionObj(String userType, Long userId, Map<String, Object> extObj) {
        if(!IamUser.class.getSimpleName().equals(userType)){
            log.warn("扩展的用户类型: {} 需自行实现附加扩展对象逻辑", userType);
            return null;
        }
        IamPositionService iamPositionService = ContextHelper.getBean(IamPositionService.class);
        IamUserPosition userPosition = iamPositionService.getUserPrimaryPosition(userType, userId);
        if(userPosition != null){
            Long orgId = userPosition.getOrgId();
            IamPosition position = iamPositionService.getEntity(userPosition.getPositionId());
            PositionDataScope positionDataScope = new PositionDataScope(userId, position.getDataPermissionType(), userId, orgId);
            List<Long> accessibleUserIds = new ArrayList<>(), accessibleOrgIds = new ArrayList<>();
            // 本人及下属的用户ids
            accessibleUserIds.add(userId);
            List<Long> userIds = ContextHelper.getBean(IamUserService.class).getUserIdsByManagerId(userId);
            if(V.notEmpty(userIds)){
                userIds.forEach(uid -> {
                    if(!accessibleUserIds.contains(uid)){
                        accessibleUserIds.add(uid);
                    }
                });
            }
            positionDataScope.setAccessibleUserIds(accessibleUserIds);
            // 本部门及下属部门的ids
            accessibleOrgIds.add(orgId);
            List<Long> childOrgIds = ContextHelper.getBean(IamOrgService.class).getChildOrgIds(orgId);
            if(V.notEmpty(childOrgIds)){
                childOrgIds.forEach(oid -> {
                    if(!accessibleOrgIds.contains(oid)){
                        accessibleOrgIds.add(oid);
                    }
                });
            }
            positionDataScope.setAccessibleOrgIds(accessibleOrgIds);
            return new LabelValue(position.getName(), position.getCode()).setExt(positionDataScope);
        }
        return null;
    }

    @Override
    public List<IamRole> getExtensionRoles(String userType, Long userId, Long extensionObjId) {
        return null;
    }
}