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
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.config.Cons;
import com.diboot.iam.entity.IamResource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 前端资源权限 VO定义
 * @author yangzhao
 * @version 2.0.0
 * @date 2020-02-27
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamResourceListVO extends IamResource {

    private static final long serialVersionUID = 6643651522844488124L;

    // 字段关联：this.parent_id=id
    @BindField(entity = IamResource.class, field = "displayName", condition = "this.parent_id=id")
    private String parentDisplayName;

    // 枚举中获取状态label
    public String getStatusLabel() {
        return Cons.ENABLE_STATUS.getLabel(this.getStatus());
    }

    // 绑定iamResourceList
    @BindEntityList(entity = IamResource.class, condition = "this.id=parent_id AND display_type ='PERMISSION'")
    private List<IamResource> permissionList;

    private List<IamResourceListVO> children;
}
