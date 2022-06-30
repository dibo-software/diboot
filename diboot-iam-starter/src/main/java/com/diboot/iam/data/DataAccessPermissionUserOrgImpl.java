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
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.PositionDataScope;
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
    public List<? extends Serializable> getAccessibleIds(Class<?> entityClass, String fieldName) {
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
            log.warn("无法获取当前用户");
            return Collections.emptyList();
        }
        // 获取用户岗位对应的数据权限
        LabelValue extensionObj = currentUser.getExtensionObj();
        if(extensionObj == null || extensionObj.getExt() == null){
            // 提取其可访问ids
            if(isOrgFieldName(fieldName)){
                return buildOrgIdsScope(currentUser);
            }
            else if(isUserFieldName(fieldName)){
                return buildUserIdsScope(currentUser);
            }
            else{
                log.warn("数据权限未能识别该字段类型: {}", fieldName);
                return Collections.emptyList();
            }
        }
        // 处理岗位对应的数据范围权限
        PositionDataScope positionDataScope = (PositionDataScope)extensionObj.getExt();
        // 可看全部数据，不拦截
        if(Cons.DICTCODE_DATA_PERMISSION_TYPE.ALL.name().equalsIgnoreCase(positionDataScope.getDataPermissionType())){
            return null;
        }
        // 本人数据
        else if(Cons.DICTCODE_DATA_PERMISSION_TYPE.SELF.name().equalsIgnoreCase(positionDataScope.getDataPermissionType())){
            if(isUserFieldName(fieldName)){
                return buildUserIdsScope(currentUser);
            }
            else{// 忽略无关字段
                return null;
            }
        }
        // 按user过滤，本人及下属
        else if(Cons.DICTCODE_DATA_PERMISSION_TYPE.SELF_AND_SUB.name().equalsIgnoreCase(positionDataScope.getDataPermissionType())){
            if(isUserFieldName(fieldName)){
                return positionDataScope.getAccessibleUserIds();
            }
            else{// 忽略无关字段
                return null;
            }
        }
        // 按部门过滤，本部门
        else if(Cons.DICTCODE_DATA_PERMISSION_TYPE.DEPT.name().equalsIgnoreCase(positionDataScope.getDataPermissionType())){
            if(isOrgFieldName(fieldName)){
                List<Long> accessibleIds = new ArrayList<>(1);
                accessibleIds.add(positionDataScope.getOrgId());
                return accessibleIds;
            }
            else{// 忽略无关字段
                return null;
            }
        }
        // 按部门过滤，本部门及下属部门
        else if(Cons.DICTCODE_DATA_PERMISSION_TYPE.DEPT_AND_SUB.name().equalsIgnoreCase(positionDataScope.getDataPermissionType())){
            if(isOrgFieldName(fieldName)){
                return positionDataScope.getAccessibleOrgIds();
            }
            else{// 忽略无关字段
                return null;
            }
        }
        else{
            log.warn("未知的数据权限类型: {}", positionDataScope.getDataPermissionType());
            return Collections.emptyList();
        }
    }

    /**
     * 未配置数据权限时的默认可见自己的
     * @param currentUser
     * @return
     */
    protected List<? extends Serializable> buildUserIdsScope(IamUser currentUser){
        List<Serializable> accessibleIds = new ArrayList<>(1);
        accessibleIds.add(currentUser.getId());
        return accessibleIds;
    }

    /**
     * 未配置数据权限时的默认本部门
     * @param currentUser
     * @return
     */
    protected List<? extends Serializable> buildOrgIdsScope(IamUser currentUser){
        List<Serializable> accessibleIds = new ArrayList<>();
        accessibleIds.add(currentUser.getOrgId());
        /*List<Long> childOrgIds = ContextHelper.getBean(IamOrgService.class).getChildOrgIds(currentUser.getOrgId());
        if(V.notEmpty(childOrgIds)){
            accessibleIds.addAll(childOrgIds);
        }*/
        return accessibleIds;
    }

    /**
     * 是否为可支持的用户字段
     * @param fieldName
     * @return
     */
    protected boolean isUserFieldName(String fieldName){
        return (Cons.FieldName.userId.name().equals(fieldName) || Cons.FieldName.createBy.name().equals(fieldName));
    }

    /**
     * 是否为可支持的部门字段
     * @param fieldName
     * @return
     */
    protected boolean isOrgFieldName(String fieldName){
        return Cons.FieldName.orgId.name().equals(fieldName);
    }

}
