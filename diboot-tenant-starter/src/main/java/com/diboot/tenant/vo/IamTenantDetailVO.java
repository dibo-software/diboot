/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.iam.entity.IamUser;
import com.diboot.tenant.entity.IamTenant;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 租户 DetailVO定义
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamTenantDetailVO extends IamTenantListVO {
    private static final long serialVersionUID = -1741724363993208360L;

}
