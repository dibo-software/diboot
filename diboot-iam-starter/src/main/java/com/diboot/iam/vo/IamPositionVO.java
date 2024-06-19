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
package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.entity.IamPosition;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 岗位 VO定义
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2019-12-03
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamPositionVO extends IamPosition {
    private static final long serialVersionUID = 3511407861677398030L;

    // 字典关联
    @BindDict(type="DATA_PERMISSION_TYPE", field = "dataPermissionType")
    private LabelValue dataPermissionTypeLabel;

}