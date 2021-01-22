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

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.iam.entity.IamPosition;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 系统用户 VO定义
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-17
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamUserOrgVO extends IamUserVO {
    private static final long serialVersionUID = -8154734016521065051L;

    @BindEntityList(entity = IamPosition.class, condition="this.id=iam_user_position.user_id AND iam_user_position.position_id=id")
    private List<IamPosition> positionList;
}