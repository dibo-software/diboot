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
package com.diboot.iam.dto;

import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.entity.route.RouteMeta;
import com.diboot.iam.entity.route.RouteRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 前端资源权限 DTO定义
 * @author yangzhao
 * @version 2.0.0
 * @date 2020-02-27
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamResourcePermissionDTO extends IamResourcePermission {
    private static final long serialVersionUID = -7218371066111984841L;

    // 按钮/权限列表
    private List<IamResourcePermissionDTO> permissionList;
}
