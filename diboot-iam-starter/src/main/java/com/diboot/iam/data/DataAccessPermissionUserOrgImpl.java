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
package com.diboot.iam.data;

import com.diboot.core.config.Cons;
import com.diboot.core.data.access.DataAccessInterface;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 组织数据访问权限实现
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/02/15
 */
@Slf4j
public class DataAccessPermissionUserOrgImpl implements DataAccessInterface {

    @Override
    public List<Serializable> getAccessibleIds(Class<?> entityClass, String fieldName) {
        // 获取当前登录用户
        IamUser currentUser = null;
        try {
            currentUser = IamSecurityUtils.getCurrentUser();
        }
        catch (Exception e){
            log.warn("获取数据权限可访问ids异常: ", e);
            return Collections.emptyList();
        }
        if (currentUser == null) {
            return Collections.emptyList();
        }
        // 提取其可访问ids
        List<Serializable> accessibleIds = new ArrayList<>();
        if(Cons.FieldName.orgId.name().equals(fieldName)){
            //添加当前部门ID
            Long currentOrgId = currentUser.getOrgId();
            accessibleIds.add(currentOrgId);
            List<Long> childOrgIds = ContextHelper.getBean(IamOrgService.class).getChildOrgIds(currentOrgId);
            if(V.notEmpty(childOrgIds)){
                accessibleIds.addAll(childOrgIds);
            }
        }
        else if(Cons.FieldName.userId.name().equals(fieldName)){
            accessibleIds.add(currentUser.getId());
        }
        // ... 其他类型字段
        return accessibleIds;
    }
}
