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

import com.diboot.core.data.access.DataAccessInterface;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.util.IamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class DataAccessPermissionOrgImpl implements DataAccessInterface {

    @Autowired
    private IamOrgService iamOrgService;

    @Override
    public List<Serializable> getAccessibleIds() {
        Long OrgId; //当前部门ID
        try {
            IamUser currentUser =  IamSecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return Collections.emptyList();
            }
            OrgId = currentUser.getOrgId();
        } catch (Exception e){
            return Collections.emptyList();
        }
        List<Serializable> orgIds = new ArrayList<>();
        orgIds.add(OrgId);
        List<Long> childOrgIds = iamOrgService.getChildOrgIds(OrgId);
        if(V.notEmpty(childOrgIds)){
            orgIds.addAll(childOrgIds);
        }
        return orgIds;
    }
}
