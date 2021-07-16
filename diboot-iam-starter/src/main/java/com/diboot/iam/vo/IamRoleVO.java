/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.entity.IamRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
* 角色 VO定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Getter
@Setter
@Accessors(chain = true)
public class IamRoleVO extends IamRole {
    private static final long serialVersionUID = -6778550575399070076L;

    // 字段关联：this.id=iam_role_resource.role_id AND iam_role_resource.resource_id=id
    @BindEntityList(entity = IamResourcePermission.class, condition = "this.id=iam_role_resource.role_id AND iam_role_resource.resource_id=id")
    private List<IamResourcePermission> permissionList;

    private List<IamResourcePermissionListVO> permissionVOList;

    /***
     * 是否为超级管理员
     * @return
     */
    public boolean isSuperAdmin(){
        if (V.isEmpty(getCode())){
            return false;
        }
        return getCode().contains(Cons.ROLE_SUPER_ADMIN + ",") || getCode().contains("," + Cons.ROLE_SUPER_ADMIN) || Cons.ROLE_SUPER_ADMIN.equals(getCode());
    }

}