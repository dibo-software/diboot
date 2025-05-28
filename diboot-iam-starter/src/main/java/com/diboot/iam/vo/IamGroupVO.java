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

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.entity.IamGroup;
import com.diboot.iam.entity.IamUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * 用户组 VO定义
 *
 * @version 3.6.1
 * @date 2025/05/28
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamGroupVO extends IamGroup {
    @Serial
    private static final long serialVersionUID = 3511203861636398030L;

    // 字典关联
    @BindFieldList(entity = IamUser.class, condition = "this.members = id", field = "realname")
    private List<String> membersLabel;

}
