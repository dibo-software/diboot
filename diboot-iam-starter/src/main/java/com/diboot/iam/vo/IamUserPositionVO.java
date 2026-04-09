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
package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindField;
import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamUserPosition;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用户岗位VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamUserPositionVO extends IamUserPosition {

    @BindField(entity = IamOrg.class, field = "name", condition = "this.org_id = id")
    private String orgName;

    @BindField(entity = IamPosition.class, field = "name", condition = "this.position_id = id")
    private String positionName;

    private String positionCode;
    private String dataPermissionType;
}
